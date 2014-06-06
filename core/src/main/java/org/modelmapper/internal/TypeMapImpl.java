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
  private final String name;
  final InheritingConfiguration configuration;
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

  TypeMapImpl(Class<S> sourceType, Class<D> destinationType, String name,
      InheritingConfiguration configuration, MappingEngineImpl engine) {
    this.sourceType = sourceType;
    this.destinationType = destinationType;
    this.name = name;
    this.configuration = configuration;
    this.engine = engine;
  }

  public void addMappings(PropertyMap<S, D> propertyMap) {
    if (sourceType.isEnum() || destinationType.isEnum())
      throw new Errors().mappingForEnum().toConfigurationException();

    synchronized (mappings) {
      for (MappingImpl mapping : new ExplicitMappingBuilder<S, D>(sourceType, destinationType,
          configuration).build(propertyMap)) {
        MappingImpl existingMapping = addMapping(mapping);
        if (existingMapping != null && existingMapping.isExplicit())
          throw new Errors().duplicateMapping(mapping.getLastDestinationProperty())
              .toConfigurationException();
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

  public String getName() {
    return name;
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
    TypeInfo<D> destinationInfo = TypeInfoRegistry.typeInfoFor(destinationType, configuration);
    List<PropertyInfo> unmapped = new ArrayList<PropertyInfo>();

    synchronized (mappings) {
      for (Map.Entry<String, Mutator> entry : destinationInfo.getMutators().entrySet())
        if (!mappedProperties.containsKey(entry.getKey()))
          unmapped.add(entry.getValue());
    }

    return unmapped;
  }

  public D map(S source) {
    Class<S> sourceType = Types.<S>deProxy(source.getClass());
    MappingContextImpl<S, D> context = new MappingContextImpl<S, D>(source, sourceType, null,
        destinationType, null, name, engine);
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
        destination, destinationType, null, name, engine);

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
    StringBuilder b = new StringBuilder();
    b.append("TypeMap[")
        .append(sourceType.getSimpleName())
        .append(" -> ")
        .append(destinationType.getSimpleName());
    if (name != null)
      b.append(' ').append(name);
    return b.append(']').toString();
  }

  public void validate() {
    if (converter != null || preConverter != null || postConverter != null)
      return;

    Errors errors = new Errors();
    List<PropertyInfo> unmappedProperties = getUnmappedProperties();
    if (!unmappedProperties.isEmpty())
      errors.errorUnmappedProperties(this, unmappedProperties);

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
   * Used by PropertyMapBuilder to determine if a skipped mapping exists for the {@code path}. No
   * need to synchronize here since the TypeMap is not exposed publicly yet.
   */
  boolean isSkipped(String path) {
    Mapping mapping = mappings.get(path);
    return mapping != null && mapping.isSkipped();
  }

  /**
   * Used by ImplicitMappingBuilder to determine if a mapping for the {@code path} already exists.
   * No need to synchronize here since the TypeMap is not exposed publicly yet.
   */
  MappingImpl mappingFor(String path) {
    return mappings.get(path);
  }
}
