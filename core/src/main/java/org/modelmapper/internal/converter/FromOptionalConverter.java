/*
 * Copyright 2022 the original author or authors.
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

import java.util.Optional;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;

/**
 * Converts  {@link Optional} to {@link Object}
 *
 * @author Chun Han Hsiao
 */
class FromOptionalConverter implements ConditionalConverter<Optional<Object>, Object> {
  @Override
  public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
    return (Optional.class.equals(sourceType) && !Optional.class.equals(destinationType))
        ? MatchResult.FULL
        : MatchResult.NONE;
  }

  @Override
  public Object convert(MappingContext<Optional<Object>, Object> mappingContext) {
    if (mappingContext.getSource() == null || !mappingContext.getSource().isPresent()) {
      return null;
    }

    MappingContext<Object, Object> propertyContext = mappingContext.create(
        mappingContext.getSource().get(), mappingContext.getDestinationType());
    return mappingContext.getMappingEngine().map(propertyContext);
  }
}
