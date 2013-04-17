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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.modelmapper.Condition;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.Provider;
import org.modelmapper.TypeMap;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.internal.util.Assert;
import org.modelmapper.internal.util.Types;
import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.PropertyInfo;

/**
 * TypeMap implementation.
 * 
 * @author Jonathan Halterman
 */
class TypeMapImpl<S, D> implements TypeMap<S, D> {
  private final Class<S> sourceType;
  private final Class<D> destinationType;
  final Configuration configuration;
  private final MappingEngineImpl engine;
  /** Guarded by "mappings" */
  private final Map<String, PropertyInfo> mappedProperties = new HashMap<String, PropertyInfo>();
  /** Guarded by "mappings" */
  private final Map<String, MappingImpl> mappings = new TreeMap<String, MappingImpl>();
  private Converter<S, D> converter;
  private Converter<S, D> preConverter;
  private Converter<S, D> postConverter;
  private Condition<?, ?> condition;
  private Provider<D> provider;
  private Converter<?, ?> propertyConverter;
  private Condition<?, ?> propertyCondition;
  private Provider<?> propertyProvider;

  TypeMapImpl(Class<S> sourceType, Class<D> destinationType, Configuration configuration,
      MappingEngineImpl engine) {
    this.sourceType = sourceType;
    this.destinationType = destinationType;
    this.configuration = configuration;
    this.engine = engine;
  }

  public void addMappings(PropertyMap<S, D> propertyMap) {
    if (sourceType.isEnum() || destinationType.isEnum())
      new Errors().mappingForEnum().throwConfigurationExceptionIfErrorsExist();

    synchronized (mappings) {
      for (MappingImpl mapping : new MappingBuilderImpl<S, D>(sourceType, destinationType,
          configuration).build(propertyMap)) {
        MappingImpl existingMapping = addMapping(mapping);
        if (existingMapping != null && existingMapping.isExplicit())
          new Errors().duplicateMapping(mapping.getLastDestinationProperty())
              .throwConfigurationExceptionIfErrorsExist();
      }
    }
  }

  public Condition<?, ?> getCondition() {
    return condition;
  }

  public Converter<S, D> getConverter() {
    return converter;
  }

  public Class<D> getDestinationType() {
    return destinationType;
  }

  public List<Mapping> getMappings() {
    synchronized (mappings) {
      return new ArrayList<Mapping>(mappings.values());
    }
  }

  public Converter<S, D> getPostConverter() {
    return postConverter;
  }

  public Converter<S, D> getPreConverter() {
    return preConverter;
  }

  public Condition<?, ?> getPropertyCondition() {
    return propertyCondition;
  }

  public Converter<?, ?> getPropertyConverter() {
    return propertyConverter;
  }

  public Provider<?> getPropertyProvider() {
    return propertyProvider;
  }

  public Provider<D> getProvider() {
    return provider;
  }

  public Class<S> getSourceType() {
    return sourceType;
  }

  public List<PropertyInfo> getUnmappedProperties() {
    List<PropertyInfo> unmapped = getUnmappedDestinationProperties();

    if (MatchingStrategies.STRICT.equals(configuration.getMatchingStrategy())) {
      unmapped.addAll(getUnmappedSourceProperties());
    }

    return unmapped;
  }

  public List<PropertyInfo> getUnmappedSourceProperties() {
    TypeInfo<S> sourceInfo = TypeInfoRegistry.typeInfoFor(sourceType, configuration);
      final List<PropertyInfo> propertyInfos = new ArrayList<PropertyInfo>();
      synchronized (mappings) {
          for (final Map.Entry<String, Mutator> entry : sourceInfo.getMutators().entrySet()) {
              if (!mappedProperties.containsKey(entry.getKey())) {
                  propertyInfos.add(entry.getValue());
              }
          }
      }
      return propertyInfos;
  }
  
  public List<PropertyInfo> getUnmappedDestinationProperties() {
   TypeInfo<D> destinationInfo = TypeInfoRegistry.typeInfoFor(destinationType, configuration);
      final List<PropertyInfo> propertyInfos = new ArrayList<PropertyInfo>();
      synchronized (mappings) {
          for (final Map.Entry<String, Mutator> entry : destinationInfo.getMutators().entrySet()) {
              if (!mappedProperties.containsKey(entry.getKey())) {
                  propertyInfos.add(entry.getValue());
              }
          }
      }
      return propertyInfos;
  }

