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
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.Provider;

/**
 * Expresses mapping.
 * 
 * @param <D> destination type
 * 
 * @author Jonathan Halterman
 */
public interface MapExpression<S, D> {
  /**
   * Defines a mapping to a destination. See the EDSL examples at {@link PropertyMap}.
   * 
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  D map();

  /**
   * Defines a mapping for the {@code subject}. See the EDSL examples at {@link PropertyMap}.
   * 
   * @param subject to map
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  D map(Object subject);

  /**
   * Defines a mapping from the {@code source} to the {@code destination}. See the EDSL examples at
   * {@link PropertyMap}.
   * 
   * @param source to map from
   * @param destination to map to
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  void map(Object source, Object destination);

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

  /**
   * Specifies the {@code provider} to be used for providing instances of the mapped property. When
   * used with deep mapping the {@code provider} should provide an instance of the <b>last</b>
   * destination property. See the {@link PropertyMap EDSL examples}.
   *
   * @param provider to use for providing the destination property
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  MapExpression<S, D> with(Provider<?> provider);
  /**
   * Specifies the {@code converter} to use for converting to the destination property hierarchy.
   * When used with deep mapping the {@code converter} should convert to an instance of the
   * <b>last</b> destination property. See the {@link PropertyMap EDSL examples}.
   *
   * @param converter to use when mapping the property
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  MapExpression<S, D> using(Converter<?, ?> converter);

  /**
   * Specifies the {@code condition} that must apply in order for mapping to take place for a
   * particular destination property hierarchy. See the {@link PropertyMap EDSL examples}.
   *
   * @param condition that must apply when mapping the property
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  MapExpression<S, D> when(Condition<?, ?> condition);
}
