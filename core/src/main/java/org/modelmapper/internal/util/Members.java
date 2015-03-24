/*
 * Copyright 2014 the original author or authors.
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Utilities for working with members.
 * 
 * @author Jonathan Halterman
 */
public final class Members {
  private Members() {}

  public static Method methodFor(Class<?> type, String methodName, Class<?>... parameterTypes) {
    if (type == null)
      return null;

    for (Method method : type.getDeclaredMethods())
      if (!method.isBridge()
          && !method.isSynthetic()
          && method.getName().equals(methodName)
          && ((parameterTypes == null && method.getParameterTypes().length == 0) || Arrays.equals(
              method.getParameterTypes(), parameterTypes)))
        return method;

    for (Class<?> interfaze : type.getInterfaces()) {
      Method result = methodFor(interfaze, methodName, parameterTypes);
      if (result != null)
        return result;
    }
    return methodFor(type.getSuperclass(), methodName, parameterTypes);
  }

  public static Field fieldFor(Class<?> type, String fieldName) {
    while (type != null) {
      for (Field field : type.getDeclaredFields())
        if (field.getName().equals(fieldName))
          return field;
      type = type.getSuperclass();
    }
    return null;
  }
}
