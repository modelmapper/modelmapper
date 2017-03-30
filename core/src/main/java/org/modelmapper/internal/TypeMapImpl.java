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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import org.modelmapper.Condition;
import org.modelmapper.Converter;
import org.modelmapper.ExpressionMap;
import org.modelmapper.PropertyMap;
import org.modelmapper.Provider;
import org.modelmapper.TypeMap;
import org.modelmapper.internal.util.Assert;
import org.modelmapper.internal.util.Types;
import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.PropertyInfo;
import org.modelmapper.spi.SourceGetter;

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

  private TypeMapImpl(TypeMapImpl<? super S, ? super D> baseTypeMap, Class<S> sourceTYpe, Class<D> destinationType) {
    this.sourceType = sourceTYpe;
    this.destinationType = destinationType;
    this.name = baseTypeMap.name;
    this.configuration = baseTypeMap.configuration;
    this.engine = baseTypeMap.engine;

    synchronized (baseTypeMap.mappings) {
      mappings.putAll(baseTypeMap.mappings);
    }
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
    PathProperties pathProperties = getDestinationProperties();

    synchronized (mappings) {
      for (Map.Entry<String, MappingImpl> entry : mappings.entrySet()) {
        pathProperties.matchAndRemove(entry.getKey());
      }
    }

    return pathProperties.get();
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

  public <V> TypeMap<S, D> addMapping(SourceGetter<S> sourceGetter, DestinationSetter<D, V> destinationSetter) {
    new ReferenceMapExpressionImpl<S, D>(this).map(sourceGetter, destinationSetter);
    return this;
  }

  public TypeMap<S, D> addMappings(ExpressionMap<S, D> mapper) {
    mapper.configure(new ConfigurableMapExpressionImpl<S, D>(this));
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

  public <DS extends S, DD extends D> TypeMap<S, D> include(Class<DS> sourceType, Class<DD> destinationType) {
    TypeMapImpl<DS, DD> derivedTypeMap = new TypeMapImpl<DS, DD>(this, sourceType, destinationType);
    configuration.typeMapStore.put(derivedTypeMap);
    return this;
  }

  public TypeMap<S, D> includeBase(Class<? super S> sourceType, Class<? super D> destinationType) {
    @SuppressWarnings("unchecked")
    TypeMapImpl<? super S, ? super D> baseTypeMap = (TypeMapImpl<? super S, ? super D>)
        configuration.typeMapStore.get(sourceType, destinationType, name);

    Assert.notNull(baseTypeMap, "Cannot find base TypeMap");

    synchronized (baseTypeMap.mappings) {
      for (Map.Entry<String, MappingImpl> entry : baseTypeMap.mappings.entrySet()) {
        addMapping(entry.getValue());
      }
    }

    return this;
  }

  MappingImpl addMapping(MappingImpl mapping) {
    synchronized (mappings) {
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

  boolean isFullMatching() {
    return getUnmappedProperties().isEmpty()
        || configuration.valueAccessStore.getFirstSupportedReader(sourceType) == null;
  }

  private PathProperties getDestinationProperties() {
    PathProperties pathProperties = new PathProperties();
    Set<Class<?>> classes = new HashSet<Class<?>>();

    Stack<Property> propertyStack = new Stack<Property>();
    propertyStack.push(new Property("", TypeInfoRegistry.typeInfoFor(destinationType, configuration)));

    while (!propertyStack.isEmpty()) {
      Property property = propertyStack.pop();
      classes.add(property.typeInfo.getType());
      for (Map.Entry<String, Mutator> entry : property.typeInfo.getMutators().entrySet()) {
        if (entry.getValue() instanceof PropertyInfoImpl.FieldPropertyInfo
            && !configuration.isFieldMatchingEnabled()) {
          continue;
        }

        String path = property.prefix + entry.getKey() + ".";
        Mutator mutator = entry.getValue();
        pathProperties.pathProperties.add(new PathProperty(path, mutator));

        if (!classes.contains(mutator.getType())
            && Types.mightContainsProperties(mutator.getType()))
          propertyStack.push(new Property(path, TypeInfoRegistry.typeInfoFor(mutator.getType(), configuration)));
      }
    }
    return pathProperties;
  }

  private static final class Property {
    String prefix;
    TypeInfo<?> typeInfo;

    public Property(String prefix, TypeInfo<?> typeInfo) {
      this.prefix = prefix;
      this.typeInfo = typeInfo;
    }
  }

  private static final class PathProperties {
    List<PathProperty> pathProperties = new ArrayList<PathProperty>();

    private void matchAndRemove(String path) {
      int startIndex = 0;
      int endIndex;
      while ((endIndex = path.indexOf(".", startIndex)) != -1) {
        String currentPath = path.substring(0, endIndex + 1);

        Iterator<PathProperty> iterator = pathProperties.iterator();
        while (iterator.hasNext())
          if (iterator.next().path.equals(currentPath))
            iterator.remove();

        startIndex = endIndex + 1;
      }

      Iterator<PathProperty> iterator = pathProperties.iterator();
      while (iterator.hasNext())
        if (iterator.next().path.startsWith(path))
          iterator.remove();
    }

    public List<PropertyInfo> get() {
      List<PropertyInfo> mutators = new ArrayList<PropertyInfo>(pathProperties.size());
      for (PathProperty pathProperty : pathProperties)
        mutators.add(pathProperty.mutator);
      return mutators;
    }
  }

  private static final class PathProperty {
    String path;
    Mutator mutator;

    private PathProperty(String path, Mutator mutator) {
      this.path = path;
      this.mutator = mutator;
    }
  }
}
