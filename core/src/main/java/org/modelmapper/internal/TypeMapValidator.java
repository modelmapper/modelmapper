/*
 * Copyright 2017 the original author or authors.
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
package org.modelmapper.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.modelmapper.TypeMap;
import org.modelmapper.internal.util.Types;
import org.modelmapper.spi.PropertyInfo;

/**
 * Validator that validates a given TypeMap is fully matched or not.
 *
 * @author Chun Han Hsiao
 */
public final class TypeMapValidator {

  /**
   * Returns unmapped properties of the given {@code typeMap}
   */
  public <S, D> List<PropertyInfo> getUnmappedProperties(TypeMap<S, D> typeMap) {
    return getUnmappedPropertiesInternal((TypeMapImpl<S, D>) typeMap);
  }

  private <S, D> List<PropertyInfo> getUnmappedPropertiesInternal(TypeMapImpl<S, D> typeMap) {
    PathProperties pathProperties = getDestinationProperties(typeMap);

    synchronized (typeMap.mappings) {
      for (Map.Entry<String, MappingImpl> entry : typeMap.mappings.entrySet()) {
        pathProperties.removeMatchPath(entry.getKey());
      }
    }

    return pathProperties.toList();
  }

  public <S, D> Errors validate(TypeMap<S, D> typeMap) {
    return validateInternal((TypeMapImpl<S, D>) typeMap);
  }

  private <S, D> Errors validateInternal(TypeMapImpl<S, D> typeMap) {
    Errors errors = new Errors();
    List<PropertyInfo> unmappedProperties = getUnmappedProperties(typeMap);
    if (!unmappedProperties.isEmpty())
      errors.errorUnmappedProperties(typeMap, unmappedProperties);

    return errors;
  }

  private static <S, D> PathProperties getDestinationProperties(TypeMapImpl<S, D> typeMap) {
    PathProperties pathProperties = new PathProperties();
    Set<Class<?>> classes = new HashSet<Class<?>>();

    Stack<Property> propertyStack = new Stack<Property>();
    propertyStack.push(new Property("", TypeInfoRegistry.typeInfoFor(typeMap.getDestinationType(), typeMap.configuration)));

    while (!propertyStack.isEmpty()) {
      Property property = propertyStack.pop();
      classes.add(property.typeInfo.getType());
      for (Map.Entry<String, Mutator> entry : property.typeInfo.getMutators().entrySet()) {
        if (entry.getValue() instanceof PropertyInfoImpl.FieldPropertyInfo
            && !typeMap.configuration.isFieldMatchingEnabled()) {
          continue;
        }

        String path = property.prefix + entry.getKey() + ".";
        Mutator mutator = entry.getValue();
        pathProperties.pathProperties.add(new PathProperty(path, mutator));

        if (!classes.contains(mutator.getType())
            && Types.mightContainsProperties(mutator.getType()))
          propertyStack.push(new Property(path, TypeInfoRegistry.typeInfoFor(mutator.getType(), typeMap.configuration)));
      }
    }
    return pathProperties;
  }

  private static final class Property {
    String prefix;
    TypeInfo<?> typeInfo;

    public Property(String prefix, TypeInfo<?> typeInfo) {
      this.prefix = prefix;
      this.typeInfo = typeInfo;
    }
  }

  private static final class PathProperty {
    String path;
    Mutator mutator;

    private PathProperty(String path, Mutator mutator) {
      this.path = path;
      this.mutator = mutator;
    }
  }

  private static final class PathProperties {
    List<PathProperty> pathProperties = new ArrayList<PathProperty>();

    private void removeMatchPath(String path) {
      int startIndex = 0;
      int endIndex;
      while ((endIndex = path.indexOf(".", startIndex)) != -1) {
        String currentPath = path.substring(0, endIndex + 1);

        Iterator<PathProperty> iterator = pathProperties.iterator();
        while (iterator.hasNext())
          if (iterator.next().path.equals(currentPath))
            iterator.remove();

        startIndex = endIndex + 1;
      }

      Iterator<PathProperty> iterator = pathProperties.iterator();
      while (iterator.hasNext())
        if (iterator.next().path.startsWith(path))
          iterator.remove();
    }

    public List<PropertyInfo> toList() {
      List<PropertyInfo> mutators = new ArrayList<PropertyInfo>(pathProperties.size());
      for (PathProperty pathProperty : pathProperties)
        mutators.add(pathProperty.mutator);
      return mutators;
    }
  }
}
