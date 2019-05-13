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
package org.modelmapper.builder;

import org.modelmapper.Condition;

/**
 * Represents mapping operations.
 *
 * @param <S> source type
 * @param <D> destination type
 *
 * @author Chun Han Hsiao
 */
public interface ConfigurableConditionExpression<S, D> extends ConfigurableProviderExpression<S, D> {

  /**
   * Uses {@code condition} to determine the mapping should fire or skip
   *
   * <pre>
   * {@code
   *   when(condition).<String>map(Src::getCustomer, Dest::setCustomer)
   *   with(ctx -> ctx.getSource() != null).<Customer>map(Src::getCustomer, Dest::setCustomer)
   * }
   * </pre>
   *
   * @param condition a condition to apply the mapping action should be invoked or not
   */
  ConfigurableProviderExpression<S, D> when(Condition<?, ?> condition);
}
