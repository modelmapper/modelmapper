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
package org.modelmapper.convention;

import org.modelmapper.spi.PropertyType;
import org.modelmapper.spi.NamingConvention;

/**
 * {@link NamingConvention} implementations.
 * 
 * @author Jonathan Halterman
 */
public class NamingConventions {
  /**
   * JavaBeans naming convention for accessors.
   */
  public static final NamingConvention JAVABEANS_ACCESSOR = new NamingConvention() {
    public boolean applies(String propertyName, PropertyType propertyType) {
      return PropertyType.FIELD.equals(propertyType)
          || (propertyName.startsWith("get") && propertyName.length() > 3)
          || (propertyName.startsWith("is") && propertyName.length() > 2);
    }

    @Override
    public String toString() {
      return "Javabeans Accessor";
    }
  };

  /**
   * JavaBeans naming convention for mutators.
   */
  public static final NamingConvention JAVABEANS_MUTATOR = new NamingConvention() {
    public boolean applies(String propertyName, PropertyType propertyType) {
      return PropertyType.FIELD.equals(propertyType)
          || (propertyName.startsWith("set") && propertyName.length() > 3);
    }

    @Override
    public String toString() {
      return "Javabeans Mutator";
    }
  };

  /**
   * Represents no naming convention. This convention
   * {@link NamingConvention#applies(String, PropertyType) applies} to all property names, allowing
   * all properties to be eligible for matching.
   */
  public static final NamingConvention NONE = new NamingConvention() {
    public boolean applies(String propertyName, PropertyType propertyType) {
      return true;
    }

    @Override
    public String toString() {
      return "None";
    }
  };
}
