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
package org.modelmapper.spi;

/**
 * Represents an operation for setting a value into destination
 *
 * @param <D> destination type
 * @param <V> argument type of the setter
 *
 * @author Chun Han Hsiao
 */
public interface DestinationSetter<D, V> {
  /**
   * Performs this operation to set the value into destination
   *
   * @param destination the instance of destination
   * @param value the value from source
   */
  void accept(D destination, V value);
}
