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
import org.modelmapper.builder.ConfigurableConditionExpression;
import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.spi.SourceGetter;

/**
 * {@link ConfigurableConditionExpression} implementation
 *
 * @author Chun Han Hsiao
 */
class ConfigurableConditionExpressionImpl<S, D> implements ConfigurableConditionExpression<S, D> {
  TypeMapImpl<S, D> typeMap;
  private MappingOptions options = new MappingOptions();
  private final ReferenceMapExpressionImpl<S, D> expression;

  public ConfigurableConditionExpressionImpl(TypeMapImpl<S, D> typeMap) {
    this.typeMap = typeMap;
    this.expression = new ReferenceMapExpressionImpl<S, D>(typeMap, options);
  }

  public ConfigurableConditionExpression<S, D> using(Converter<?, ?> converter) {
    notNull(converter, "converter");

    options.converter = converter;
    return this;
  }

  public ConfigurableConditionExpression<S, D> with(Provider<?> provider) {
    notNull(provider, "provider");

    options.provider = provider;
    return this;
  }

  public ConfigurableConditionExpression<S, D> when(Condition<?, ?> condition) {
    notNull(condition, "condition");

    options.condition = condition;
    return this;
  }

  public <V> void map(SourceGetter<S> sourceGetter, DestinationSetter<D, V> destinationSetter) {
    notNull(sourceGetter, "sourceGetter");
    notNull(destinationSetter, "destinationSetter");
    expression.map(sourceGetter, destinationSetter);
  }

  public <V> void skip(DestinationSetter<D, V> destinationSetter) {
    notNull(destinationSetter, "destinationSetter");
    expression.skip(destinationSetter);
  }

  @Override
  public <V> void skip(SourceGetter<S> sourceGetter, DestinationSetter<D, V> destinationSetter) {
    notNull(sourceGetter, "sourceGetter");
    notNull(destinationSetter, "destinationSetter");
    expression.skip(sourceGetter, destinationSetter);
  }
}
