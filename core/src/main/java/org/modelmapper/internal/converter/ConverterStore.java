/**
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
package org.modelmapper.internal.converter;

import java.util.Arrays;
import java.util.List;

import org.modelmapper.spi.ConditionalConverter;

/**
 * @author Jonathan Halterman
 */
public final class ConverterStore {
  private final List<ConditionalConverter<?, ?>> converters = Arrays
      .<ConditionalConverter<?, ?>>asList(new StringConverter(), new EnumConverter(),
          new ArrayConverter(), new CollectionConverter(), new MapConverter(),
          new AssignableConverter(), new NumberConverter(), new BooleanConverter(),
          new CharacterConverter(), new DateConverter());

  public void add(ConditionalConverter<?, ?> converter) {
    if (!converters.contains(converter))
      converters.add(0, converter);
  }

  /**
   * Returns the first converter that supports converting from {@code sourceType} to
   * {@code destinationType}.
   */
  @SuppressWarnings("unchecked")
  public <S, D> ConditionalConverter<S, D> getFirstSupported(Class<?> sourceType,
      Class<?> destinationType) {
    for (ConditionalConverter<?, ?> converter : converters)
      if (converter.supports(sourceType, destinationType))
        return (ConditionalConverter<S, D>) converter;
    return null;
  }
}
