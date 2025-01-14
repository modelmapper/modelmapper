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

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.modelmapper.Condition;
import org.modelmapper.ConfigurationException;
import org.modelmapper.Converter;
import org.modelmapper.Provider;
import org.modelmapper.TypeMap;
import org.modelmapper.TypeToken;
import org.modelmapper.internal.converter.ConverterStore;
import org.modelmapper.internal.util.Iterables;
import org.modelmapper.internal.util.Objects;
import org.modelmapper.internal.util.Primitives;
import org.modelmapper.internal.util.Types;
import org.modelmapper.spi.ConstantMapping;
import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.MappingEngine;
import org.modelmapper.spi.PropertyMapping;
import org.modelmapper.spi.SourceMapping;

/**
 * MappingEngine implementation that caches ConditionalConverters by source and destination type
 * pairs.
 * 
 * @author Jonathan Halterman
 */
public class MappingEngineImpl implements MappingEngine {
  /** Cache of conditional converters */
  private final Map<TypePair<?, ?>, Converter<?, ?>> converterCache = new ConcurrentHashMap<TypePair<?, ?>, Converter<?, ?>>();
  private final InheritingConfiguration configuration;
  private final TypeMapStore typeMapStore;
  private final ConverterStore converterStore;

  public MappingEngineImpl(InheritingConfiguration configuration) {
    this.configuration = configuration;
    this.typeMapStore = configuration.typeMapStore;
    this.converterStore = configuration.converterStore;
  }

  /**
   * Initial entry point.
   */
  public <S, D> D map(S source, Class<S> sourceType, D destination,
      TypeToken<D> destinationTypeToken, String typeMapName) {
    MappingContextImpl<S, D> context = new MappingContextImpl<S, D>(source, sourceType,
        destination, destinationTypeToken.getRawType(), destinationTypeToken.getType(),
        typeMapName, this);
    D result = null;

    try {
      result = map(context);
    } catch (ConfigurationException e) {
      throw e;
    } catch (ErrorsException e) {
      throw context.errors.toMappingException();
    } catch (Throwable t) {
      context.errors.errorMapping(sourceType, destinationTypeToken.getType(), t);
    }

    context.errors.throwMappingExceptionIfErrorsExist();
    return result;
  }

  /**
   * Performs mapping using a TypeMap if one exists, else a converter if one applies, else a newly
   * created TypeMap. Recursive entry point.
   */
  @Override
  @SuppressWarnings("unchecked")
  public <S, D> D map(MappingContext<S, D> context) {
    MappingContextImpl<S, D> contextImpl = (MappingContextImpl<S, D>) context;
    Class<D> destinationType = context.getDestinationType();

    // Resolve some circular dependencies
    if (!Iterables.isIterable(destinationType)) {
      D circularDest = contextImpl.destinationForSource();
      if (circularDest != null && circularDest.getClass().isAssignableFrom(contextImpl.getDestinationType()))
        return circularDest;
    }

    D destination = null;
    TypeMap<S, D> typeMap = typeMapStore.get(context.getSourceType(), context.getDestinationType(),
        context.getTypeMapName());
    if (typeMap != null) {
      destination = typeMap(contextImpl, typeMap);
    } else {
      Converter<S, D> converter = converterFor(context);
      if (converter != null && (context.getDestination() == null || context.getParent() != null))
        destination = convert(context, converter);
      else if (!Primitives.isPrimitive(context.getSourceType()) && !Primitives.isPrimitive(context.getDestinationType())) {
        // Call getOrCreate in case TypeMap was created concurrently
        typeMap = typeMapStore.getOrCreate(context.getSource(), context.getSourceType(),
            context.getDestinationType(), context.getTypeMapName(), this);
        destination = typeMap(contextImpl, typeMap);
      } else if (context.getDestinationType().isAssignableFrom(context.getSourceType()))
        destination = (D) context.getSource();
    }

    contextImpl.setDestination(destination, true);
    return destination;
  }

  /**
   * Performs a type mapping for the {@code typeMap} and {@code context}.
   */
  <S, D> D typeMap(MappingContextImpl<S, D> context, TypeMap<S, D> typeMap) {
    if (context.getParent() != null && context.getDestination() == null)
      context.setDestination(destinationProperty(context), false);

    context.setTypeMap(typeMap);

    @SuppressWarnings("unchecked")
    Condition<S, D> condition = (Condition<S, D>) typeMap.getCondition();
    boolean noSkip = condition == null || condition.applies(context);

    if (noSkip && typeMap.getConverter() != null)
      return convert(context, typeMap.getConverter());

    if (context.getDestination() == null && Types.isInstantiable(context.getDestinationType())) {
      D destination = createDestination(context);
      if (destination == null)
        return null;
    }

    if (noSkip) {
      Converter<S, D> converter = typeMap.getPreConverter();
      if (converter != null)
        context.setDestination(convert(context, converter), true);

      for (Mapping mapping : typeMap.getMappings())
        propertyMap(mapping, context);

      converter = typeMap.getPostConverter();
      if (converter != null)
        context.setDestination(convert(context, converter), true);
    }

    return context.getDestination();
  }

