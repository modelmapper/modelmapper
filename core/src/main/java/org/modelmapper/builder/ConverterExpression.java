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

import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;

/**
 * Expresses a Converter.
 * 
 * @param <S> source type
 * @param <D> destination type
 * 
 * @author Jonathan Halterman
 */
public interface ConverterExpression<S, D> extends SkipExpression<D> {
  /**
   * Specifies the {@code converter} to use for converting to the destination property hierarchy.
   * When used with deep mapping the {@code converter} should convert to an instance of the
   * <b>last</b> destination property. See the {@link PropertyMap EDSL examples}.
   * 
   * @param converter to use when mapping the property
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  MapExpression<D> using(Converter<?, ?> converter);
}
