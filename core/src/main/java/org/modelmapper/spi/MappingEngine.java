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
package org.modelmapper.spi;

/**
 * Engine that performs mapping operations.
 * 
 * @author Jonathan Halterman
 */
public interface MappingEngine {
  /**
   * Maps an instance of type {@code D} for the {@code context}.
   * 
   * @param <S> source type
   * @param <D> destination type
   * @param context to map for
   * @return fully mapped instance of {@code D}
   */
  <S, D> D map(MappingContext<S, D> context);

  /**
   * Creates an instance of the destination type for the {@code context}, capturing any errors that
   * may occur during instantiation.
   */
  <S, D> D createDestination(MappingContext<S, D> context);
}