  @SuppressWarnings("unchecked")
  private <S, D> void propertyMap(Mapping mapping, MappingContextImpl<S, D> context) {
    MappingImpl mappingImpl = (MappingImpl) mapping;
    String propertyPath = context.destinationPath + mappingImpl.getPath();
    if (context.isShaded(propertyPath))
      return;
    if (mapping.getCondition() == null && mapping.isSkipped()) // skip()
      return;

    Object source = resolveSourceValue(context, mapping);
    MappingContextImpl<Object, Object> propertyContext = propertyContextFor(context, source,
        mappingImpl);

    Condition<Object, Object> condition = (Condition<Object, Object>) Objects.firstNonNull(
        mapping.getCondition(),
        context.getTypeMap().getPropertyCondition(),
        configuration.getPropertyCondition());
    if (condition != null) {
      boolean conditionIsTrue = condition.applies(propertyContext);
      if (conditionIsTrue && mapping.isSkipped()) // when(condition).skip()
        return;
      else if (!conditionIsTrue && !mapping.isSkipped()) { // when(condition)
        context.shadePath(propertyPath);
        return;
      }
    }
    setDestinationValue(context, propertyContext, mappingImpl);
  }

  @SuppressWarnings("unchecked")
  private Object resolveSourceValue(MappingContextImpl<?, ?> context, Mapping mapping) {
    Object source = context.getSource();
    if (mapping instanceof PropertyMappingImpl) {
      StringBuilder destPathBuilder = new StringBuilder().append(context.destinationPath);
      for (Accessor accessor : (List<Accessor>) ((PropertyMapping) mapping).getSourceProperties()) {
        destPathBuilder.append(accessor.getName()).append('.');
        source = accessor.getValue(source);
        context.addParentSource(destPathBuilder.toString(), source);
        if (source == null)
          return null;
        if (!Iterables.isIterable(source.getClass())) {
          Object circularDest = context.sourceToDestination.get(source);
          if (circularDest != null)
            context.intermediateDestinations.put(destPathBuilder.toString(), circularDest);
        }
      }
    } else if (mapping instanceof ConstantMapping) {
      source = ((ConstantMapping) mapping).getConstant();
      context.addParentSource("", source);
    } else if (mapping instanceof SourceMapping) {
      context.addParentSource("", source);
    }
    return source;
  }

  /**
   * Sets a mapped or converted destination value in the last mapped mutator for the given
   * {@code mapping}. The final destination value is resolved by walking the {@code mapping}'s
   * mutator chain and obtaining each destination value in the chain either from the cache, from a
   * corresponding accessor, from a provider, or by instantiation, in that order.
   */
  @SuppressWarnings("unchecked")
  private <S, D> void setDestinationValue(MappingContextImpl<S, D> context,
      MappingContextImpl<Object, Object> propertyContext, MappingImpl mapping) {
    String destPath = context.destinationPath + mapping.getPath();
    Converter<Object, Object> converter = (Converter<Object, Object>) Objects.firstNonNull(
        mapping.getConverter(),
        context.getTypeMap().getPropertyConverter());
    if (converter != null)
      context.shadePath(destPath);

    Object destination = propertyContext.getParentDestination();
    if (destination == null)
      return;

    Mutator mutator = (Mutator) mapping.getLastDestinationProperty();
    Accessor accessor = PropertyInfoRegistry.accessorFor(mutator.getInitialType(), mutator.getName(), configuration);
    Object destinationValue = propertyContext.createDestinationViaProvider();
    if (destinationValue == null && propertyContext.isProvidedDestination() && accessor != null) {
      destinationValue = accessor.getValue(destination);
      propertyContext.setDestination(destinationValue, false);
    }

    if (converter != null)
      destinationValue = convert(propertyContext, converter);
    else if (propertyContext.getSource() != null)
      destinationValue = map(propertyContext);
    else {
      converter = converterFor(propertyContext);
      if (converter != null)
        destinationValue = convert(propertyContext, converter);
    }

    context.destinationCache.put(destPath, destinationValue);
    if (destinationValue != null || !configuration.isSkipNullEnabled())
      mutator.setValue(destination,
          destinationValue == null ? Primitives.defaultValue(mutator.getType())
              : destinationValue);
    if (destinationValue == null)
      context.shadePath(propertyContext.destinationPath);
  }

