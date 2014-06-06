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

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

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

  /**
   * Returns the proxied type, if any, else returns the given {@code type}.
   */
  @SuppressWarnings("unchecked")
  public static <T> Class<T> deProxy(Class<?> type) {
    // Ignore JDK proxies
    if (type.isInterface())
      return (Class<T>) type;

    boolean isProxy = false;

    // CGLib
    if (type.getName().contains("$$EnhancerBy"))
      isProxy = true;

    // Javassist
    try {
      if (JAVASSIST_IS_PROXY_CLASS_METHOD != null && JAVASSIST_IS_PROXY_CLASS_METHOD != null
          && (Boolean) JAVASSIST_IS_PROXY_CLASS_METHOD.invoke(null, type))
        isProxy = true;
    } catch (Exception ignore) {
    }

    if (isProxy) {
      if (!type.getSuperclass().equals(Object.class))
        return (Class<T>) type.getSuperclass();
      else {
        Class<?>[] interfaces = type.getInterfaces();
        if (interfaces.length > 0)
          return (Class<T>) interfaces[0];
      }
    }

    return (Class<T>) type;
  }

  /**
   * Returns whether the {@code type} is a Groovy type.
   */
  public static boolean isGroovyType(Class<?> type) {
    return type.getName().startsWith("org.codehaus.groovy");
  }

  /**
   * Returns true if the {@code type} is instantiable.
   */
  public static boolean isInstantiable(Class<?> type) {
    return !type.isEnum() && !type.isAssignableFrom(String.class)
        && !Primitives.isPrimitiveWrapper(type);
  }

  /**
   * Returns the raw type for the {@code type}. If {@code type} is a TypeVariable or a WildcardType
   * then the first upper bound is returned. is returned.
   * 
   * @throws IllegalArgumentException if {@code type} is not a Class, ParameterizedType,
   *           GenericArrayType, TypeVariable or WildcardType.
   */
  public static Class<?> rawTypeFor(Type type) {
    if (type instanceof Class<?>) {
      return (Class<?>) type;
    } else if (type instanceof ParameterizedType) {
      return (Class<?>) ((ParameterizedType) type).getRawType();
    } else if (type instanceof GenericArrayType) {
      Type componentType = ((GenericArrayType) type).getGenericComponentType();
      return Array.newInstance(rawTypeFor(componentType), 0).getClass();
    } else if (type instanceof TypeVariable) {
      return rawTypeFor(((TypeVariable<?>) type).getBounds()[0]);
    } else if (type instanceof WildcardType) {
      return rawTypeFor(((WildcardType) type).getUpperBounds()[0]);
    } else {
      String className = type == null ? "null" : type.getClass().getName();
      throw new IllegalArgumentException("Could not determine raw type for " + className);
    }
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

  /**
   * Returns a simplified String representation of the {@code type}.
   */
  public static String toString(Type type) {
    return type instanceof Class ? ((Class<?>) type).getName() : type.toString();
  }
}