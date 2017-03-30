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
import static org.modelmapper.internal.util.Assert.notNull;

import org.modelmapper.Condition;
import org.modelmapper.Converter;
import org.modelmapper.Provider;
import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.builder.ReferenceMapExpression;
import org.modelmapper.spi.SourceGetter;
import org.modelmapper.builder.ConfigurableMapExpression;

/**
 * {@link ConfigurableMapExpression} implementation
 *
 * @author Chun Han Hsiao
 */
class ConfigurableMapExpressionImpl<S, D> implements ConfigurableMapExpression<S, D> {
  TypeMapImpl<S, D> typeMap;

  public ConfigurableMapExpressionImpl(TypeMapImpl<S, D> typeMap) {
    this.typeMap = typeMap;
  }

  public ReferenceMapExpression<S, D> using(Converter<?, ?> converter) {
    notNull(converter, "converter");

    MappingOptions options = new MappingOptions();
    options.converter = converter;
    return new ReferenceMapExpressionImpl<S, D>(typeMap, options);
  }

  public ReferenceMapExpression<S, D> with(Provider<?> provider) {
    notNull(provider, "provider");

    MappingOptions options = new MappingOptions();
    options.provider = provider;
    return new ReferenceMapExpressionImpl<S, D>(typeMap, options);
  }

  public ReferenceMapExpression<S, D> when(Condition<?, ?> condition) {
    notNull(condition, "condition");

    MappingOptions options = new MappingOptions();
    options.condition = condition;
    return new ReferenceMapExpressionImpl<S, D>(typeMap, options);
  }

  public <V> void map(SourceGetter<S> sourceGetter, DestinationSetter<D, V> destinationSetter) {
    notNull(sourceGetter, "sourceGetter");
    notNull(destinationSetter, "destinationSetter");
    new ReferenceMapExpressionImpl<S, D>(typeMap).map(sourceGetter, destinationSetter);
  }

  public <V> void skip(DestinationSetter<D, V> destinationSetter) {
    notNull(destinationSetter, "destinationSetter");
    new ReferenceMapExpressionImpl<S, D>(typeMap).skip(destinationSetter);
  }
}
