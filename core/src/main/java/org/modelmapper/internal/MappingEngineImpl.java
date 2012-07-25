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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.modelmapper.Condition;
import org.modelmapper.ConfigurationException;
import org.modelmapper.Converter;
import org.modelmapper.Provider;
import org.modelmapper.TypeMap;
import org.modelmapper.config.Configuration;
import org.modelmapper.internal.converter.ConverterStore;
import org.modelmapper.internal.util.Iterables;
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
  private final Configuration configuration;
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
  public <S, D> D map(S source, Class<S> sourceType, D destination, Class<D> destinationType) {
    MappingContextImpl<S, D> context = new MappingContextImpl<S, D>(source, sourceType,
        destination, destinationType, this);
    D result = null;

    try {
      result = map(context);
    } catch (ConfigurationException e) {
      throw e;
    } catch (ErrorsException e) {
      throw context.errors.toMappingException();
    } catch (Throwable t) {
      context.errors.errorMapping(sourceType, destinationType, t);
    }

    context.errors.throwMappingExceptionIfErrorsExist();
    return result;
  }

  /**
   * Performs mapping using a TypeMap if one exists, else a converter if one applies, else a newly
   * created TypeMap. Recursive entry point.
   */
  public <S, D> D map(MappingContext<S, D> context) {
    MappingContextImpl<S, D> contextImpl = (MappingContextImpl<S, D>) context;
    Class<D> destinationType = context.getDestinationType();
    if (!Iterables.isIterable(destinationType) && contextImpl.currentlyMapping(destinationType))
      throw contextImpl.errors.errorCircularReference(destinationType).toException();

    D destination = null;
    TypeMap<S, D> typeMap = typeMapStore.get(context.getSourceType(), context.getDestinationType());
    if (typeMap != null) {
      destination = typeMap(context, typeMap);
    } else {
      Converter<S, D> converter = converterFor(context);
      if (converter != null) {
        destination = convert(context, converter);
      } else {
        // Call getOrCreate in case TypeMap was created concurrently
        typeMap = typeMapStore.getOrCreate(context.getSourceType(), context.getDestinationType(),
            this);
        destination = typeMap(context, typeMap);
      }
    }

    contextImpl.finishedMapping(destinationType);
    return destination;
  }

  /**
   * Performs a type mapping for the {@code typeMap} and {@code context}.
   */
  <S, D> D typeMap(MappingContext<S, D> context, TypeMap<S, D> typeMap) {
    MappingContextImpl<S, D> contextImpl = (MappingContextImpl<S, D>) context;

    contextImpl.setTypeMap(typeMap);
    if (context.getDestination() == null) {
      D destination = createDestination(context);
      if (destination == null)
        return null;
    }

    @SuppressWarnings("unchecked")
    Condition<S, D> condition = (Condition<S, D>) typeMap.getCondition();
    Converter<S, D> converter = typeMap.getConverter();
    if (condition == null || condition.applies(context)) {
      if (converter != null)
        return convert(context, converter);

      for (Mapping mapping : typeMap.getMappings()) {
        propertyMap(mapping, contextImpl);
      }
    }

    return context.getDestination();
  }

  @SuppressWarnings("unchecked")
  private <S, D> void propertyMap(Mapping mapping, MappingContextImpl<S, D> context) {
    MappingImpl mappingImpl = (MappingImpl) mapping;
    if (context.isShaded(mappingImpl.getPath()))
      return;

    Condition<Object, Object> condition = (Condition<Object, Object>) mapping.getCondition();
    if (condition == null)
      condition = (Condition<Object, Object>) context.typeMap().getPropertyCondition();
    if (condition == null && mapping.isSkipped())
      return;

    Object source = resolveSourceValue(context, mapping);
    MappingContextImpl<Object, Object> propertyContext = propertyContextFor(context, source,
        mapping);

    if (condition != null) {
      if (!condition.applies(propertyContext)) {
        context.shadePath(mappingImpl.getPath());
        return;
      } else if (mapping.isSkipped())
        return;
    }

    Converter<Object, Object> converter = (Converter<Object, Object>) mapping.getConverter();
    if (converter == null)
      converter = (Converter<Object, Object>) context.typeMap().getPropertyConverter();
    if (converter != null)
      context.shadePath(mappingImpl.getPath());
    else if (mapping instanceof SourceMapping)
      return;

    // Create last destination via provider prior to mapping/conversion
    createDestinationViaProvider(propertyContext);

    Object destinationValue = converter != null ? convert(propertyContext, converter)
        : source != null ? map(propertyContext) : null;
    setDestinationValue(context.getDestination(), destinationValue, propertyContext, mappingImpl);
  }

  @SuppressWarnings("unchecked")
  private Object resolveSourceValue(MappingContextImpl<?, ?> context, Mapping mapping) {
    Object source = context.getSource();
    if (mapping instanceof PropertyMapping)
      for (Accessor accessor : (List<Accessor>) ((PropertyMapping) mapping).getSourceProperties()) {
        context.setParentSource(source);
        source = accessor.getValue(source);
        if (source == null)
          return null;
      }
    else if (mapping instanceof ConstantMapping)
      source = ((ConstantMapping) mapping).getConstant();

    return source;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private void setDestinationValue(Object destination, Object destinationValue,
      MappingContextImpl<?, ?> context, MappingImpl mapping) {
    List<Mutator> mutatorChain = (List<Mutator>) mapping.getDestinationProperties();

    for (int i = 0; i < mutatorChain.size(); i++) {
      Mutator mutator = mutatorChain.get(i);

      // Handle last mutator in chain
      if (i == mutatorChain.size() - 1) {
        context.destinationCache.put(mutator, destinationValue);
        mutator.setValue(destination,
            destinationValue == null ? Primitives.defaultValue(mutator.getType())
                : destinationValue);
        if (destinationValue == null)
          context.shadePath(mapping.getPath());
      } else {
        Object intermediateDest = context.destinationCache.get(mutator);
        if (intermediateDest == null) {
          // Create intermediate destination via global provider
          if (configuration.getProvider() != null)
            intermediateDest = configuration.getProvider().get(
                new ProvisionRequestImpl(context.parentSource(), mutator.getType()));
          else
            intermediateDest = instantiate(mutator.getType(), context.errors);

          if (intermediateDest == null)
            return;

          context.destinationCache.put(mutator, intermediateDest);
        }

        mutator.setValue(destination, intermediateDest);
        destination = intermediateDest;
      }
    }
  }

  /**
   * Returns a property context.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private MappingContextImpl<Object, Object> propertyContextFor(MappingContextImpl<?, ?> context,
      Object source, Mapping mapping) {
    Class<?> sourceType;
    if (mapping instanceof PropertyMapping) {
      sourceType = ((PropertyMapping) mapping).getLastSourceProperty().getType();
    } else if (mapping instanceof ConstantMapping) {
      Object constant = ((ConstantMapping) mapping).getConstant();
      sourceType = constant == null ? Object.class : Types.deProxy(constant.getClass());
    } else {
      sourceType = ((SourceMapping) mapping).getSourceType();
    }

    Class<Object> destinationType = (Class<Object>) mapping.getLastDestinationProperty().getType();
    return new MappingContextImpl(context, source, sourceType, null, destinationType, mapping, true);
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
    TypePair<?, ?> typePair = TypePair.of(context.getSourceType(), context.getDestinationType());
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

  /**
   * Returns a destination object via a provider with the current Mapping's provider used first,
   * else the TypeMap's property provider, else the TypeMap's provider, else the configuration's
   * provider.
   */
  @SuppressWarnings("unchecked")
  private <S, D> D createDestinationViaProvider(MappingContextImpl<S, D> context) {
    Provider<D> provider = null;
    if (context.getMapping() != null) {
      provider = (Provider<D>) context.getMapping().getProvider();
      if (provider == null && context.parentTypeMap() != null)
        provider = (Provider<D>) context.parentTypeMap().getPropertyProvider();
    }
    if (provider == null && context.typeMap() != null)
      provider = context.typeMap().getProvider();
    if (provider == null && configuration.getProvider() != null)
      provider = (Provider<D>) configuration.getProvider();
    if (provider == null)
      return null;

    D destination = provider.get(context);
    if (destination != null
        && !context.getDestinationType().isAssignableFrom(destination.getClass()))
      context.errors.invalidProvidedDestinationInstance(destination, context.getDestinationType());
    context.setDestination(destination);
    return destination;
  }

  public <S, D> D createDestination(MappingContext<S, D> context) {
    MappingContextImpl<S, D> contextImpl = (MappingContextImpl<S, D>) context;
    D destination = createDestinationViaProvider(contextImpl);
    if (destination != null)
      return destination;

    destination = instantiate(context.getDestinationType(), contextImpl.errors);
    contextImpl.setDestination(destination);
    return destination;
  }
}
