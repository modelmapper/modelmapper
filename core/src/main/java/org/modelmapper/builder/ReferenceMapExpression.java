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

import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.spi.SourceGetter;

/**
 * Represents mapping actions, to set a destination property from a source property,
 * or to skip a destination property.
 *
 * @param <S> source type
 * @param <D> destination type
 *
 * @author Chun Han Hsiao
 */
public interface ReferenceMapExpression<S, D> {
  /**
   * Map source getter value to destination setter
   *
   * <pre>
   * {@code
   *   <String>map(Src::getValue, Dest::setValue)
   *   <String>map(src -> srg.getCustomer().getAddress(), dest -> dest.getCustomer().setAddress())
   * }
   * </pre>
   *
   * @param sourceGetter a method reference to source getter
   * @param destinationSetter a method reference to destination setter
   * @param <V> the value type to set into destination
   */
  <V> void map(SourceGetter<S> sourceGetter, DestinationSetter<D, V> destinationSetter);

  /**
   * Skip destination property based on {@code destinationSetter}
   *
   * <pre>
   * {@code
   *   <String>skip(Dest::setValue)
   *   <String>map(dest -> dest.getCustomer().setAddress())
   * }
   * </pre>
   *
   * @param destinationSetter a method reference to destination setter
   * @param <V> the value type of the setter
   */
  <V> void skip(DestinationSetter<D, V> destinationSetter);
}
