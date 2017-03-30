/*
 * Copyright 2013 the original author or authors.
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

import java.util.Collection;

/**
 * Reads values from a source. Allows for integration with 3rd party libraries.
 * 
 * @param <T> source type
 * @author Jonathan Halterman
 */
public interface ValueReader<T> {
  /**
   * Returns the value from the {@code source} object for the {@code memberName}.
   * 
   * @throws IllegalArgumentException if the {@code memberName} is invalid for the {@code source}
   */
  Object get(T source, String memberName);

  /**
   * Returns the {@link Member} from the {@code source} within given {@code memberName}.
   *
   * @param source the source object
   * @param memberName the name of member
   */
  Member<T> getMember(T source, String memberName);

  /**
   * Returns all member names for the {@code source} object, else {@code null} if the source has no
   * members.
   */
  Collection<String> memberNames(T source);


  /**
   * A member of a given source
   *
   * @param <T> source type
   */
  class Member<T> {
    private ValueReader<T> valueReader;
    private Class<Object> valueType;
    private T nestedValue;

    /**
     * Creates a member doesn't contain nested value
     *
     * @param valueReader the ValueReader itself
     * @param valueType the value type
     */
    public Member(ValueReader<T> valueReader, Class<?> valueType) {
      this(valueReader, valueType, null);
    }

    /**
     * Creates a member contains nested value
     *
     * @param valueReader the ValueReader itself
     * @param valueType the value type
     * @param nestedValue the nested value
     */
    @SuppressWarnings("unchecked")
    public Member(ValueReader<T> valueReader, Class<?> valueType, T nestedValue) {
      this.valueReader = valueReader;
      this.valueType = (Class<Object>) valueType;
      this.nestedValue = nestedValue;
    }

    public ValueReader<T> getValueReader() {
      return valueReader;
    }

    public Class<Object> getValueType() {
      return valueType;
    }

    public T getNestedValue() {
      return nestedValue;
    }
  }
}
