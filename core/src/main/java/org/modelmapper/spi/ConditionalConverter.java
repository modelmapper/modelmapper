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

import org.modelmapper.Converter;
import org.modelmapper.config.Configuration;

/**
 * Conditionally converts matching source objects to instances of destination type {@code D}.
 * 
 * @param <S> source type
 * @param <D> destination type
 * @author Jonathan Halterman
 */
public interface ConditionalConverter<S, D> extends Converter<S, D> {
  public enum MatchResult {
    /** Indicates that the source and destination types were matched. */
    FULL,
    /** Indicates that only the destination type was matched. */
    PARTIAL,
    /** Indicates that the destination type was not matched. */
    NONE;
  }

  /**
   * Determines whether the converter matches and supports conversion from {@code sourceType} to
   * {@code destinationType}.
   * <p>
   * In the case of a partial match, the converter is indicating that it does not recognise the
   * source type but that it may still be capable performing the conversion implicitly by parsing the
   * result of calling {@link Object#toString()} or by some other means which similarly does not
   * require knowledge of the type at compile time.
   * <p>
   * Implicit conversion may result in conversions which are susceptible to unexpected failure when
   * property types or formats change. Conversion of properties with partially matched types can be
   * disabled via {@link Configuration#setFullTypeMatchingRequired(boolean)}.
   * 
   * @param sourceType to match
   * @param destinationType to match
   * @return <ul>
   *         <li>{@link MatchResult#FULL} if {@code sourceType} and {@code destinationType} are
   *         matched</li>
   *         <li>{@link MatchResult#PARTIAL} if {@code destinationType} is matched</li>
   *         <li>{@link MatchResult#NONE} if {@code destinationType} is not matched</li>
   *         </ul>
   */
  MatchResult match(Class<?> sourceType, Class<?> destinationType);
}
