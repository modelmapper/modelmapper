/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.modelmapper.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.modelmapper.Converter;
import org.modelmapper.TypeMap;
import org.modelmapper.internal.PropertyInfoImpl.ValueReaderPropertyInfo;
import org.modelmapper.internal.converter.ConverterStore;
import org.modelmapper.internal.util.Iterables;
import org.modelmapper.internal.util.Strings;
import org.modelmapper.internal.util.Types;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.ConditionalConverter.MatchResult;
import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.MatchingStrategy;
import org.modelmapper.spi.NameableType;
import org.modelmapper.spi.PropertyInfo;
import org.modelmapper.spi.PropertyMapping;

/**
 * Builds and populates implicit property mappings for a TypeMap.
 * 
 * @param <S> source type
 * @param <D> destination type
 * 
 * @author Jonathan Halterman
 */
class ImplicitMappingBuilder<S, D> {
  private final TypeMapImpl<S, D> typeMap;
  private final TypeInfo<S> sourceTypeInfo;
  private final TypeMapStore typeMapStore;
  private final InheritingConfiguration configuration;
  private final ConverterStore converterStore;
  private final MatchingStrategy matchingStrategy;

  /** Mutable state */
  private final Errors errors = new Errors();
  private final PropertyNameInfoImpl propertyNameInfo;
  private final Set<Class<?>> sourceTypes = new HashSet<Class<?>>();
  private final Set<Class<?>> destinationTypes = new HashSet<Class<?>>();
  private final List<PropertyMappingImpl> mappings = new ArrayList<PropertyMappingImpl>();
  /** Mappings for which the source accessor type was not verified by the supported converter. */
  private final List<PropertyMappingImpl> partiallyMatchedMappings = new ArrayList<PropertyMappingImpl>();
  /** Mappings whose source and destination paths match by name, but not by type. */
  private final Map<Accessor, PropertyMappingImpl> intermediateMappings = new HashMap<Accessor, PropertyMappingImpl>();
  /** Mappings which are to be merged in from a pre-existing TypeMap. */
  private final List<MappingImpl> mergedMappings = new ArrayList<MappingImpl>();

  ImplicitMappingBuilder(S source, TypeMapImpl<S, D> typeMap, TypeMapStore typeMapStore,
      ConverterStore converterStore) {
    this.typeMap = typeMap;
    this.converterStore = converterStore;
    this.typeMapStore = typeMapStore;
    this.configuration = typeMap.configuration;
    sourceTypeInfo = TypeInfoRegistry.typeInfoFor(source, typeMap.getSourceType(), configuration);
    matchingStrategy = configuration.getMatchingStrategy();
    propertyNameInfo = new PropertyNameInfoImpl(typeMap.getSourceType(), configuration);
  }

  void build() {
    matchDestination(TypeInfoRegistry.typeInfoFor(typeMap.getDestinationType(), configuration));
  }

  /**
   * Matches the {@code destinationTypeInfo}'s mutator hierarchy hierarchy to the
   * {@code sourceTypeInfo}'s accessor hierarchy.
   */
  private void matchDestination(TypeInfo<?> destinationTypeInfo) {
    destinationTypes.add(destinationTypeInfo.getType());

    for (Map.Entry<String, Mutator> entry : destinationTypeInfo.getMutators().entrySet()) {
      propertyNameInfo.pushDestination(entry.getKey(), entry.getValue());
      String destPath = Strings.join(propertyNameInfo.getDestinationProperties());
      Mutator mutator = entry.getValue();

      // Skip explicit mappings
      MappingImpl existingMapping = typeMap.mappingFor(destPath);
      if (existingMapping == null) {
        matchSource(sourceTypeInfo, mutator);
        propertyNameInfo.clearSource();
        sourceTypes.clear();
      }

      // Use partially matched mappings only if there is no fully matched mapping
      if (mappings.isEmpty())
        mappings.addAll(partiallyMatchedMappings);

      if (!mappings.isEmpty()) {
        PropertyMappingImpl mapping = null;
        if (mappings.size() == 1) {
          mapping = mappings.get(0);
        } else {
          mapping = disambiguateMappings();
          if (mapping == null && !configuration.isAmbiguityIgnored())
            errors.ambiguousDestination(mappings);
        }

        if (mapping != null) {
          typeMap.addMapping(mapping);

          // If the mapping is potentially circular, add intermediate mappings
          if (Iterables.isIterable(mapping.getLastDestinationProperty().getType())) {
            for (PropertyInfo sourceAccessor : mapping.sourceAccessors) {
              PropertyMappingImpl intermediateMapping = intermediateMappings.get(sourceAccessor);
              if (intermediateMapping != null
                  && !intermediateMapping.getPath().equals(mapping.getPath()))
                typeMap.addMapping(intermediateMapping);
            }
          }
        }

        mappings.clear();
        partiallyMatchedMappings.clear();
        intermediateMappings.clear();
      } else if (!mergedMappings.isEmpty()) {
        for (MappingImpl mapping : mergedMappings)
          typeMap.addMapping(mapping);
        mergedMappings.clear();
      } else if (!destinationTypes.contains(mutator.getType())
          && !typeMap.isSkipped(destPath)
          && Types.mightContainsProperties(mutator.getType())
          && !isConvertable(existingMapping)) {
        matchDestination(mutator.getTypeInfo(configuration));
      }

      propertyNameInfo.popDestination();
    }

    destinationTypes.remove(destinationTypeInfo.getType());
    errors.throwConfigurationExceptionIfErrorsExist();
  }

