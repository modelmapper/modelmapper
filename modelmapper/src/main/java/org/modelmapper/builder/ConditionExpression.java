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
package org.modelmapper.builder;

import org.modelmapper.Condition;
import org.modelmapper.PropertyMap;

/**
 * Expresses a Condition.
 * 
 * @param <S> source type
 * @param <D> destination type
 * 
 * @author Jonathan Halterman
 */
public interface ConditionExpression<S, D> extends ProviderExpression<S, D> {
  /**
   * Specifies the {@code condition} that must apply in order for mapping to take place for a
   * particular destination property hierarchy. See the {@link PropertyMap EDSL examples}.
   * 
   * @param condition that must apply when mapping the property
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  ConditionExpression<S, D> when(Condition<?, ?> condition);
}
