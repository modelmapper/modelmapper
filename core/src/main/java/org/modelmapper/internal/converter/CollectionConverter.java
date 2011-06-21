/**
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.modelmapper.internal.util.Iterables;
import org.modelmapper.internal.util.TypeResolver;
import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.PropertyInfo;
import org.modelmapper.spi.PropertyMapping;

/**
 * Converts {@link Collection} and array instances to {@link Collection} instances.
 * 
 * @author Jonathan Halterman
 */
class CollectionConverter extends IterableConverter<Object, Collection<Object>> {
  @Override
  public boolean supports(Class<?> sourceType, Class<?> destinationType) {
    return Collection.class.isAssignableFrom(destinationType) && Iterables.isIterable(sourceType);
  }

  @Override
  protected Class<?> getElementType(MappingContext<Object, Collection<Object>> context) {
    Mapping mapping = context.getMapping();
    if (mapping instanceof PropertyMapping) {
      PropertyInfo destInfo = ((PropertyMapping) mapping).getLastDestinationProperty();
      return TypeResolver.resolveArgument(destInfo.getGenericType(), destInfo.getMember()
          .getDeclaringClass());
    }

    return Object.class;
  }

  @Override
  protected void setElement(Collection<Object> destination, Object element, int index) {
    destination.add(element);
  }

  @Override
  protected Collection<Object> createDestination(
      MappingContext<Object, Collection<Object>> context, int length) {
    if (context.getDestinationType().isInterface())
      if (Set.class.isAssignableFrom(context.getDestinationType()))
        return new HashSet<Object>();
      else
        return new ArrayList<Object>(length);

    return context.getMappingEngine().createDestination(context);
  }
}