  /**
   * Matches a source accessor hierarchy to the {@code destinationMutator}, first by checking the
   * {@code typeMapStore} for any existing TypeMaps and merging the mappings if one exists, else by
   * running the {@code matchingStrategy} against all accessors for the {@code sourceTypeInfo}.
   */
  private void matchSource(TypeInfo<?> sourceTypeInfo, Mutator destinationMutator) {
    sourceTypes.add(sourceTypeInfo.getType());

    for (Map.Entry<String, Accessor> entry : sourceTypeInfo.getAccessors().entrySet()) {
      Accessor accessor = entry.getValue();
      propertyNameInfo.pushSource(entry.getKey(), entry.getValue());
      boolean doneMatching = false;

      if (matchingStrategy.matches(propertyNameInfo)) {
        if (destinationTypes.contains(destinationMutator.getType())) {
          mappings.add(new PropertyMappingImpl(propertyNameInfo.getSourceProperties(),
              propertyNameInfo.getDestinationProperties(), true));
        } else {
          TypeMap<?, ?> propertyTypeMap = typeMapStore.get(accessor.getType(),
              destinationMutator.getType(), null);
          PropertyMappingImpl mapping = null;

          // Check to create mapping(s) from existing TypeMap
          if (propertyTypeMap != null) {
            Converter<?, ?> propertyConverter = propertyTypeMap.getConverter();
            if (propertyConverter == null)
              mergeMappings(propertyTypeMap);
            else
              mappings.add(new PropertyMappingImpl(propertyNameInfo.getSourceProperties(),
                  propertyNameInfo.getDestinationProperties(), propertyTypeMap.getProvider(),
                  propertyConverter));
            doneMatching = matchingStrategy.isExact();
          } else {
            for (ConditionalConverter<?, ?> converter : converterStore.getConverters()) {
              MatchResult matchResult = converter.match(accessor.getType(),
                  destinationMutator.getType());

              if (!MatchResult.NONE.equals(matchResult)) {
                mapping = new PropertyMappingImpl(propertyNameInfo.getSourceProperties(),
                    propertyNameInfo.getDestinationProperties(), false);

                if (MatchResult.FULL.equals(matchResult)) {
                  mappings.add(mapping);
                  doneMatching = matchingStrategy.isExact();
                  break;
                } else if (!configuration.isFullTypeMatchingRequired()) {
                  partiallyMatchedMappings.add(mapping);
                  break;
                }
              }
            }
          }

          if (mapping == null)
            intermediateMappings.put(
                accessor,
                new PropertyMappingImpl(propertyNameInfo.getSourceProperties(),
                    propertyNameInfo.getDestinationProperties(), false));
        }
      }

      if (!doneMatching
          && Types.mightContainsProperties(accessor.getType())
          && (!sourceTypes.contains(accessor.getType()) || accessor instanceof ValueReaderPropertyInfo))
        matchSource(accessor.getTypeInfo(configuration), destinationMutator);

      propertyNameInfo.popSource();

      if (doneMatching)
        break;
    }

    sourceTypes.remove(sourceTypeInfo.getType());
  }

  /**
   * Disambiguates the captured mappings by looking for the mapping with property tokens that most
   * closely match the destination. Match closeness is calculated as the total number of matched
   * source to destination tokens / the total number of source and destination tokens. Currently
   * this algorithm does not consider class name tokens.
   * 
   * @return closest matching mapping, else {@code null} if one could not be determined
   */
  PropertyMappingImpl disambiguateMappings() {
    double maxMatchRatio = -1;
    // Whether multiple mappings have the same max ratio
    boolean multipleMax = false;
    PropertyMappingImpl closestMapping = null;

    for (PropertyMappingImpl mapping : mappings) {
      double matches = 0, totalSourceTokens = 0, totalDestTokens = 0;
      String[][] allSourceTokens = new String[mapping.getSourceProperties().size()][];
      // Tracks whether a source token has been matched
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

        for (int destTokenIndex = 0; destTokenIndex < destTokens.length
            && matches < totalSourceTokens; destTokenIndex++) {
          String destToken = destTokens[destTokenIndex];
          boolean matched = false;

          for (int i = 0; i < allSourceTokens.length && !matched && matches < totalSourceTokens; i++) {
            String[] sourceTokens = allSourceTokens[i];

            for (int j = 0; j < sourceTokens.length && !matched && matches < totalSourceTokens; j++) {
              if (!sourceMatches[i][j] && sourceTokens[j].equalsIgnoreCase(destToken)) {
                matched = true;
                matches++;
                sourceMatches[i][j] = true;
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

  /**
   * Merges mappings from an existing TypeMap into the type map under construction.
   */
  private void mergeMappings(TypeMap<?, ?> destinationMap) {
    for (Mapping mapping : destinationMap.getMappings())
      mergedMappings.add(((MappingImpl) mapping).createMergedCopy(
          propertyNameInfo.getSourceProperties(), propertyNameInfo.getDestinationProperties()));
  }


  /**
   * Indicates whether the mapping represents a PropertyMapping that is convertible to the
   * destination type.
   */
  private boolean isConvertable(Mapping mapping) {
    if (mapping == null || mapping.getProvider() != null || !(mapping instanceof PropertyMapping))
      return false;

    PropertyMapping propertyMapping = (PropertyMapping) mapping;
    boolean hasSupportConverter = converterStore.getFirstSupported(
        propertyMapping.getLastSourceProperty().getType(),
        mapping.getLastDestinationProperty().getType()) != null;
    boolean hasSupportTypeMap = typeMapStore.get(
        propertyMapping.getLastSourceProperty().getType(),
        mapping.getLastDestinationProperty().getType(),
        null) != null;

    return hasSupportConverter || hasSupportTypeMap;
  }
}
