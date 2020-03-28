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
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.modelmapper.config.Configuration;
import org.modelmapper.internal.PropertyInfoImpl.FieldPropertyInfo;
import org.modelmapper.internal.PropertyInfoImpl.MethodAccessor;
import org.modelmapper.internal.PropertyInfoImpl.MethodMutator;

/**
 * Statically stores and retrieves MemberInfo by member and configuration. This registry is designed
 * to return a distinct PropertyInfo instance for each initial type, member and configuration object
 * set.
 * 
 * @author Jonathan Halterman
 */
class PropertyInfoRegistry {

  private static final Map<PropertyInfoKey, Mutator> MUTATOR_CACHE = new ConcurrentHashMap<PropertyInfoKey, Mutator>();
  private static final Map<PropertyInfoKey, Accessor> ACCESSOR_CACHE = new ConcurrentHashMap<PropertyInfoKey, Accessor>();
  private static final Map<PropertyInfoKey, FieldPropertyInfo> FIELD_CACHE = new ConcurrentHashMap<PropertyInfoKey, FieldPropertyInfo>();

  private static class PropertyInfoKey {
    private final Class<?> initialType;
    private final String propertyName;
    private final Configuration configuration;

    PropertyInfoKey(Class<?> initialType, String propertyName, Configuration configuration) {
      this.initialType = initialType;
      this.propertyName = propertyName;
      this.configuration = configuration;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (!(o instanceof PropertyInfoKey))
        return false;
      PropertyInfoKey other = (PropertyInfoKey) o;
      return initialType.equals(other.initialType) && propertyName.equals(other.propertyName)
          && configuration.equals(other.configuration);
    }

    @Override
    public int hashCode() {
      int result = 31 + initialType.hashCode();
      result = 31 * result + propertyName.hashCode();
      result = 31 * result + configuration.hashCode();
      return result;
    }
  }

  /**
   * Returns an accessor for the {@code accessorName}, else {@code null} if none exists.
   */
  static Accessor accessorFor(Class<?> type, String accessorName, InheritingConfiguration configuration) {
    PropertyInfoKey key = new PropertyInfoKey(type, accessorName, configuration);
    if (!ACCESSOR_CACHE.containsKey(key) && !FIELD_CACHE.containsKey(key)) {
      @SuppressWarnings("unchecked")
      Class<Object> uncheckedType = (Class<Object>) type;
      for (Entry<String, Accessor> entry : TypeInfoRegistry.typeInfoFor(uncheckedType, configuration).getAccessors().entrySet()) {
        if (entry.getValue().getMember() instanceof Method)
          accessorFor(type, (Method) entry.getValue().getMember(), configuration, entry.getKey());
        else if (entry.getValue().getMember() instanceof Field)
          fieldPropertyFor(type, (Field) entry.getValue().getMember(), configuration, entry.getKey());
      }
    }

    if (ACCESSOR_CACHE.containsKey(key))
      return ACCESSOR_CACHE.get(key);
    return FIELD_CACHE.get(key);
  }

  /**
   * Returns an Accessor for the given accessor method. The method must be externally validated to
   * ensure that it accepts zero arguments and does not return void.class.
   */
  static synchronized Accessor accessorFor(Class<?> type, Method method,
      Configuration configuration, String name) {
    PropertyInfoKey key = new PropertyInfoKey(type, name, configuration);
    Accessor accessor = ACCESSOR_CACHE.get(key);
    if (accessor == null) {
      accessor = new MethodAccessor(type, method, name);
      ACCESSOR_CACHE.put(key, accessor);
    }

    return accessor;
  }

  /**
   * Returns a FieldPropertyInfo instance for the given field.
   */
  static synchronized FieldPropertyInfo fieldPropertyFor(Class<?> type, Field field,
      Configuration configuration, String name) {
    PropertyInfoKey key = new PropertyInfoKey(type, name, configuration);
    FieldPropertyInfo fieldPropertyInfo = FIELD_CACHE.get(key);
    if (fieldPropertyInfo == null) {
      fieldPropertyInfo = new FieldPropertyInfo(type, field, name);
      FIELD_CACHE.put(key, fieldPropertyInfo);
    }

    return fieldPropertyInfo;
  }

  /**
   * Returns an mutator for the {@code accessorName}, else {@code null} if none exists.
   * Returns a Mutator instance for the given mutator method. The method must be externally
   * validated to ensure that it accepts one argument and returns void.class.
   */
  static synchronized Mutator mutatorFor(Class<?> type, String name, InheritingConfiguration configuration) {
    PropertyInfoKey key = new PropertyInfoKey(type, name, configuration);
    if (!MUTATOR_CACHE.containsKey(key) && !FIELD_CACHE.containsKey(key)) {
      @SuppressWarnings("unchecked")
      Class<Object> uncheckedType = (Class<Object>) type;
      for (Entry<String, Mutator> entry : TypeInfoRegistry.typeInfoFor(uncheckedType, configuration).getMutators().entrySet()) {
        if (entry.getValue().getMember() instanceof Method)
          mutatorFor(type, (Method) entry.getValue().getMember(), configuration, entry.getKey());
        else if (entry.getValue().getMember() instanceof Field)
          fieldPropertyFor(type, (Field) entry.getValue().getMember(), configuration, entry.getKey());
      }
    }

    if (MUTATOR_CACHE.containsKey(key))
      return MUTATOR_CACHE.get(key);
    return FIELD_CACHE.get(key);
  }

  /**
   * Returns a Mutator instance for the given mutator method. The method must be externally
   * validated to ensure that it accepts one argument and returns void.class.
   */
  static synchronized Mutator mutatorFor(Class<?> type, Method method, Configuration configuration,
      String name) {
    PropertyInfoKey key = new PropertyInfoKey(type, name, configuration);
    Mutator mutator = MUTATOR_CACHE.get(key);
    if (mutator == null) {
      mutator = new MethodMutator(type, method, name);
      MUTATOR_CACHE.put(key, mutator);
    }

    return mutator;
  }
}
