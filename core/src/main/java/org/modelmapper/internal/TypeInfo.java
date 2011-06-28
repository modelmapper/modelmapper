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
package org.modelmapper.internal;

import java.util.Map;

import org.modelmapper.config.Configuration;

/**
 * Contains type information for a particular type and
 * {@link org.modelmapper.config.Configuration}.
 * 
 * @param <T> type providing information for
 * 
 * @author Jonathan Halterman
 */
interface TypeInfo<T> {
  /**
   * Returns a map of accessors representing fields and methods as matched according to the
   * MatchingConfiguration used to construct the TypeInfo.
   */
  Map<String, Accessor> getAccessors();

  /**
   * Returns a map of mutators representing fields and methods as matched according to the
   * MatchingConfiguration used to construct the TypeInfo.
   */
  Map<String, Mutator> getMutators();

  /**
   * Returns the type that this object provides information for.
   */
  Class<T> getType();

  /**
   * Returns the configuration that this type's information is effected by.
   */
  Configuration getConfiguration();
}
