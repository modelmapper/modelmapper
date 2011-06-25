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
package org.modelmapper.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
  private static final Map<Integer, PropertyInfo> cache = new HashMap<Integer, PropertyInfo>();

  private static Integer hashCodeFor(Class<?> initialType, Member member,
      Configuration configuration) {
    int result = 31 * +initialType.hashCode();
    result = 31 * result + member.hashCode();
    result = 31 * result + configuration.hashCode();
    return Integer.valueOf(result);
  }

  /**
   * Returns an Accessor for the given accessor method. The method must be externally validated to
   * ensure that it accepts zero arguments and does not return void.class.
   */
  static synchronized Accessor accessorFor(Class<?> initialType, Method method,
      Configuration configuration, String name) {
    Integer hashCode = hashCodeFor(initialType, method, configuration);
    Accessor accessor = (Accessor) cache.get(hashCode);
    if (accessor == null) {
      accessor = new MethodAccessor(initialType, method, name);
      cache.put(hashCode, accessor);
    }

    return accessor;
  }

  /**
   * Returns a FieldPropertyInfo instance for the given field.
   */
  static synchronized FieldPropertyInfo fieldPropertyFor(Class<?> initialType, Field field,
      Configuration configuration, String name) {
    Integer hashCode = hashCodeFor(initialType, field, configuration);
    FieldPropertyInfo fieldPropertyInfo = (FieldPropertyInfo) cache.get(hashCode);
    if (fieldPropertyInfo == null) {
      fieldPropertyInfo = new FieldPropertyInfo(initialType, field, name);
      cache.put(hashCode, fieldPropertyInfo);
    }

    return fieldPropertyInfo;
  }

  /**
   * Returns a Mutator instance for the given mutator method. The method must be externally
   * validated to ensure that it accepts one argument and returns void.class.
   */
  static synchronized Mutator mutatorFor(Class<?> initialType, Method method,
      Configuration configuration, String name) {
    Integer hashCode = hashCodeFor(initialType, method, configuration);
    Mutator mutator = (Mutator) cache.get(hashCode);
    if (mutator == null) {
      mutator = new MethodMutator(initialType, method, name);
      cache.put(hashCode, mutator);
    }

    return mutator;
  }
}