  public D map(S source) {
    Class<S> sourceType = Types.<S>deProxy(source.getClass());
    MappingContextImpl<S, D> context = new MappingContextImpl<S, D>(source, sourceType, null,
        destinationType, null, engine);
    D result = null;

    try {
      result = engine.typeMap(context, this);
    } catch (Throwable t) {
      context.errors.errorMapping(sourceType, destinationType, t);
    }

    context.errors.throwMappingExceptionIfErrorsExist();
    return result;
  }

  public void map(S source, D destination) {
    Class<S> sourceType = Types.<S>deProxy(source.getClass());
    MappingContextImpl<S, D> context = new MappingContextImpl<S, D>(source, sourceType,
        destination, destinationType, null, engine);

    try {
      engine.typeMap(context, this);
    } catch (Throwable t) {
      context.errors.errorMapping(sourceType, destinationType, t);
    }

    context.errors.throwMappingExceptionIfErrorsExist();
  }

  public TypeMap<S, D> setCondition(Condition<?, ?> condition) {
    this.condition = Assert.notNull(condition, "condition");
    return this;
  }

  public TypeMap<S, D> setConverter(Converter<S, D> converter) {
    this.converter = Assert.notNull(converter, "converter");
    return this;
  }

  public TypeMap<S, D> setPostConverter(Converter<S, D> converter) {
    this.postConverter = Assert.notNull(converter, "converter");
    return this;
  }

  public TypeMap<S, D> setPreConverter(Converter<S, D> converter) {
    this.preConverter = Assert.notNull(converter, "converter");
    return this;
  }

  public TypeMap<S, D> setPropertyCondition(Condition<?, ?> condition) {
    propertyCondition = Assert.notNull(condition, "condition");
    return this;
  }

  public TypeMap<S, D> setPropertyConverter(Converter<?, ?> converter) {
    propertyConverter = Assert.notNull(converter, "converter");
    return this;
  }

  public TypeMap<S, D> setPropertyProvider(Provider<?> provider) {
    propertyProvider = Assert.notNull(provider, "provider");
    return this;
  }

  public TypeMap<S, D> setProvider(Provider<D> provider) {
    this.provider = Assert.notNull(provider, "provider");
    return this;
  }

  @Override
  public String toString() {
    return String.format("TypeMap[%s -> %s]", sourceType.getSimpleName(),
        destinationType.getSimpleName());
  }

  public void validate() {
    if (converter != null || preConverter != null || postConverter != null)
      return;

    Errors errors = new Errors();

    final List<PropertyInfo> unmappedDestinationProperties = getUnmappedDestinationProperties();
    if (!unmappedDestinationProperties.isEmpty()) {
      errors.errorUnmappedDestinationProperties(this, unmappedDestinationProperties);
    }

    if (MatchingStrategies.STRICT.equals(configuration.getMatchingStrategy())) {
      final List<PropertyInfo> unmappedSourceProperties = getUnmappedSourceProperties();
      if (!unmappedSourceProperties.isEmpty()) {
        errors.errorUnmappedSourceProperties(this, unmappedSourceProperties);
      }
    }
      
    errors.throwValidationExceptionIfErrorsExist();
  }

  MappingImpl addMapping(MappingImpl mapping) {
    synchronized (mappings) {
      mappedProperties.put(mapping.getDestinationProperties().get(0).getName(),
          mapping.getDestinationProperties().get(0));
      return mappings.put(mapping.getPath(), mapping);
    }
  }

  /**
   * Used by PropertyMapBuilder to determine if a mapping for the {@code path} already exists. No
   * need to synchronize here since the TypeMap is not exposed publicly yet.
   */
  boolean isMapped(String path) {
    return mappings.containsKey(path);
  }

  /**
   * Used by PropertyMapBuilder to determine if a skipped mapping exists for the {@code path}. No
   * need to synchronize here since the TypeMap is not exposed publicly yet.
   */
  boolean isSkipped(String path) {
    Mapping mapping = mappings.get(path);
    return mapping != null && mapping.isSkipped();
  }
}
