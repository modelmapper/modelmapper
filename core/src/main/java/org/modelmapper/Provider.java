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
package org.modelmapper;

/**
 * Provides instances of type {@code T}.
 * 
 * @param <T> type to provide
 * 
 * @author Jonathan Halterman
 */
public interface Provider<T> {
  /**
   * Contains provision request information.
   */
  public interface ProvisionRequest<T> {
    /**
     * Returns the type being requested.
     */
    Class<T> getRequestedType();

    /** Returns the source object being mapped from. */
    Object getSource();
  };

  /**
   * Returns an instance of the requested type {@code T} else {@code null} if the requested type
   * cannot be provided. If {@code null} is returned ModelMapper will construct the requested type.
   * 
   * @param request information
   * @throws MappingException if an error occurs while providing the requested type
   */
  T get(ProvisionRequest<T> request);
}
