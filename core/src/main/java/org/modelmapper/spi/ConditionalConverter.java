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
    SOURCE_AND_DEST,
    /** Indicates that the destination type was matched. */
    DEST,
    /** Indicates that the destination type was not matched. */
    NO_MATCH;
  }

  /**
   * Determines whether the converter support conversion from {@code sourceType} to
   * {@code destinationType}.
   * 
   * @param sourceType to evaluate
   * @param destinationType to evaluate
   * @return <ul>
   *         <li>{@link MatchResult#SOURCE_AND_DEST} if {@code sourceType} and
   *         {@code destinationType} are supported</li>
   *         <li>{@link MatchResult#DEST} if {@code destinationType} is supported</li>
   *         <li>{@link MatchResult#NO_MATCH} if {@code destinationType} is not supported</li>
   *         </ul>
   */
  MatchResult match(Class<?> sourceType, Class<?> destinationType);
}
