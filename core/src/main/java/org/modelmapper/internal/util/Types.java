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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

/**
 * Utilities for working with types.
 * 
 * @author Jonathan Halterman
 */
public final class Types {
  private static Class<?> JAVASSIST_PROXY_FACTORY_CLASS;
  private static Method JAVASSIST_IS_PROXY_CLASS_METHOD;

  static {
    try {
      JAVASSIST_PROXY_FACTORY_CLASS = Types.class.getClassLoader().loadClass(
          "javassist.util.proxy.ProxyFactory");
      JAVASSIST_IS_PROXY_CLASS_METHOD = JAVASSIST_PROXY_FACTORY_CLASS.getMethod("isProxyClass",
          new Class<?>[] { Class.class });
    } catch (Exception ignore) {
    }
  }

  private Types() {
  }

  /**
   * Gets the method for the given parameters.
   * 
   * @throws RuntimeException on error
   */
  public static Method methodFor(Class<?> type, String name, Class<?>... parameterTypes) {
    try {
      return type.getDeclaredMethod(name, parameterTypes);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns the proxied type, if any, else returns the given {@code type}.
   */
  @SuppressWarnings("unchecked")
  public static <T> Class<T> deProxy(Class<?> type) {
    // Ignore JDK proxies
    if (type.isInterface())
      return (Class<T>) type;

    // CGLib
    if (type.getName().contains("$$EnhancerByCGLIB$$"))
      return (Class<T>) type.getSuperclass();

    // Javassist
    try {
      if (JAVASSIST_IS_PROXY_CLASS_METHOD != null && JAVASSIST_IS_PROXY_CLASS_METHOD != null
          && (Boolean) JAVASSIST_IS_PROXY_CLASS_METHOD.invoke(null, type))
        return (Class<T>) type.getSuperclass();
    } catch (Exception ignore) {
    }

    return (Class<T>) type;
  }

  /**
   * Returns a simplified String representation of the {@code type}.
   */
  public static String toString(Class<?> type) {
    return type.getName();
  }

  /**
   * Returns a simplified String representation of the {@code member}.
   */
  public static String toString(Member member) {
    if (member instanceof Method) {
      return member.getDeclaringClass().getName() + "." + member.getName() + "()";
    } else if (member instanceof Field) {
      return member.getDeclaringClass().getName() + "." + member.getName();
    } else if (member instanceof Constructor) {
      return member.getDeclaringClass().getName() + ".<init>()";
    }
    return null;
  }
}