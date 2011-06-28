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
import java.util.Map;

/**
 * Utility methods for working with primitives.
 * 
 * @author Jonathan Halterman
 */
public final class Primitives {
  private static Map<Class<?>, Class<?>> primitiveToWrapper;
  private static Map<Class<?>, Class<?>> wrapperToPrimitive;
  private static Map<Class<?>, Object> wrapperToDefault;

  private Primitives() {
  }

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

    wrapperToDefault = new HashMap<Class<?>, Object>();
    wrapperToDefault.put(Boolean.class, Boolean.FALSE);
    wrapperToDefault.put(Character.class, Character.valueOf('\u0000'));
    wrapperToDefault.put(Byte.class, Byte.valueOf((byte) 0));
    wrapperToDefault.put(Short.class, Short.valueOf((short) 0));
    wrapperToDefault.put(Integer.class, Integer.valueOf(0));
    wrapperToDefault.put(Long.class, Long.valueOf(0L));
    wrapperToDefault.put(Float.class, Float.valueOf(0.0f));
    wrapperToDefault.put(Double.class, Double.valueOf(0.0d));
  }

  /**
   * Returns the default value for {@code type} if {@code type} is a primitive, else null.
   */
  public static Object defaultValue(Class<?> type) {
    return wrapperToDefault.get(wrapperFor(type));
  }

  /**
   * Returns true if {@code type} is a primitive or a primitive wrapper.
   */
  public static boolean isPrimitive(Class<?> type) {
    return type.isPrimitive() || isPrimitiveWrapper(type);
  }

  /**
   * Returns the primitive wrapper for {@code type}, else returns {@code type} if {@code type} is
   * not a primitive.
   */
  public static Class<?> wrapperFor(Class<?> type) {
    return type.isPrimitive() ? primitiveToWrapper.get(type) : type;
  }

  /**
   * Returns true if {@code type} is a primitive wrapper.
   */
  public static boolean isPrimitiveWrapper(Class<?> type) {
    return wrapperToPrimitive.containsKey(type);
  }
}
