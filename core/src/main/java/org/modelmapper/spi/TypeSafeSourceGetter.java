/*
 * Copyright 2018 the original author or authors.
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
 * Represents an operation for getting an property from source
 *
 * @param <S> source type
 * @param <P> property type
 *
 * @author Chun Han Hsiao
 */
public interface TypeSafeSourceGetter<S, P> {
  /**
   * Performs this operation to get a property from source
   *
   * @param source the source instance
   * @return the property from source
   */
  P get(S source);
}
