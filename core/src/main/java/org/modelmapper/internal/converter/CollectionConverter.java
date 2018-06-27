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

import java.util.Collection;

import org.modelmapper.internal.util.Iterables;
import org.modelmapper.internal.util.MappingContextHelper;
import org.modelmapper.spi.MappingContext;

/**
 * Converts {@link Collection} and array instances to {@link Collection} instances.
 * 
 * @author Jonathan Halterman
 */
class CollectionConverter extends IterableConverter<Object, Collection<Object>> {
  public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
    return Iterables.isIterable(sourceType) && Collection.class.isAssignableFrom(destinationType) ? MatchResult.FULL
        : MatchResult.NONE;
  }

  @Override
  protected Collection<Object> createDestination(
      MappingContext<Object, Collection<Object>> context, int length) {
    return MappingContextHelper.createCollection(context, length);
  }

  @Override
  protected Class<?> getElementType(MappingContext<Object, Collection<Object>> context) {
    return MappingContextHelper.resolveCollectionElementType(context);
  }

  @Override
  protected void setElement(Collection<Object> destination, Object element, int index) {
    if (destination.size() < index + 1)
      destination.add(element);
  }
}
