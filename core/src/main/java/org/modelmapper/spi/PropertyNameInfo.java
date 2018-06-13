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

import java.util.List;

/**
 * Encapsulates property name information to be use for determining whether a hierarchy of source
 * and destination properties match.
 *
 *  @author Jonathan Halterman
 */
public interface PropertyNameInfo {
  /**
   * Returns the destination properties.
   */
  List<PropertyInfo> getDestinationProperties();

  /**
   * Returns transformed name tokens for the destination property.
   */
  List<Tokens> getDestinationPropertyTokens();

  /**
   * Returns transformed name tokens for the source's declaring class.
   */
  Tokens getSourceClassTokens();

  /**
   * Returns the source properties.
   */
  List<PropertyInfo> getSourceProperties();

  /**
   * Returns transformed name tokens for the source property.
   */
  List<Tokens> getSourcePropertyTokens();

  /**
   * Returns transformed name tokens for each source property type.
   */
  List<Tokens> getSourcePropertyTypeTokens();
}
