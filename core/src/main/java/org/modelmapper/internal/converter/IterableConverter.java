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
import java.util.Collection;
import java.util.Iterator;

import org.modelmapper.internal.util.ArrayIterator;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;

/**
 * Support class for converting Iterable and psuedo-Iterable (array) instances. Implementors must
 * support source and destination types that are Iterable or array instances.
 * 
 * @author Jonathan Halterman
 */
abstract class IterableConverter<S, D> implements ConditionalConverter<S, D> {
  public D convert(MappingContext<S, D> context) {
    S source = context.getSource();
    if (source == null)
      return null;

    int sourceLength = getSourceLength(source);
    D destination = context.getDestination() == null ? createDestination(context, sourceLength)
        : context.getDestination();
    Class<?> elementType = getElementType(context);

    int index = 0;
    for (Iterator<Object> iterator = getSourceIterator(source); iterator.hasNext(); index++) {
      Object sourceElement = iterator.next();
      Object element = null;
      if (sourceElement != null) {
        MappingContext<?, ?> elementContext = context.create(sourceElement, elementType);
        element = context.getMappingEngine().map(elementContext);
      }

      setElement(destination, element, index);
    }

    return destination;
  }

  /**
   * Creates a destination instance for the {@code destinationType} where the destination supports
   * element {@code length}.
   */
  protected abstract D createDestination(MappingContext<S, D> context, int length);

  /**
   * Gets the contained element type for the {@code type} and nullable {@code genericType}.
   * 
   * @param type to retrieve element type for
   * @param genericType to retrieve element type for - maybe be null
   */
  protected Class<?> getElementType(MappingContext<S, D> context) {
    return Object.class;
  }

  /**
   * Gets an Iterator for the Iterable or psuedo-Iterable (array) {@code source}.
   */
  @SuppressWarnings("unchecked")
  protected Iterator<Object> getSourceIterator(S source) {
    return source.getClass().isArray() ? new ArrayIterator(source)
        : ((Iterable<Object>) source).iterator();
  }

  /**
   * Gets the source length of the Iterable or psuedo-Iterable (array) {@code source}.
   */
  protected int getSourceLength(S source) {
    return source.getClass().isArray() ? Array.getLength(source) : ((Collection<?>) source).size();
  }

  /**
   * Sets the {@code element} at the against the {@code destination} at the optional {@code index}.
   */
  protected abstract void setElement(D destination, Object element, int index);
}
