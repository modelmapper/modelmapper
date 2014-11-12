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
package org.modelmapper.internal.converter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.ConditionalConverter.MatchResult;

/**
 * @author Jonathan Halterman
 */
public final class ConverterStore {
  private static final ConditionalConverter<?, ?>[] DEFAULT_CONVERTERS = new ConditionalConverter<?, ?>[] {
      new ArrayConverter(), new CollectionConverter(), new MapConverter(),
      new AssignableConverter(), new StringConverter(), new EnumConverter(), new NumberConverter(),
      new BooleanConverter(), new CharacterConverter(), new DateConverter(),
      new CalendarConverter() };
  private final List<ConditionalConverter<?, ?>> converters = new CopyOnWriteArrayList<ConditionalConverter<?, ?>>();

  public ConverterStore() {
    for (ConditionalConverter<?, ?> converter : DEFAULT_CONVERTERS)
      converters.add(converter);
  }

  /**
   * Returns the first converter that supports converting from {@code sourceType} to
   * {@code destinationType}.
   */
  @SuppressWarnings("unchecked")
  public <S, D> ConditionalConverter<S, D> getFirstSupported(Class<?> sourceType,
      Class<?> destinationType) {
    for (ConditionalConverter<?, ?> converter : converters)
      if (!MatchResult.NONE.equals(converter.match(sourceType, destinationType)))
        return (ConditionalConverter<S, D>) converter;
    return null;
  }

  public List<ConditionalConverter<?, ?>> getConverters() {
    return converters;
  }
}
