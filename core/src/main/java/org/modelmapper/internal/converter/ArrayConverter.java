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

import java.util.Iterator;
import org.modelmapper.internal.util.Iterables;
import org.modelmapper.internal.util.Types;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;

/**
 * Converts {@link Collection} and array instances to array instances.
 * 
 * @author Jonathan Halterman
 */
class ArrayConverter implements ConditionalConverter<Object, Object> {
  @Override
  public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
    return Iterables.isIterable(sourceType) && destinationType.isArray() ? MatchResult.FULL
        : MatchResult.NONE;
  }

  @Override
  public Object convert(MappingContext<Object, Object> context) {
    Object source = context.getSource();
    if (source == null)
      return null;

    boolean destinationProvided = context.getDestination() != null;
    Object destination = createDestination(context);

    Class<?> elementType = getElementType(context);
    int index = 0;
    for (Iterator<Object> iterator = Iterables.iterator(source); iterator.hasNext(); index++) {
      Object sourceElement = iterator.next();
      Object element = null;
      if (destinationProvided)
        element = Iterables.getElement(destination, index);
      if (sourceElement != null) {
        MappingContext<?, ?> elementContext = element == null
            ? context.create(sourceElement, elementType)
            : context.create(sourceElement, element);
        element = context.getMappingEngine().map(elementContext);
      }
      Array.set(destination, index, element);
    }

    return destination;
  }

  @SuppressWarnings("all")
  private Object createDestination(MappingContext<Object, Object> context) {
    int sourceLength = Iterables.getLength(context.getSource());
    int destinationLength = context.getDestination() != null ? Iterables.getLength(context.getDestination()) : 0;
    int newLength = Math.max(sourceLength, destinationLength);
    Object originalDestination = context.getDestination();

    Class<?> destType = context.getDestinationType();
    Object destination = Array.newInstance(destType.isArray() ? destType.getComponentType() : destType, newLength);
    if (originalDestination != null)
      System.arraycopy(originalDestination, 0, destination, 0, destinationLength);
    return destination;
  }

  private Class<?> getElementType(MappingContext<Object, Object> context) {
    if (context.getGenericDestinationType() instanceof GenericArrayType)
      return Types.rawTypeFor(context.getGenericDestinationType())
          .getComponentType();
    return context.getDestinationType().getComponentType();
  }
}
