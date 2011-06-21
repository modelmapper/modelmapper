/**
 * Copyright 2002-2011 the original author or authors.
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

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Enhanced type resolution utilities. Based on org.springframework.core.GenericTypeResolver.
 * 
 * @author Jonathan Halterman
 */
public final class TypeResolver {
  private TypeResolver() {
  }

  /** Cache of type variable/argument pairs */
  private static final Map<Class<?>, Reference<Map<TypeVariable<?>, Type>>> typeVariableCache = Collections
      .synchronizedMap(new WeakHashMap<Class<?>, Reference<Map<TypeVariable<?>, Type>>>());

  /**
   * Returns the raw class representing the type argument for the {@code targetType} resolved
   * upwards from the {@code initialType}. If no arguments can be resolved then {@code Object.class}
   * is returned.
   * 
   * @param initialType to resolve upwards from
   * @param targetType to resolve arguments for
   * @return type argument for {@code initialType} else {@code null} if no type arguments are
   *         declared
   * @throws IllegalArgumentException if more or less than one type argument is resolved for the
   *           give types
   */
  public static <T, I extends T> Class<?> resolveArgument(Class<I> initialType, Class<T> targetType) {
    return resolveArgument(resolveGenericType(initialType, targetType), initialType);
  }

  /**
   * Resolves the type argument for the {@code genericType} using type variable information from the
   * {@code sourceType}. If no arguments can be resolved then {@code Object.class} is returned.
   * 
   * @param genericType to resolve upwards from
   * @param sourceType to resolve arguments for
   * @return type argument for {@code initialType} else {@code null} if no type arguments are
   *         declared
   * @throws IllegalArgumentException if more or less than one type argument is resolved for the
   *           give types
   */
  public static Class<?> resolveArgument(Type genericType, Class<?> sourceType) {
    Class<?>[] arguments = resolveArguments(genericType, sourceType);
    if (arguments == null)
      return Object.class;

    if (arguments.length != 1)
      throw new IllegalArgumentException("Expected 1 type argument on generic type "
          + sourceType.getName() + " but found " + arguments.length);

    return arguments[0];
  }

  /**
   * Returns an array of raw classes representing type arguments for the {@code targetType} resolved
   * upwards from the {@code initialType}. Arguments for {@code targetType} that cannot be resolved
   * to a Class are returned as {@code Object.class}. If no arguments can be resolved then
   * {@code null} is returned.
   * 
   * @param initialType to resolve upwards from
   * @param targetType to resolve arguments for
   * @return array of raw classes representing type arguments for {@code initialType} else
   *         {@code null} if no type arguments are declared
   */
  public static <T, I extends T> Class<?>[] resolveArguments(Class<I> initialType,
      Class<T> targetType) {
    return resolveArguments(resolveGenericType(initialType, targetType), initialType);
  }

  /**
   * Resolves the arguments for the {@code genericType} using the type variable information for the
   * {@code sourceType}. Returns {@code null} if {@code genericType} is not parameterized or if
   * arguments cannot be resolved.
   */
  public static Class<?>[] resolveArguments(Type genericType, Class<?> sourceType) {
    if (!(genericType instanceof ParameterizedType))
      return null;

    ParameterizedType paramType = (ParameterizedType) genericType;
    Type[] arguments = paramType.getActualTypeArguments();
    Class<?>[] result = new Class[arguments.length];
    for (int i = 0; i < arguments.length; i++)
      result[i] = resolveClass(arguments[i], sourceType);

    return result;
  }

  /**
   * Resolves the generic Type for the {@code targetType} by walking the type hierarchy upwards from
   * the {@code initialType}.
   */
  public static Type resolveGenericType(Type initialType, Class<?> targetType) {
    Class<?> rawType;
    if (initialType instanceof ParameterizedType)
      rawType = (Class<?>) ((ParameterizedType) initialType).getRawType();
    else
      rawType = (Class<?>) initialType;

    if (targetType.equals(rawType))
      return initialType;

    Type result;
    if (targetType.isInterface()) {
      for (Type superInterface : rawType.getGenericInterfaces())
        if (superInterface != null && !superInterface.equals(Object.class))
          if ((result = resolveGenericType(superInterface, targetType)) != null)
            return result;
    }

    Type superType = rawType.getGenericSuperclass();
    if (superType != null && !superType.equals(Object.class))
      if ((result = resolveGenericType(superType, targetType)) != null)
        return result;

    return null;
  }

