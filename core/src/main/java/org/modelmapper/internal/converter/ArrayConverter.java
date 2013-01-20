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

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.util.Collection;

import org.modelmapper.internal.util.Iterables;
import org.modelmapper.internal.util.Types;
import org.modelmapper.spi.MappingContext;

/**
 * Converts {@link Collection} and array instances to array instances.
 * 
 * @author Jonathan Halterman
 */
class ArrayConverter extends IterableConverter<Object, Object> {
  public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
    return Iterables.isIterable(sourceType) && destinationType.isArray() ? MatchResult.FULL
        : MatchResult.NONE;
  }

  @Override
  protected void setElement(Object destination, Object element, int index) {
    Array.set(destination, index, element);
  }

  @Override
  protected Class<?> getElementType(MappingContext<Object, Object> context) {
    if (context.getGenericDestinationType() instanceof GenericArrayType)
      return Types.rawTypeFor((GenericArrayType) context.getGenericDestinationType())
          .getComponentType();
    return context.getDestinationType().getComponentType();
  }

  @Override
  protected Object createDestination(MappingContext<Object, Object> context, int length) {
    Class<?> destType = context.getDestinationType();
    return Array.newInstance(destType.isArray() ? destType.getComponentType() : destType, length);
  }
}
