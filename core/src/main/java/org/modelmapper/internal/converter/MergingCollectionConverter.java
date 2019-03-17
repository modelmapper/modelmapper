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

import org.modelmapper.internal.util.Iterables;
import org.modelmapper.internal.util.MappingContextHelper;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;

import java.util.Collection;
import java.util.Iterator;

/**
 * Converts {@link Collection} and array instances to {@link Collection} instances.
 *
 * @author Jonathan Halterman
 */
public class MergingCollectionConverter implements ConditionalConverter<Object, Collection<Object>> {
  @Override
  public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
    return Iterables.isIterable(sourceType) && Collection.class.isAssignableFrom(destinationType) ? MatchResult.FULL
        : MatchResult.NONE;
  }

  @Override
  public Collection<Object> convert(MappingContext<Object, Collection<Object>> context) {
    Object source = context.getSource();
    if (source == null)
      return null;

    int sourceLength = Iterables.getLength(source);
    Collection<Object> originalDestination = context.getDestination();
    Collection<Object> destination = MappingContextHelper.createCollection(context);
    Class<?> elementType = MappingContextHelper.resolveDestinationGenericType(context);

    int index = 0;
    for (Iterator<Object> iterator = Iterables.iterator(source); iterator.hasNext(); index++) {
      Object sourceElement = iterator.next();
      Object element = null;
      if (originalDestination != null)
        element = Iterables.getElement(originalDestination, index);
      if (sourceElement != null) {
        MappingContext<?, ?> elementContext = element == null
            ? context.create(sourceElement, elementType)
            : context.create(sourceElement, element);
        element = context.getMappingEngine().map(elementContext);
      }
      destination.add(element);
    }
    for (Object element : Iterables.subIterable(originalDestination, sourceLength))
      destination.add(element);

    return destination;
  }
}
