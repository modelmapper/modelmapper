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

import java.util.List;

import org.modelmapper.Condition;
import org.modelmapper.Converter;
import org.modelmapper.Provider;

/**
 * Mapping to a destination property hierarchy.
 * 
 * @author Jonathan Halterman
 */
public interface Mapping {
  /**
   * Gets the condition that to be satisfied before this mapping can be used to perform a mapping.
   * 
   * @return condition, else null if none was configured
   */
  Condition<?, ?> getCondition();

  /**
   * Gets the Converter to be used when performing a mapping.
   * 
   * @return converter, else null if none was configured
   */
  Converter<?, ?> getConverter();

  /**
   * Get the hierarchy of destination property info.
   */
  List<? extends PropertyInfo> getDestinationProperties();

  /**
   * Gets the last property info in the destination properties hierarchy.
   */
  PropertyInfo getLastDestinationProperty();

  /**
   * Gets the Provider to use for providing instances of the first destination type.
   * 
   * @return converter, else null if none was configured
   */
  Provider<?> getProvider();

  /**
   * Returns whether the destination should be skipped when performing a mapping.
   */
  boolean isSkipped();
}
