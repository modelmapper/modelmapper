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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.modelmapper.internal.util.Iterables;
import org.modelmapper.internal.util.Types;
import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.PropertyInfo;
import org.modelmapper.spi.PropertyMapping;

import net.jodah.typetools.TypeResolver;
import net.jodah.typetools.TypeResolver.Unknown;

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
    if (context.getDestinationType().isInterface())
      if (SortedSet.class.isAssignableFrom(context.getDestinationType()))
        return new TreeSet<Object>();
      else if (Set.class.isAssignableFrom(context.getDestinationType()))
        return new HashSet<Object>();
      else
        return new ArrayList<Object>(length);

    return context.getMappingEngine().createDestination(context);
  }

  @Override
  protected Class<?> getElementType(MappingContext<Object, Collection<Object>> context) {
    Mapping mapping = context.getMapping();
    if (mapping instanceof PropertyMapping) {
      PropertyInfo destInfo = ((PropertyMapping) mapping).getLastDestinationProperty();
      Class<?> elementType = TypeResolver.resolveRawArgument(destInfo.getGenericType(),
          destInfo.getInitialType());
      return elementType == Unknown.class ? Object.class : elementType;
    } else if (context.getGenericDestinationType() instanceof ParameterizedType)
      return Types.rawTypeFor(((ParameterizedType) context.getGenericDestinationType()).getActualTypeArguments()[0]);

    return Object.class;
  }

  @Override
  protected void setElement(Collection<Object> destination, Object element, int index) {
    destination.add(element);
  }
}
