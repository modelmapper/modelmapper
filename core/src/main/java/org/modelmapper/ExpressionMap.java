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
package org.modelmapper;

import org.modelmapper.builder.ConfigurableMapExpression;

/**
 * Represents an operation that accepts {@code mapping} and use functions
 * defined in {@link ConfigurableMapExpression} to configure a {@link TypeMap}
 *
 * <pre>
 *   modelMapper.addMappings(mapper -> {
 *     mapper.map(Src::getA, Dest::setB);
 *     mapper.using(converter).map(Src::getC, Dest::setD);
 *   })
 * </pre>
 *
 * @param <S> source type
 * @param <D> destination type
 *
 * @author Chun Han Hsiao
 */
public interface ExpressionMap<S, D> {
  /**
   * Performs the operation to configure {@link TypeMap}
   */
  void configure(ConfigurableMapExpression<S, D> mapping);
}