  /**
   * Returns a property context.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private MappingContextImpl<Object, Object> propertyContextFor(MappingContextImpl<?, ?> context,
      Object source, MappingImpl mapping) {
    Class<?> sourceType = mapping.getSourceType();
    if (source != null)
      sourceType = Types.deProxiedClass(source);
    boolean cyclic = mapping instanceof PropertyMapping && ((PropertyMappingImpl) mapping).cyclic;
    Class<Object> destinationType = (Class<Object>) mapping.getLastDestinationProperty().getType();
    Type genericDestinationType = context.genericDestinationPropertyType(mapping.getLastDestinationProperty().getGenericType());
    return new MappingContextImpl(context, source, sourceType, null, destinationType, genericDestinationType,
        mapping, !cyclic);
  }

  private <S, D> D destinationProperty(MappingContextImpl<S, D> context) {
    if (!context.isProvidedDestination() || context.getMapping() == null)
      return null;

    Object intermediateDest = context.getParent().getDestination();
    @SuppressWarnings("unchecked")
    List<Mutator> mutatorChain = (List<Mutator>) context.getMapping().getDestinationProperties();
    for (Mutator mutator : mutatorChain) {
      if (intermediateDest == null)
        break;

      Accessor accessor = TypeInfoRegistry.typeInfoFor(intermediateDest.getClass(),
          configuration)
          .getAccessors()
          .get(mutator.getName());
      if (accessor != null)
        intermediateDest = accessor.getValue(intermediateDest);
    }

    @SuppressWarnings("unchecked")
    D destinationProperty = (D) intermediateDest;
    return destinationProperty;
  }

  /**
   * Performs a mapping using a Converter.
   */
  private <S, D> D convert(MappingContext<S, D> context, Converter<S, D> converter) {
    try {
      return converter.convert(context);
    } catch (ErrorsException e) {
      throw e;
    } catch (Exception e) {
      ((MappingContextImpl<S, D>) context).errors.errorConverting(converter,
          context.getSourceType(), context.getDestinationType(), e);
      return null;
    }
  }

  /**
   * Retrieves a converter from the store or from the cache.
   */
  @SuppressWarnings("unchecked")
  private <S, D> Converter<S, D> converterFor(MappingContext<S, D> context) {
    TypePair<?, ?> typePair = TypePair.of(context.getSourceType(), context.getDestinationType(),
        context.getTypeMapName());
    Converter<S, D> converter = (Converter<S, D>) converterCache.get(typePair);
    if (converter == null) {
      converter = converterStore.getFirstSupported(context.getSourceType(),
          context.getDestinationType());
      if (converter != null)
        converterCache.put(typePair, converter);
    }

    return converter;
  }

  private <T> T instantiate(Class<T> type, Errors errors) {
    try {
      Constructor<T> constructor = type.getDeclaredConstructor();
      if (!constructor.isAccessible())
        constructor.setAccessible(true);
      return constructor.newInstance();
    } catch (Exception e) {
      errors.errorInstantiatingDestination(type, e);
      return null;
    }
  }

  @Override
  public <S, D> D createDestination(MappingContext<S, D> context) {
    MappingContextImpl<S, D> contextImpl = (MappingContextImpl<S, D>) context;
    D destination = contextImpl.createDestinationViaProvider();
    if (destination == null)
      destination = instantiate(context.getDestinationType(), contextImpl.errors);

    contextImpl.setDestination(destination, true);
    return destination;
  }

  InheritingConfiguration getConfiguration() {
    return configuration;
  }

  @SuppressWarnings("unchecked")
  <S, D> D createDestinationViaGlobalProvider(S source, Class<D> requestedType,
      Errors errors) {
    D destination = null;
    Provider<D> provider = (Provider<D>) configuration.getProvider();
    if (provider != null) {
      destination = provider.get(new ProvisionRequestImpl<D>(source, requestedType));
      validateDestination(requestedType, destination, errors);
    }
    if (destination == null)
      destination = instantiate(requestedType, errors);

    return destination;
  }

  void validateDestination(Class<?> destinationType, Object destination, Errors errors) {
    if (destination != null && !destinationType.isAssignableFrom(destination.getClass()))
      errors.invalidProvidedDestinationInstance(destination, destinationType);
  }
}
