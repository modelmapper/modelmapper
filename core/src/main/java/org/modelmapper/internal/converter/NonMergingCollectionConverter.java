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
public class NonMergingCollectionConverter implements ConditionalConverter<Object, Collection<Object>> {
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

    Collection<Object> destination = MappingContextHelper.createCollection(context);
    Class<?> elementType = MappingContextHelper.resolveDestinationGenericType(context);

    for (Iterator<Object> iterator = Iterables.iterator(source); iterator.hasNext();) {
      Object sourceElement = iterator.next();
      Object destinationElement = null;
      if (sourceElement != null) {
        MappingContext<?, ?> elementContext = context.create(sourceElement, elementType);
        destinationElement = context.getMappingEngine().map(elementContext);
      }
      destination.add(destinationElement);
    }

    return destination;
  }
}
