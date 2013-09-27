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

import java.lang.reflect.ParameterizedType;

import org.modelmapper.internal.util.Optionals;
import org.modelmapper.internal.util.TypeResolver;
import org.modelmapper.internal.util.Types;

import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.PropertyInfo;
import org.modelmapper.spi.PropertyMapping;

public class OptionalConverter implements ConditionalConverter<Object, Object> {

  public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
    if (Optionals.isOptional(destinationType)) {
      return MatchResult.FULL;
    } else {
      return MatchResult.NONE;
    }
  }

  private Class<?> getElementType(MappingContext<Object, Object> context) {
    Mapping mapping = context.getMapping();
    if (mapping instanceof PropertyMapping) {
      PropertyInfo destInfo = ((PropertyMapping) mapping).getLastDestinationProperty();
      Class<?> elementType = TypeResolver.resolveArgument(destInfo.getGenericType(),
              destInfo.getInitialType());
      return elementType == TypeResolver.Unknown.class ? Object.class : elementType;
    } else if (context.getGenericDestinationType() instanceof ParameterizedType) {
      return Types.rawTypeFor(((ParameterizedType) context.getGenericDestinationType()).getActualTypeArguments()[0]);
    }

    return Object.class;
  }

  public Object convert(MappingContext<Object, Object> context) {
    Class<?> optionalType = getElementType(context);
    Object source = context.getSource();
    Object dest = null;
    if (source != null) {
      MappingContext<?, ?> optionalContext = context.create(source, optionalType);
      dest = context.getMappingEngine().map(optionalContext);
    }

    return Optionals.fromNullable(dest);
  }

}
