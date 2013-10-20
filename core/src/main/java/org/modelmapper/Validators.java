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
public enum Validators implements Validator {
  
  /**
   * Validates that all top level source properties are mapped.
   */
  SOURCE_PROPERTIES_MAPPED {
    public void validate(TypeMap<?, ?> typeMap) {
      final List<PropertyInfo> properties = typeMap.getUnmappedSourceProperties();

      if (!properties.isEmpty()) {
        final Errors errors = new Errors();
        errors.errorUnmappedSourceProperties(typeMap, properties);
        errors.throwValidationExceptionIfErrorsExist();
      }
    }
  },

  /**
   * Validates that <b>all</b> top level destination properties are mapped, or that a
   * {@code Converter} was set for the TypeMap.
   */
  DESTINATION_PROPERTIES_MAPPED {
    public void validate(TypeMap<?, ?> typeMap) {
      final List<PropertyInfo> properties = typeMap.getUnmappedDestinationProperties();
      
      if (!properties.isEmpty()) {
        final Errors errors = new Errors();
        errors.errorUnmappedDestinationProperties(typeMap, properties);
        errors.throwValidationExceptionIfErrorsExist();
      }
    }
  },

  /**
   * Valides that <b>both</b>, <b>all</b> top level source and <b>all</b> top level destination properties
   * are mapped.
   */
  ALL_PROPERTIES_MAPPED {
    public void validate(TypeMap<?, ?> typeMap) {
      final Errors errors = new Errors();
      
      try {
        SOURCE_PROPERTIES_MAPPED.validate(typeMap);
      } catch (ValidationException e) {
        errors.merge(e.getErrorMessages());
      }
      
      try {
        DESTINATION_PROPERTIES_MAPPED.validate(typeMap);
      } catch (ValidationException e) {
        errors.merge(e.getErrorMessages());
      }
     
      errors.throwValidationExceptionIfErrorsExist();
    }
  }
}
