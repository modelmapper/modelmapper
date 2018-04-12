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
package org.modelmapper.internal.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Utility methods for working with primitives.
 * 
 * @author Jonathan Halterman
 */
public final class Primitives {
  private static Map<Class<?>, Class<?>> primitiveToWrapper;
  private static Map<Class<?>, Class<?>> wrapperToPrimitive;
  private static Map<Class<?>, Object> defaultValue;
  private static Set<String> primitiveWrapperInternalNames;

  static {
    primitiveToWrapper = new HashMap<Class<?>, Class<?>>();
    primitiveToWrapper.put(Boolean.TYPE, Boolean.class);
    primitiveToWrapper.put(Character.TYPE, Character.class);
    primitiveToWrapper.put(Byte.TYPE, Byte.class);
    primitiveToWrapper.put(Short.TYPE, Short.class);
    primitiveToWrapper.put(Integer.TYPE, Integer.class);
    primitiveToWrapper.put(Long.TYPE, Long.class);
    primitiveToWrapper.put(Float.TYPE, Float.class);
    primitiveToWrapper.put(Double.TYPE, Double.class);

    wrapperToPrimitive = new HashMap<Class<?>, Class<?>>();
    wrapperToPrimitive.put(Boolean.class, Boolean.TYPE);
    wrapperToPrimitive.put(Character.class, Character.TYPE);
    wrapperToPrimitive.put(Byte.class, Byte.TYPE);
    wrapperToPrimitive.put(Short.class, Short.TYPE);
    wrapperToPrimitive.put(Integer.class, Integer.TYPE);
    wrapperToPrimitive.put(Long.class, Long.TYPE);
    wrapperToPrimitive.put(Float.class, Float.TYPE);
    wrapperToPrimitive.put(Double.class, Double.TYPE);

    defaultValue = new HashMap<Class<?>, Object>();
    defaultValue.put(Boolean.TYPE, Boolean.FALSE);
    defaultValue.put(Character.TYPE, Character.valueOf('\u0000'));
    defaultValue.put(Byte.TYPE, Byte.valueOf((byte) 0));
    defaultValue.put(Short.TYPE, Short.valueOf((short) 0));
    defaultValue.put(Integer.TYPE, Integer.valueOf(0));
    defaultValue.put(Long.TYPE, Long.valueOf(0L));
    defaultValue.put(Float.TYPE, Float.valueOf(0.0f));
    defaultValue.put(Double.TYPE, Double.valueOf(0.0d));

    primitiveWrapperInternalNames = new HashSet<String>();
    for (Class<?> wrapper : wrapperToPrimitive.keySet())
      primitiveWrapperInternalNames.add(wrapper.getName().replace('.', '/'));
  }

  private Primitives() {
  }

  /**
   * Returns the boxed default value for {@code type} if {@code type} is a primitive, else null.
   */
  @SuppressWarnings("unchecked")
  public static <T> T defaultValue(Class<?> type) {
    return type.isPrimitive() ? (T) defaultValue.get(type) : null;
  }

  /**
   * Returns the boxed default value for {@code type} if {@code type} is a primitive wrapper.
   */
  @SuppressWarnings("unchecked")
  public static <T> T defaultValueForWrapper(Class<?> type) {
    Class<?> primiviteType = isPrimitiveWrapper(type) ? primitiveFor(type) : type;
    return primiviteType == null ? null : (T) defaultValue.get(primiviteType);
  }

  /**
   * Returns true if {@code type} is a primitive or a primitive wrapper.
   */
  public static boolean isPrimitive(Class<?> type) {
    return type.isPrimitive() || isPrimitiveWrapper(type);
  }

  /**
   * Returns true if {@code type} is a primitive wrapper.
   */
  public static boolean isPrimitiveWrapper(Class<?> type) {
    return wrapperToPrimitive.containsKey(type);
  }

  /**
   * Returns whether the {@code name} is an internal class name of a primitive wrapper.
   */
  public static boolean isPrimitiveWrapperInternalName(String name) {
    return primitiveWrapperInternalNames.contains(name);
  }

  /**
   * Returns the primitive type for the {@code wrapper}, else returns {@code null} if
   * {@code wrapper} is not a primitive wrapper.
   */
  public static Class<?> primitiveFor(Class<?> wrapper) {
    return wrapperToPrimitive.get(wrapper);
  }

  /**
   * Returns the primitive wrapper for {@code type}, else returns {@code type} if {@code type} is
   * not a primitive.
   */
  public static Class<?> wrapperFor(Class<?> type) {
    return type.isPrimitive() ? primitiveToWrapper.get(type) : type;
  }
}
