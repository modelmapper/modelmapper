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

import org.modelmapper.spi.MappingContext;

/**
 * Converts {@link Enum} instances.
 * 
 * @author Jonathan Halterman
 */
class EnumConverter extends AbstractConditionalConverter<Enum<?>, Enum<?>> {
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Enum<?> convert(MappingContext<Enum<?>, Enum<?>> context) {
    String name = context.getSource().name();

    if (name != null)
      try {
        return Enum.valueOf((Class) context.getDestinationType(), name);
      } catch (IllegalArgumentException ignore) {
      }

    return null;
  }

  public boolean supports(Class<?> sourceType, Class<?> destinationType) {
    return sourceType.isEnum() && destinationType.isEnum();
  }
}
