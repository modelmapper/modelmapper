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

import org.modelmapper.internal.util.MappingContextHelper;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;

import java.util.Optional;

/**
 * Converts  {@link Object} to {@link Optional}
 *
 * @author Chun Han Hsiao
 */
class ToOptionalConverter implements ConditionalConverter<Object, Optional<Object>> {
  @Override
  public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
    return (!Optional.class.equals(sourceType) && Optional.class.equals(destinationType))
        ? MatchResult.FULL
        : MatchResult.NONE;
  }

  @Override
  public Optional<Object> convert(MappingContext<Object, Optional<Object>> mappingContext) {
    if (mappingContext.getSource() == null) {
      return Optional.empty();
    }

    MappingContext<?, ?> propertyContext = mappingContext.create(
        mappingContext.getSource(), MappingContextHelper.resolveDestinationGenericType(mappingContext));
    Object destination = mappingContext.getMappingEngine().map(propertyContext);
    return Optional.ofNullable(destination);
  }
}
