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

import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.ConditionalConverter.MatchResult;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Jonathan Halterman
 */
public final class ConverterStore {
  private static final ConditionalConverter<?, ?>[] DEFAULT_CONVERTERS = new ConditionalConverter<?, ?>[] {
          new ArrayConverter(), new MergingCollectionConverter(), new MapConverter(),
          new AssignableConverter(), new StringConverter(), new EnumConverter(), new NumberConverter(),
          new BooleanConverter(), new CharacterConverter(), new DateConverter(),
          new CalendarConverter(), new FromOptionalConverter(), new OptionalConverter(),
          new ToOptionalConverter(),
  };

  private final List<ConditionalConverter<?, ?>> converters;

  public ConverterStore() {
    this(new CopyOnWriteArrayList<ConditionalConverter<?, ?>>(DEFAULT_CONVERTERS));
  }

  ConverterStore(List<ConditionalConverter<?, ?>> converters) {
    this.converters = converters;
  }

  /**
   * Returns the first converter that supports converting from {@code sourceType} to
   * {@code destinationType}. It will select converter that was full match first.
   * Then it will select {@code MatchResult.PARTIAL} if there is no full match converter
   * exists.
   */
  @SuppressWarnings("unchecked")
  public <S, D> ConditionalConverter<S, D> getFirstSupported(Class<?> sourceType,
      Class<?> destinationType) {
    ConditionalConverter<S, D> firstPartialMatchConverter = null;

    for (ConditionalConverter<?, ?> converter : converters) {
      MatchResult matchResult = converter.match(sourceType, destinationType);
      if (matchResult == MatchResult.FULL)
        return (ConditionalConverter<S, D>) converter;
      if (firstPartialMatchConverter == null
          && matchResult == MatchResult.PARTIAL)
        firstPartialMatchConverter = (ConditionalConverter<S, D>) converter;
    }
    return firstPartialMatchConverter;
  }

  public List<ConditionalConverter<?, ?>> getConverters() {
    return converters;
  }

  public ConverterStore removeConverter(Class<? extends ConditionalConverter<?, ?>> converterClass) {
    ConditionalConverter<?, ?> matchConverter = getConverterByType(converterClass);
    if (matchConverter != null)
      converters.remove(matchConverter);
    return this;
  }

  public boolean hasConverter(Class<? extends ConditionalConverter<?, ?>> converterClass) {
    return getConverterByType(converterClass) != null;
  }

  public ConverterStore addConverter(ConditionalConverter<?, ?> converter) {
    converters.add(converter);
    return this;
  }

  /**
   * Replaces the converter of specified class with the given instance.
   * <p>
   * If the converter to replace is not found than no replace is performed,
   * the converters are left untouched.
   *
   * @param converterClass to replace
   * @param converter instance with which to replace it
   * @return converter store itself
   */
  public ConverterStore replaceConverter(Class<? extends ConditionalConverter<?, ?>> converterClass, ConditionalConverter<?, ?> converter) {
      ConditionalConverter<?, ?> matchConverter = getConverterByType(converterClass);
      if (matchConverter != null) {
        int idx = converters.indexOf(matchConverter);
        if (idx != -1) {
          // because of concurrency do not use List#set(int, T obj) to avoid to replace a wrong converter
          converters.remove(matchConverter);
          converters.add(idx, converter);
        }
      }
      return this;
  }

  private ConditionalConverter<?, ?> getConverterByType(Class<? extends ConditionalConverter<?, ?>> converterClass) {
    for (ConditionalConverter<?, ?> converter : converters) {
      if (converter.getClass().equals(converterClass))
        return converter;
    }
    return null;
  }

}
