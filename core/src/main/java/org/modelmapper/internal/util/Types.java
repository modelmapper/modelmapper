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
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilities for working with types.
 * 
 * @author Jonathan Halterman
 */
public final class Types {
  private static Class<?> JAVASSIST_PROXY_FACTORY_CLASS;
  private static Method JAVASSIST_IS_PROXY_CLASS_METHOD;
  private static Map<Class<?>, Object[]> defaultConstructionArgs;
  private static Map<Class<?>, Class<?>[]> defaultConstructionParamTypes;

  static {
    try {
      JAVASSIST_PROXY_FACTORY_CLASS = Types.class.getClassLoader().loadClass(
          "javassist.util.proxy.ProxyFactory");
      JAVASSIST_IS_PROXY_CLASS_METHOD = JAVASSIST_PROXY_FACTORY_CLASS.getMethod("isProxyClass",
          new Class<?>[] { Class.class });
    } catch (Exception ignore) {
    }

    defaultConstructionArgs = new HashMap<Class<?>, Object[]>();
    defaultConstructionArgs.put(BigInteger.class, new Object[] { "0" });
    defaultConstructionParamTypes = new HashMap<Class<?>, Class<?>[]>();
    defaultConstructionParamTypes.put(BigDecimal.class, new Class<?>[] { Integer.TYPE });
  }

  /**
   * Constructs the {@code type} via a non-private default constructor, a pre-defined constructor,
   * or the constructor with the least non-primitive parameter types.
   * 
   * @param type to construct
   * @param lookupType used to lookup pre-defined constructor parameter types and arguments for
   *          types that are difficult to construct generically
   */
  @SuppressWarnings("unchecked")
  public static <T> T construct(Class<?> type, Class<?> lookupType) throws Exception {
    Constructor<?>[] constructors = type.getDeclaredConstructors();

    for (Constructor<?> constructor : constructors)
      if (!Modifier.isPrivate(constructor.getModifiers())
          && constructor.getParameterTypes().length == 0)
        return (T) constructor.newInstance();

    Class<?>[] paramTypes = defaultConstructionParamTypes.get(lookupType);
    Object[] args = defaultConstructionArgs.get(lookupType);
    if (paramTypes != null || args != null) {
      if (paramTypes == null)
        paramTypes = typesFor(args);
      else if (args == null)
        args = defaultArgumentsFor(paramTypes);

      Constructor<?> constructor = type.getDeclaredConstructor(paramTypes);
      if (constructor != null)
        return (T) constructor.newInstance(args);
    }

    Constructor<?> constructor = bestConstructorOf(constructors);
    return (T) constructor.newInstance(defaultArgumentsFor(constructor.getParameterTypes()));
  }

  /**
   * Returns default arguments for the {@code types}.
   */
  public static Object[] defaultArgumentsFor(Class<?>[] types) {
    Object[] args = new Object[types.length];
    for (int i = 0; i < types.length; i++) {
      Class<?> type = types[i];
      args[i] = Primitives.defaultValue(type);
      if (args[i] == null)
        if (type.isArray())
          args[i] = Array.newInstance(type.getComponentType(), 0);
        else if (Collection.class.isAssignableFrom(type))
          args[i] = Collections.emptyList();
        else if (Map.class.isAssignableFrom(type))
          args[i] = Collections.emptyMap();
    }
    return args;
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
    if (type.getName().contains("$$EnhancerBy"))
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

  /**
   * Returns the types for the {@code objects}.
   */
  public static Class<?>[] typesFor(Object[] objects) {
    Class<?>[] types = new Class<?>[objects.length];
    for (int i = 0; i < objects.length; i++)
      types[i] = objects[i].getClass();
    return types;
  }

  /** Returns the constructor with the least non-primitive parameter types. */
  static Constructor<?> bestConstructorOf(Constructor<?>[] constructors) {
    int minNonPrimitives = -1;
    Constructor<?> bestConstructor = null;

    for (Constructor<?> constructor : constructors) {
      int nonPrimitiveCount = 0;
      for (Class<?> paramType : constructor.getParameterTypes())
        if (!paramType.isPrimitive())
          nonPrimitiveCount++;
      if (minNonPrimitives == -1 || nonPrimitiveCount < minNonPrimitives) {
        minNonPrimitives = nonPrimitiveCount;
        bestConstructor = constructor;
      }
    }

    return bestConstructor;
  }
}