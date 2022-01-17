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

import net.jodah.typetools.TypeResolver;
import org.modelmapper.internal.util.Types;
import org.modelmapper.spi.*;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;

/**
 * Converts  {@link Optional} to {@link Optional}
 */
class OptionalConverter implements ConditionalConverter<Optional<?>, Optional<?>> {
  @Override
  public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
    return (Optional.class.equals(sourceType) && Optional.class.equals(destinationType))
        ? MatchResult.FULL
        : MatchResult.NONE;
  }

  @Override
  @SuppressWarnings("all")
  public Optional<?> convert(MappingContext<Optional<?>, Optional<?>> mappingContext) {
    Class<?> optionalType = getElementType(mappingContext);
    Optional<?> source = mappingContext.getSource();
    if (source == null) {
      return null;
    } else if (source.isPresent()) {
      MappingContext<?, ?> optionalContext = mappingContext.create(source.get(), optionalType);
      return Optional.ofNullable(mappingContext.getMappingEngine().map(optionalContext));
    } else {
      return Optional.empty();
    }
  }


  private Class<?> getElementType(MappingContext<Optional<?>, Optional<?>> mappingContext) {
    Mapping mapping = mappingContext.getMapping();
    if (mapping instanceof PropertyMapping) {
      PropertyInfo destInfo = mapping.getLastDestinationProperty();
      Class<?> elementType = TypeResolver.resolveRawArgument(destInfo.getGenericType(),
          destInfo.getInitialType());
      return elementType == TypeResolver.Unknown.class ? Object.class : elementType;
    } else if (mappingContext.getGenericDestinationType() instanceof ParameterizedType) {
      return Types.rawTypeFor(((ParameterizedType) mappingContext.getGenericDestinationType()).getActualTypeArguments()[0]);
    }

    return Object.class;
  }

}
