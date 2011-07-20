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
package org.modelmapper.internal.converter;

import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;

/**
 * Converts to {@link String} instances.
 * 
 * @author Jonathan Halterman
 */
class StringConverter implements ConditionalConverter<Object, String> {
  public String convert(MappingContext<Object, String> context) {
    return context.getSource().toString();
  }

  public boolean supports(Class<?> sourceType, Class<?> destinationType) {
    return destinationType == String.class;
  }

  public boolean supportsSource(Class<?> sourceType) {
    return sourceType == String.class;
  }

  public boolean verifiesSource() {
    return false;
  }
}
