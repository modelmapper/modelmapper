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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author Jörn Horstmann
 */
public final class Optionals {

  private static Class<?> OPTIONAL_CLASS;
  private static Method FROM_NULLABLE_METHOD;

  static {
    try {
      Class<?> optionalClass = Class.forName("com.google.common.base.Optional");
      OPTIONAL_CLASS = optionalClass;
      FROM_NULLABLE_METHOD = optionalClass.getDeclaredMethod("fromNullable", Object.class);
    } catch (ClassNotFoundException ex) {
      // Google guava not on classpath
    } catch (NoSuchMethodException ex) {
      throw new IllegalStateException(ex);
    } catch (SecurityException ex) {
      throw new IllegalStateException(ex);
    }
  }

  private Optionals() {
  }

  public static boolean isOptional(Class<?> clazz) {
    return OPTIONAL_CLASS != null && OPTIONAL_CLASS.isAssignableFrom(clazz);
  }

  public static Object fromNullable(Object obj) {
    if (FROM_NULLABLE_METHOD == null) {
      throw new IllegalStateException("Method Optional.fromNullable is not available");
    }
    try {
      return FROM_NULLABLE_METHOD.invoke(null, obj);
    } catch (IllegalAccessException ex) {
      throw new IllegalStateException(ex);
    } catch (InvocationTargetException ex) {
      throw new IllegalStateException(ex);
    }
  }

}
