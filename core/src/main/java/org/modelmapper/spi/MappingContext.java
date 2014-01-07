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

import java.lang.reflect.Type;

import org.modelmapper.TypeMap;

/**
 * Maintains context during the mapping of a source object of type {@code S} to a destination object
 * of type {@code D}.
 * 
 * @author Jonathan Halterman
 */
public interface MappingContext<S, D> {
  /**
   * Creates a new child MappingContext for the {@code source} and {@code destination} which
   * inherits all other information from the this MappingContext.
   * 
   * @param <CS> child source type
   * @param <CD> child destination type
   * @param source to map from
   * @param destination to map to
   * @return new child MappingContext
   * @throws IllegalArgumentException if {@code source} or {@code destination} are null
   */
  <CS, CD> MappingContext<CS, CD> create(CS source, CD destination);

  /**
   * Creates a new child MappingContext for the {@code source} and {@code destinationType} which
   * inherits all other information from the this MappingContext.
   * 
   * @param <CS> child source type
   * @param <CD> child destination type
   * @param source to map from
   * @param destinationType to map to
   * @return new child MappingContext
   * @throws IllegalArgumentException if {@code source} or {@code destinatinoType} are null
   */
  <CS, CD> MappingContext<CS, CD> create(CS source, Class<CD> destinationType);

  /**
   * Creates a new child MappingContext for the {@code source} and {@code destinationType} which
   * inherits all other information from the this MappingContext.
   * 
   * @param <CS> child source type
   * @param <CD> child destination type
   * @param source to map from
   * @param destinationType to map to
   * @return new child MappingContext
   * @throws IllegalArgumentException if {@code source} or {@code destinatinoType} are null
   */
  <CS, CD> MappingContext<CS, CD> create(CS source, Type destinationType);

  /**
   * Returns the destination object being mapped to or null if the destination has not yet been
   * initialized.
   */
  D getDestination();

  /**
   * Returns the destination type being mapped to.
   */
  Class<D> getDestinationType();

  /**
   * Returns the generic destination type.
   */
  Type getGenericDestinationType();

  /**
   * Returns the mapping associated with the mapping request else {@code null} if the request did
   * not originate from a {@code TypeMap}.
   */
  Mapping getMapping();

  /**
   * Returns the MappingEngine that initiated the mapping request.
   */
  MappingEngine getMappingEngine();

  /**
   * Returns the parent MappingContext from which the current MappingContext was created, else
   * {@code null} if there is no parent context.
   */
  MappingContext<?, ?> getParent();

  /**
   * Returns the source object being mapped from.
   **/
  S getSource();

  /**
   * Returns the source type being mapped from.
   */
  Class<S> getSourceType();

  /**
   * Returns the TypeMap associated with the mapping request else {@code null} if the request did
   * not originate from a TypeMap.
   */
  TypeMap<S, D> getTypeMap();

  /**
   * Returns the name of the TypeMap associated with the mapping request else {@code null} if the
   * request did not originate from a named TypeMap.
   */
  String getTypeMapName();
}
