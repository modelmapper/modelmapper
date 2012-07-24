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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.modelmapper.TypeMap;
import org.modelmapper.config.Configuration;
import org.modelmapper.internal.converter.ConverterStore;
import org.modelmapper.internal.util.Iterables;
import org.modelmapper.internal.util.Primitives;
import org.modelmapper.internal.util.Strings;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.ConditionalConverter.MatchResult;
import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.MatchingStrategy;
import org.modelmapper.spi.NameableType;
import org.modelmapper.spi.PropertyInfo;

/**
 * Builds and populates implicit property mappings for a TypeMap.
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
  private final Set<Class<?>> sourceTypes = new HashSet<Class<?>>();
  private final Set<Class<?>> destinationTypes = new HashSet<Class<?>>();
  private final List<PropertyMappingImpl> mappings = new ArrayList<PropertyMappingImpl>();
  /** Mappings for which the source accessor type was not verified by the supported converter */
  private final List<PropertyMappingImpl> unverifiedMappings = new ArrayList<PropertyMappingImpl>();

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
    matchDestination(TypeInfoRegistry.typeInfoFor(typeMap.getDestinationType(), configuration));
  }

  private void matchDestination(TypeInfo<?> destinationTypeInfo) {
    Class<?> destinationType = destinationTypeInfo.getType();
    if (!Iterables.isIterable(destinationType) && !destinationTypes.add(destinationType))
      throw errors.errorCircularReference(destinationType).toConfigurationException();

    for (Map.Entry<String, Mutator> entry : destinationTypeInfo.getMutators().entrySet()) {
      Mutator mutator = entry.getValue();
      propertyNameInfo.pushDestination(entry.getKey(), entry.getValue());

      // Skip explicit mappings
      if (!typeMap.isMapped(Strings.join(propertyNameInfo.destinationProperties))) {
        matchSource(sourceTypeInfo, mutator);
        propertyNameInfo.clearSource();
        sourceTypes.clear();
      }

      if (!unverifiedMappings.isEmpty() && mappings.isEmpty())
        mappings.addAll(unverifiedMappings);

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
        unverifiedMappings.clear();
      } else {
        TypeMap<?, ?> destinationMap = typeMapStore.get(typeMap.getSourceType(), mutator.getType());
        if (destinationMap == null) {
          matchDestination(TypeInfoRegistry.typeInfoFor(mutator.getType(), configuration));
        } else {
          mergeMappings(destinationMap);
        }
      }

      propertyNameInfo.popDestination();
    }

    destinationTypes.remove(destinationTypeInfo.getType());
    errors.throwConfigurationExceptionIfErrorsExist();
  }

  /**
   * Matches a source accessor hierarchy to the {@code destinationMutator}.
   */
  private void matchSource(TypeInfo<?> sourceTypeInfo, Mutator destinationMutator) {
    sourceTypes.add(sourceTypeInfo.getType());

    for (Map.Entry<String, Accessor> entry : sourceTypeInfo.getAccessors().entrySet()) {
      Accessor accessor = entry.getValue();
      propertyNameInfo.pushSource(entry.getKey(), entry.getValue());

      if (matchingStrategy.matches(propertyNameInfo)) {
        for (ConditionalConverter<?, ?> converter : typeConverterStore.getConverters()) {
          MatchResult matchResult = converter.match(accessor.getType(),
              destinationMutator.getType());

          if (!MatchResult.NONE.equals(matchResult)) {
            PropertyMappingImpl mapping = new PropertyMappingImpl(
                propertyNameInfo.sourceProperties, propertyNameInfo.destinationProperties);

            if (MatchResult.FULL.equals(matchResult)) {
              mappings.add(mapping);
              if (matchingStrategy.isExact())
                return;
            } else
              unverifiedMappings.add(mapping);

            break;
          }
        }
      }

      if (isRecursivelyMatchable(accessor.getType()))
        matchSource(TypeInfoRegistry.typeInfoFor(accessor.getType(), configuration),
            destinationMutator);

      propertyNameInfo.popSource();
    }

    sourceTypes.remove(sourceTypeInfo.getType());
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

  /**
   * Merges mappings from an existing TypeMap into the type map under construction.
   */
  private void mergeMappings(TypeMap<?, ?> destinationMap) {
    for (Mapping mapping : destinationMap.getMappings())
      typeMap.addMapping(((MappingImpl) mapping).createMergedCopy(propertyNameInfo.destinationProperties));
  }

  boolean isRecursivelyMatchable(Class<?> type) {
    return type != Object.class && !Primitives.isPrimitive(type) && !type.isArray()
        && type != String.class && !sourceTypes.contains(type);
  }
}
