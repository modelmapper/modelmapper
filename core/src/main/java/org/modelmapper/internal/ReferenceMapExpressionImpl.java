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

import static org.modelmapper.internal.ExplicitMappingBuilder.MappingOptions;

import org.modelmapper.internal.util.Primitives;
import org.modelmapper.internal.util.TypeResolver;
import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.builder.ReferenceMapExpression;
import org.modelmapper.spi.SourceGetter;

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

  public void map(SourceGetter<S> sourceGetter, DestinationSetter<D, ?> destinationSetter) {
    PropertyReferenceCollector collector = new PropertyReferenceCollector(typeMap.configuration, options);

    S source = ProxyFactory.proxyFor(typeMap.getSourceType(), collector.newSourceInterceptor(), collector.getErrors());
    sourceGetter.get(source);

    D destination = ProxyFactory.proxyFor(typeMap.getDestinationType(), collector.newDestinationInterceptor(), collector.getErrors());

    @SuppressWarnings("unchecked")
    DestinationSetter<D, Object> typedDestinationSetter = (DestinationSetter<D, Object>) destinationSetter;
    typedDestinationSetter.accept(destination, destinationValue(destinationSetter));

    typeMap.addMapping(collector.collect());
  }

  public void skip(DestinationSetter<D, ?> destinationSetter) {
    options.skipType = 1;

    PropertyReferenceCollector collector = new PropertyReferenceCollector(typeMap.configuration, options);
    D destination = ProxyFactory.proxyFor(typeMap.getDestinationType(), collector.newDestinationInterceptor(), collector.getErrors());

    @SuppressWarnings("unchecked")
    DestinationSetter<D, Object> typedDestinationSetter = (DestinationSetter<D, Object>) destinationSetter;
    typedDestinationSetter.accept(destination, destinationValue(destinationSetter));

    typeMap.addMapping(collector.collect());
  }

  private Object destinationValue(DestinationSetter<D, ?> destinationSetter) {
    Class<?>[] typeArguments = TypeResolver.resolveArguments(destinationSetter.getClass(), DestinationSetter.class);
    if (typeArguments != null) {
      Class<?> valueClass = typeArguments[1];
      if (Primitives.isPrimitive(valueClass)) {
        return Primitives.defaultValue(Primitives.primitiveFor(valueClass));
      }
    }
    return null;
  }
}
