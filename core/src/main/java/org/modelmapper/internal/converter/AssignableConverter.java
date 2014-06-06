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
 * Converts destination objects that are assignable from the source objects.
 * 
 * @author Jonathan Halterman
 */
public class AssignableConverter implements ConditionalConverter<Object, Object> {
  public Object convert(MappingContext<Object, Object> context) {
    return context.getDestination() == null ? context.getSource() : context.getDestination();
  }

  public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
    return destinationType.isAssignableFrom(sourceType) ? MatchResult.FULL : MatchResult.NONE;
  }
}
