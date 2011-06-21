package org.modelmapper.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.modelmapper.TypeMap;
import org.modelmapper.config.Configuration;
import org.modelmapper.internal.converter.ConverterStore;
import org.modelmapper.internal.util.Primitives;
import org.modelmapper.internal.util.Stack;
import org.modelmapper.internal.util.Strings;
import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.MatchingStrategy;
import org.modelmapper.spi.NameableType;
import org.modelmapper.spi.PropertyInfo;

/**
 * Builds implicit property mappings for a TypeMap.
 * 
 * @param <S> source type
 * @param <D> destination type
 * 
 * @author Jonathan Halterman
 */
class PropertyMappingBuilder<S, D> {
  private final TypeMapImpl<S, D> typeMap;
  private final TypeInfo<S> sourceTypeInfo;
  private final TypeMapStore typeMapStore;
  private final Configuration configuration;
  private final ConverterStore typeConverterStore;
  private final MatchingStrategy matchingStrategy;

  /** Mutable state */
  private final Errors errors = new Errors();
  private final PropertyNameInfoImpl propertyNameInfo;
  private final Stack<Class<?>> sourceTypes = new Stack<Class<?>>();
  private final List<PropertyMappingImpl> mappings = new ArrayList<PropertyMappingImpl>();

  PropertyMappingBuilder(TypeMapImpl<S, D> typeMap, TypeMapStore typeMapStore,
      ConverterStore converterStore) {
    this.typeMap = typeMap;
    this.typeConverterStore = converterStore;
    this.typeMapStore = typeMapStore;
    this.configuration = typeMap.configuration;
    sourceTypeInfo = TypeInfoRegistry.typeInfoFor(typeMap.getSourceType(), configuration);
    matchingStrategy = configuration.getMatchingStrategy();
    propertyNameInfo = new PropertyNameInfoImpl(typeMap.getSourceType(), configuration);
  }

  void build() {
    process(TypeInfoRegistry.typeInfoFor(typeMap.getDestinationType(), configuration));
  }

  private void process(TypeInfo<?> destinationTypeInfo) {
    for (Map.Entry<String, Mutator> entry : destinationTypeInfo.getMutators().entrySet()) {
      Mutator mutator = entry.getValue();
      propertyNameInfo.pushDestination(entry.getKey(), entry.getValue());

      // Skip explicit mappings
      if (!typeMap.isMapped(Strings.join(propertyNameInfo.destinationProperties))) {
        buildMappings(sourceTypeInfo, mutator);
        propertyNameInfo.clearSource();
        sourceTypes.clear();
      }

      if (!mappings.isEmpty()) {
        if (mappings.size() == 1) {
          typeMap.addMapping(mappings.get(0));
        } else {
          MappingImpl mapping = disambiguateMappings();
          if (mapping != null)
            typeMap.addMapping(mapping);
          else if (!configuration.isAmbiguityIgnored())
            errors.ambiguousDestination(mutator, mappings);
        }

        mappings.clear();
      } else {
        TypeMap<?, ?> destinationMap = typeMapStore.get(typeMap.getSourceType(), mutator.getType());
        if (destinationMap == null) {
          process(TypeInfoRegistry.typeInfoFor(mutator.getType(), configuration));
        } else {
          mergeMappings(destinationMap);
        }
      }

      propertyNameInfo.popDestination();
    }

    errors.throwConfigurationExceptionIfErrorsExist();
  }

  /**
   * Matches a source accessor hierarchy to the {@code destinationMutator}.
   */
  private void buildMappings(TypeInfo<?> sourceTypeInfo, Mutator destinationMutator) {
    sourceTypes.push(sourceTypeInfo.getType());

    for (Map.Entry<String, Accessor> entry : sourceTypeInfo.getAccessors().entrySet()) {
      Accessor accessor = entry.getValue();
      propertyNameInfo.pushSource(entry.getKey(), entry.getValue());

      if (matchingStrategy.matches(propertyNameInfo)
          && typeConverterStore.getFirstSupported(accessor.getType(), destinationMutator.getType()) != null) {
        mappings.add(new PropertyMappingImpl(propertyNameInfo.sourceProperties,
            propertyNameInfo.destinationProperties));

        if (matchingStrategy.isExact())
          return;
      }

      if (isRecursivelyMatchable(accessor.getType()))
        buildMappings(TypeInfoRegistry.typeInfoFor(accessor.getType(), configuration),
            destinationMutator);

      propertyNameInfo.popSource();
    }

    sourceTypes.pop();
  }

  /**
   * Disambiguates the captured mappings by looking for the mapping with property tokens that most
   * closely match the destination. Match closeness is calculated as the total number of matched
   * source and destination tokens / the total number of source and destination tokens. Currently
   * this algorithm does not consider class name tokens.
   * 
   * @return closest matching mapping, else {@code null} if one could not be determined
   */
  MappingImpl disambiguateMappings() {
    double maxMatchRatio = -1;
    // Whether multiple mappings have the same max ratio
    boolean multipleMax = false;
    MappingImpl closestMapping = null;

    for (PropertyMappingImpl mapping : mappings) {
      double matches = 0, totalSourceTokens = 0, totalDestTokens = 0;
      String[][] allSourceTokens = new String[mapping.getSourceProperties().size()][];
      boolean[][] sourceMatches = new boolean[allSourceTokens.length][];

      // Build source tokens
      for (int i = 0; i < mapping.getSourceProperties().size(); i++) {
        PropertyInfo source = mapping.getSourceProperties().get(i);
        NameableType nameableType = NameableType.forPropertyType(source.getPropertyType());
        allSourceTokens[i] = configuration.getSourceNameTokenizer().tokenize(source.getName(),
            nameableType);
        sourceMatches[i] = new boolean[allSourceTokens[i].length];
        totalSourceTokens += allSourceTokens[i].length;
      }

      for (int destIndex = 0; destIndex < mapping.getDestinationProperties().size(); destIndex++) {
        PropertyInfo dest = mapping.getDestinationProperties().get(destIndex);
        NameableType nameableType = NameableType.forPropertyType(dest.getPropertyType());
        String[] destTokens = configuration.getDestinationNameTokenizer().tokenize(dest.getName(),
            nameableType);
        totalDestTokens += destTokens.length;

        for (String destToken : destTokens) {
          boolean matched = false;

          for (int i = 0; i < allSourceTokens.length && !matched; i++) {
            String[] sourceTokens = allSourceTokens[i];

            for (int j = 0; j < sourceTokens.length && !matched; j++) {
              if (sourceTokens[j].equalsIgnoreCase(destToken)) {
                matched = true;
                matches++;

                // Prevents the same source token from being counted twice
                if (!sourceMatches[i][j]) {
                  sourceMatches[i][j] = true;
                  matches++;
                }
              }
            }
          }
        }
      }

      double matchRatio = matches / (totalSourceTokens + totalDestTokens);
      if (matchRatio == maxMatchRatio)
        multipleMax = true;

      if (matchRatio > maxMatchRatio) {
        maxMatchRatio = matchRatio;
        closestMapping = mapping;
        multipleMax = false;
      }
    }

    return multipleMax ? null : closestMapping;
  }

  private void mergeMappings(TypeMap<?, ?> destinationMap) {
    for (Mapping mapping : destinationMap.getMappings())
      typeMap.addMapping(((MappingImpl) mapping)
          .createMergedCopy(propertyNameInfo.destinationProperties));
  }

  boolean isRecursivelyMatchable(Class<?> type) {
    return !Primitives.isPrimitive(type) && !type.isArray() && type != String.class
        && !sourceTypes.contains(type);
  }
}
