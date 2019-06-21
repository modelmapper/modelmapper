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
import org.modelmapper.Provider;

/**
 * Expresses a Provider.
 * 
 * @param <S> source type
 * @param <D> destination type
 * 
 * @author Jonathan Halterman
 */
public interface ProviderExpression<S, D> extends ConverterExpression<S, D> {
  /**
   * Specifies the {@code provider} to be used for providing instances of the mapped property. When
   * used with deep mapping the {@code provider} should provide an instance of the <b>last</b>
   * destination property. See the {@link PropertyMap EDSL examples}.
   * 
   * @param provider to use for providing the destination property
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  ConverterExpression<S, D> with(Provider<?> provider);
}
