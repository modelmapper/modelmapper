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

import java.util.Collection;
import java.util.Map;

/**
 * Writes values to a destination. Allows for integration with 3rd party libraries.
 * 
 * @param <T> destination type
 * @author Chun Han Hsiao
 */
public interface ValueWriter<T> {
  /**
   * Returns the value from the {@code destination} object for the {@code memberName}.
   * 
   * @throws IllegalArgumentException if the {@code memberName} is invalid for the {@code destination}
   */
  void setValue(T destination, Object value, String memberName);

  /**
   * Returns the {@link Member} from the {@code destination} within given {@code memberName}.
   *
   * @param destinationType the destination type
   * @param memberName the name of member
   */
  Member<T> getMember(Class<T> destinationType, String memberName);

  /**
   * Returns all member names for the {@code destinationType} object, else {@code null} if the destination has no
   * members.
   */
  Collection<String> memberNames(Class<T> destinationType);

  /**
   * Returns whether the value writer is supporting on resolving the members.
   *
   * @return {@code true} if the value writer  is supporting on resolving the members; otherwise, return {@code false}
   */
  boolean isResolveMembersSupport();

  /**
   * A member of a given destination
   *
   * @param <T> destination type
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
    public Class<T> getOrigin() {
      return null;
    }

    /**
     * Set the value.
     *
     * @param destination the destination
     * @param value the value
     */
    public abstract void setValue(T destination, Object value);
  }
}
