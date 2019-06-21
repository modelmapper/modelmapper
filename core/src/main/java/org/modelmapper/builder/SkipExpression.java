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

import org.modelmapper.PropertyMap;

/**
 * Expresses a mapping that is skipped.
 * 
 * @param <D> destination type
 * 
 * @author Jonathan Halterman
 */
public interface SkipExpression<D> extends MapExpression<D> {
  /**
   * Specifies that mapping for the destination property be skipped during the mapping process. See
   * the EDSL examples at {@link PropertyMap}.
   * 
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  D skip();

  /**
   * Specifies that mapping to the {@code destination} be skipped during the mapping process. See
   * the EDSL examples at {@link PropertyMap}.
   * 
   * @param destination to skip
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  void skip(Object destination);

  /**
   * Specifies that mapping from the {@code source} to the {@code destination} be skipped during the
   * mapping process. See the EDSL examples at {@link PropertyMap}.
   * 
   * @param source to skip
   * @param destination to skip
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  void skip(Object source, Object destination);
}
