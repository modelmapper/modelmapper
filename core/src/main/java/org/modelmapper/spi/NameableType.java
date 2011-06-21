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
package org.modelmapper.spi;

/**
 * Nameable types.
 * 
 * @author Jonathan Halterman
 */
public enum NameableType {
  CLASS, METHOD, FIELD;

  /**
   * Returns a NameableType for the corresponding {@code propertyType}, else {@link #CLASS}.
   */
  public static NameableType forPropertyType(PropertyType propertyType) {
    return PropertyType.FIELD.equals(propertyType) ? FIELD
        : PropertyType.METHOD.equals(propertyType) ? METHOD : CLASS;
  }
}