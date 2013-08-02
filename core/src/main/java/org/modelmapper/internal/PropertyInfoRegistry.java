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
package org.modelmapper.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.modelmapper.config.Configuration;
import org.modelmapper.internal.PropertyInfoImpl.FieldPropertyInfo;
import org.modelmapper.internal.PropertyInfoImpl.MethodAccessor;
import org.modelmapper.internal.PropertyInfoImpl.MethodMutator;
import org.modelmapper.spi.PropertyInfo;

/**
 * Statically stores and retrieves MemberInfo by member and configuration. This registry is designed
 * to return a distinct PropertyInfo instance for each initial type, member and configuration object
 * set.
 * 
 * @author Jonathan Halterman
 */
class PropertyInfoRegistry {
  private static final Map<Integer, PropertyInfo> cache = new ConcurrentHashMap<Integer, PropertyInfo>();

  private static Integer hashCodeFor(Class<?> initialType, String propertyName,
      Configuration configuration) {
    int result = 31 + initialType.hashCode();
    result = 31 * result + propertyName.hashCode();
    result = 31 * result + configuration.hashCode();
    return Integer.valueOf(result);
  }

  /**
   * Returns an accessor for the {@code accessorName}, else {@code null} if none exists.
   */
  static Accessor accessorFor(Class<?> type, String accessorName, Configuration configuration) {
    return (Accessor) cache.get(hashCodeFor(type, accessorName, configuration));
  }

  /**
   * Returns an Accessor for the given accessor method. The method must be externally validated to
   * ensure that it accepts zero arguments and does not return void.class.
   */
  static synchronized Accessor accessorFor(Class<?> type, Method method,
      Configuration configuration, String name) {
    Integer hashCode = hashCodeFor(type, method.getName(), configuration);
    Accessor accessor = (Accessor) cache.get(hashCode);
    if (accessor == null) {
      accessor = new MethodAccessor(type, method, name);
      cache.put(hashCode, accessor);
    }

    return accessor;
  }

  /**
   * Returns a FieldPropertyInfo instance for the given field.
   */
  static synchronized FieldPropertyInfo fieldPropertyFor(Class<?> type, Field field,
      Configuration configuration, String name) {
    Integer hashCode = hashCodeFor(type, field.getName(), configuration);
    FieldPropertyInfo fieldPropertyInfo = (FieldPropertyInfo) cache.get(hashCode);
    if (fieldPropertyInfo == null) {
      fieldPropertyInfo = new FieldPropertyInfo(type, field, name);
      cache.put(hashCode, fieldPropertyInfo);
    }

    return fieldPropertyInfo;
  }

  /**
   * Returns a Mutator instance for the given mutator method. The method must be externally
   * validated to ensure that it accepts one argument and returns void.class.
   */
  static synchronized Mutator mutatorFor(Class<?> type, Method method, Configuration configuration,
      String name) {
    Integer hashCode = hashCodeFor(type, method.getName(), configuration);
    Mutator mutator = (Mutator) cache.get(hashCode);
    if (mutator == null) {
      mutator = new MethodMutator(type, method, name);
      cache.put(hashCode, mutator);
    }

    return mutator;
  }
}
