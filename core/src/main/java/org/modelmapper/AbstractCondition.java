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
package org.modelmapper;

import org.modelmapper.internal.util.Assert;

/**
 * Condition support class.
 * 
 * @param <S> source type
 * @param <D> destination type
 * 
 * @author Jonathan Halterman
 */
public abstract class AbstractCondition<S, D> implements Condition<S, D> {
  /**
   * Returns a new condition that applies if {@code this} AND the given {@code condition} apply.
   * 
   * @return new condition
   * @throws IllegalArgumentException if {@code condition} is null
   */
  public Condition<S, D> and(Condition<S, D> condition) {
    Assert.notNull(condition, "condition");
    return Conditions.and(this, condition);
  }

  /**
   * Returns a new condition that applies if {@code this} OR the given {@code condition} apply.
   * 
   * @return new condition
   * @throws IllegalArgumentException if {@code condition} is null
   */
  public Condition<S, D> or(Condition<S, D> condition) {
    Assert.notNull(condition, "condition");
    return Conditions.or(this, condition);
  }
}
