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
  abstract class Member<T> {
    private Class<Object> valueType;

    /**
     * Creates a member contains nested value
     *
     * @param valueType the value type
     */
    @SuppressWarnings("unchecked")
    public Member(Class<?> valueType) {
      this.valueType = (Class<Object>) valueType;
    }

    public Class<Object> getValueType() {
      return valueType;
    }

    /**
     * The origin value of this member
     *
     * @return the origin value
     */
    public T getOrigin() {
      return null;
    }

    /**
     * Get the member from the source with given member name.
     *
     * @param source the source
     * @param memberName the member name
     * @return the member of the source
     */
    public abstract Object get(T source, String memberName);
  }
}
