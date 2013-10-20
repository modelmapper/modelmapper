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

/**
 * Nameable types.
 * 
 * @author Jonathan Halterman
 */
public enum NameableType {
  /** A property that represents a {@link java.lang.Class} */
  CLASS,
  /** A property that represents a {@link java.lang.reflect.Method} */
  METHOD,
  /** A property that represents a {@link java.lang.reflect.Field} */
  FIELD,
  /** A nameable type obtained from a {@link org.modelmapper.spi.ValueReader} */
  GENERIC;

  /**
   * Returns a NameableType for the corresponding {@code propertyType}, else {@link #CLASS}.
   */
  public static NameableType forPropertyType(PropertyType propertyType) {
    if (PropertyType.FIELD.equals(propertyType))
      return FIELD;
    if (PropertyType.METHOD.equals(propertyType))
      return METHOD;
    if (PropertyType.GENERIC.equals(propertyType))
      return GENERIC;
    return CLASS;
  }
}