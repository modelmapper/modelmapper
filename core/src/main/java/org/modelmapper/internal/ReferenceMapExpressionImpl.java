/*
 * Copyright 2017 the original author or authors.
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

import net.jodah.typetools.TypeResolver;

import static org.modelmapper.internal.ExplicitMappingBuilder.MappingOptions;

import org.modelmapper.internal.util.Primitives;
import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.builder.ReferenceMapExpression;
import org.modelmapper.spi.SourceGetter;

import java.lang.reflect.Modifier;

/**
 * {@link ReferenceMapExpression} implementation
 *
 * @param <S> source type
 * @param <D> destination type
 *
 * @author Chun Han Hsiao
 */
class ReferenceMapExpressionImpl<S, D> implements ReferenceMapExpression<S, D> {
  private TypeMapImpl<S, D> typeMap;
  private MappingOptions options;

  public ReferenceMapExpressionImpl(TypeMapImpl<S, D> typeMap) {
    this(typeMap, new MappingOptions());
  }

  public ReferenceMapExpressionImpl(TypeMapImpl<S, D> typeMap, MappingOptions options) {
    this.typeMap = typeMap;
    this.options = options;
  }

  public <V> void map(SourceGetter<S> sourceGetter, DestinationSetter<D, V> destinationSetter) {
    PropertyReferenceCollector collector = new PropertyReferenceCollector(typeMap.configuration, options);

    try {
      S source = ProxyFactory.proxyFor(typeMap.getSourceType(), collector.newSourceInterceptor(), collector.getProxyErrors());
      sourceGetter.get(source);
    } catch (NullPointerException e) {
      if (collector.getProxyErrors().hasErrors())
        throw collector.getProxyErrors().toException();
      throw e;
    } catch (ErrorsException e) {
      throw e.getErrors().toConfigurationException();
    }

    try {
      D destination = ProxyFactory.proxyFor(typeMap.getDestinationType(), collector.newDestinationInterceptor(), collector.getProxyErrors());
      destinationSetter.accept(destination, destinationValue(destinationSetter));
    } catch (NullPointerException e) {
      if (collector.getProxyErrors().hasErrors())
        throw collector.getProxyErrors().toException();
      throw e;
    } catch (ErrorsException e) {
      throw e.getErrors().toConfigurationException();
    }

    typeMap.addMapping(collector.collect());
  }

  public <V> void skip(DestinationSetter<D, V> destinationSetter) {
    options.skipType = 1;

    PropertyReferenceCollector collector = new PropertyReferenceCollector(typeMap.configuration, options);
    D destination = ProxyFactory.proxyFor(typeMap.getDestinationType(), collector.newDestinationInterceptor(), collector.getErrors());
    destinationSetter.accept(destination, destinationValue(destinationSetter));

    typeMap.addMapping(collector.collect());
  }

  private <V> V destinationValue(DestinationSetter<D, V> destinationSetter) {
    Class<?>[] typeArguments = TypeResolver.resolveRawArguments(DestinationSetter.class, destinationSetter.getClass());
    if (typeArguments != null) {
      Class<?> valueClass = typeArguments[1];
      if (Primitives.isPrimitive(valueClass)) {
        return Primitives.defaultValue(Primitives.primitiveFor(valueClass));
      }
    }
    return null;
  }
}