  /**
   * Resolves the raw class for the given {@code genericType}, using the type variable information
   * from the {@code sourceType}.
   */
  public static Class<?> resolveClass(Type genericType, Class<?> sourceType) {
    if (genericType instanceof ParameterizedType) {
      return resolveClass(((ParameterizedType) genericType).getRawType(), sourceType);
    } else if (genericType instanceof GenericArrayType) {
      GenericArrayType arrayType = (GenericArrayType) genericType;
      Class<?> compoment = resolveClass(arrayType.getGenericComponentType(), sourceType);
      return Array.newInstance(compoment, 0).getClass();
    } else if (genericType instanceof TypeVariable) {
      TypeVariable<?> variable = (TypeVariable<?>) genericType;
      genericType = getTypeVariableMap(sourceType).get(variable);
      genericType = genericType == null ? resolveBound(variable) : resolveClass(genericType,
          sourceType);
    }

    return genericType instanceof Class ? (Class<?>) genericType : Object.class;
  }

  private static Map<TypeVariable<?>, Type> getTypeVariableMap(final Class<?> sourceType) {
    Reference<Map<TypeVariable<?>, Type>> ref = typeVariableCache.get(sourceType);
    Map<TypeVariable<?>, Type> map = ref != null ? ref.get() : null;

    if (map == null) {
      map = new HashMap<TypeVariable<?>, Type>();

      // Populate interfaces
      buildTypeVariableMap(sourceType.getGenericInterfaces(), map);

      // Populate super classes and interfaces
      Type genericType = sourceType.getGenericSuperclass();
      Class<?> type = sourceType.getSuperclass();
      while (type != null && !Object.class.equals(type)) {
        if (genericType instanceof ParameterizedType)
          buildTypeVariableMap((ParameterizedType) genericType, map);
        buildTypeVariableMap(type.getGenericInterfaces(), map);

        genericType = type.getGenericSuperclass();
        type = type.getSuperclass();
      }

      // Populate enclosing classes
      type = sourceType;
      while (type.isMemberClass()) {
        genericType = type.getGenericSuperclass();
        if (genericType instanceof ParameterizedType)
          buildTypeVariableMap((ParameterizedType) genericType, map);

        type = type.getEnclosingClass();
      }

      typeVariableCache.put(sourceType, new WeakReference<Map<TypeVariable<?>, Type>>(map));
    }

    return map;
  }

  /**
   * Populates the {@code map} with with variable/argument pairs for the given {@code types}.
   */
  static void buildTypeVariableMap(final Type[] types, final Map<TypeVariable<?>, Type> map) {
    for (Type type : types) {
      if (type instanceof ParameterizedType) {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        buildTypeVariableMap(parameterizedType, map);
        Type rawType = parameterizedType.getRawType();
        if (rawType instanceof Class)
          buildTypeVariableMap(((Class<?>) rawType).getGenericInterfaces(), map);
      } else if (type instanceof Class) {
        buildTypeVariableMap(((Class<?>) type).getGenericInterfaces(), map);
      }
    }
  }

  /**
   * Populates the {@code typeVariableMap} with type arguments and parameters for the given
   * {@code type}.
   */
  private static void buildTypeVariableMap(ParameterizedType type,
      Map<TypeVariable<?>, Type> typeVariableMap) {
    if (type.getRawType() instanceof Class) {
      TypeVariable<?>[] typeVariables = ((Class<?>) type.getRawType()).getTypeParameters();
      Type[] typeArguments = type.getActualTypeArguments();

      for (int i = 0; i < typeArguments.length; i++) {
        TypeVariable<?> variable = typeVariables[i];
        Type typeArgument = typeArguments[i];

        if (typeArgument instanceof Class) {
          typeVariableMap.put(variable, typeArgument);
        } else if (typeArgument instanceof GenericArrayType) {
          typeVariableMap.put(variable, typeArgument);
        } else if (typeArgument instanceof ParameterizedType) {
          typeVariableMap.put(variable, typeArgument);
        } else if (typeArgument instanceof TypeVariable) {
          TypeVariable<?> typeVariableArgument = (TypeVariable<?>) typeArgument;
          Type resolvedType = typeVariableMap.get(typeVariableArgument);
          if (resolvedType == null)
            resolvedType = resolveBound(typeVariableArgument);
          typeVariableMap.put(variable, resolvedType);
        }
      }
    }
  }

  /**
   * Resolves the first bound for the {@code typeVariable}, returning {@code Object.class} if none
   * can be resolved.
   */
  public static Type resolveBound(TypeVariable<?> typeVariable) {
    Type[] bounds = typeVariable.getBounds();
    if (bounds.length == 0)
      return Object.class;

    Type bound = bounds[0];
    if (bound instanceof TypeVariable)
      bound = resolveBound((TypeVariable<?>) bound);

    return bound;
  }
}
