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
package org.modelmapper;

import org.modelmapper.internal.Errors;
import org.modelmapper.spi.PropertyInfo;

import java.util.List;

/**
 * {@link Validator} implementations.
 * 
 * @author Jonathan Halterman
 */
public class Validators {
  /**
   * Validates that all top level source properties are mapped.
   */
  public static final Validator SOURCE_PROPERTIES_MAPPED = new Validator() {
    @SuppressWarnings("unused") private static final long serialVersionUID = 0;

    public void isValid(TypeMap<?, ?> typeMap) {
      final Errors error = new Errors();
      final List<PropertyInfo> unmappedSourceProperties = typeMap.getUnmappedSourceProperties();
      if (!unmappedSourceProperties.isEmpty()) {
        error.errorUnmappedSourceProperties(typeMap, unmappedSourceProperties);
      }
      error.throwValidationExceptionIfErrorsExist();
    }

    @Override
    public String toString() {
      return "sourcePropertiesMapped()";
    }
  };

  /**
   * Validates that <b>all</b> top level destination properties are mapped, or that a
   * {@code Converter} was set for the TypeMap.
   */
  public static final Validator DESTINATION_PROPERTIES_MAPPED = new Validator() {
    @SuppressWarnings("unused") private static final long serialVersionUID = 0;

    public void isValid(TypeMap<?, ?> typeMap) {
      final Errors error = new Errors();
      final List<PropertyInfo> unmappedDestinationProperties = typeMap.getUnmappedDestinationProperties();
      if (!unmappedDestinationProperties.isEmpty()) {
        error.errorUnmappedDestinationProperties(typeMap, unmappedDestinationProperties);
      }

      error.throwValidationExceptionIfErrorsExist();
    }

    @Override
    public String toString() {
      return "destinationPropertiesMapped()";
    }
  };

  public static final Validator[] ALL = {SOURCE_PROPERTIES_MAPPED, DESTINATION_PROPERTIES_MAPPED};
}
