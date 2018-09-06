/*
 * Copyright 2018 the original author or authors.
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

import java.lang.reflect.Constructor;

/**
 *  An utility class that provides methods for objects manipulation.
 */
public class Objects {
  private Objects() {
  }

  public static <T> T firstNonNull(T... objects) {
    for (T object : objects)
      if (object != null)
        return object;
    return null;
  }

  public static <T> T firstNonNull(Callable<T>... callables) {
    for (Callable<T> callable : callables) {
      T obj = callable.call();
      if (obj != null)
        return obj;
    }
    return null;
  }

  public static <T> Callable<T> callable(final T obj) {
    return new Callable<T>() {
      @Override
      public T call() {
        return obj;
      }
    };
  }

  public static <T> T instantiate(Class<T> type) {
    try {
      Constructor<T> constructor = type.getDeclaredConstructor();
      if (!constructor.isAccessible())
        constructor.setAccessible(true);
      return constructor.newInstance();
    } catch (Exception e) {
      return null;
    }
  }
}
