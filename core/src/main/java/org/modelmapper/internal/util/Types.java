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

import org.modelmapper.internal.util.plugins.ByteBuddyProxyDiscoveryPlugin;
import org.modelmapper.internal.util.plugins.EnhancerProxyDiscoveryPlugin;
import org.modelmapper.internal.util.plugins.HibernateProxyDiscoveryPlugin;
import org.modelmapper.internal.util.plugins.JavaAssistProxyDiscoveryPlugin;
import org.modelmapper.internal.util.plugins.JavaProxyDiscoveryPlugin;
import org.modelmapper.internal.util.plugins.MockitoProxyDiscoveryPlugin;
import org.modelmapper.internal.util.plugins.PermazenProxyDiscoveryPlugin;
import org.modelmapper.internal.util.plugins.ProxyDiscoveryPlugin;
import org.modelmapper.internal.util.plugins.SpringCglibProxyDiscoveryPlugin;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Utilities for working with types.
 *
 * @author Jonathan Halterman
 */
public final class Types {
  private static final List<ProxyDiscoveryPlugin> proxyDiscoveryPlugins = new CopyOnWriteArrayList<>();

  static {
    final List<ProxyDiscoveryPlugin> defaultPlugins = new ArrayList<>();
    defaultPlugins.add(new ByteBuddyProxyDiscoveryPlugin());
    defaultPlugins.add(new EnhancerProxyDiscoveryPlugin());
    defaultPlugins.add(new HibernateProxyDiscoveryPlugin());
    defaultPlugins.add(new MockitoProxyDiscoveryPlugin());
    defaultPlugins.add(new PermazenProxyDiscoveryPlugin());
    defaultPlugins.add(new SpringCglibProxyDiscoveryPlugin());
    defaultPlugins.add(new JavaProxyDiscoveryPlugin());
    defaultPlugins.add(new JavaAssistProxyDiscoveryPlugin());
    proxyDiscoveryPlugins.addAll(defaultPlugins);
  }

  /**
   * Adds a new plugin to support proxy discovery for unknown types.
   *
   * @param plugin The nonnull plugin
   */
  public static void addProxyDiscoveryPlugin(ProxyDiscoveryPlugin plugin) {
    if (plugin == null) {
      throw new IllegalArgumentException("Argument 'plugin' must not be null");
    }
    proxyDiscoveryPlugins.add(plugin);
  }

  @SuppressWarnings("unchecked")
  public static <T> Class<T> deProxiedClass(Object object) {
    if (object == null) {
      return null;
    }
    Object target = object;
    outer:
    while (true) {
      for (ProxyDiscoveryPlugin plugin : proxyDiscoveryPlugins) {
        Object result = plugin.getProxyTarget(target);
        if (result != null && result != target) {
          target = result;
          continue outer;
        }
      }
      break;
    }
    if (target instanceof Class) {
      return (Class<T>) target;
    } else {
      return (Class<T>) target.getClass();
    }
  }

  public static boolean isProxied(Object object) {
    if (object == null) {
      return false;
    }
    return deProxiedClass(object) != object.getClass();
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
   * Returns a origin class for the ASM {@code type}, else {@code null}.
   */
  public static Class<?> classFor(org.objectweb.asm.Type type, ClassLoader classLoader) throws ClassNotFoundException {
    switch (type.getSort()) {
      case org.objectweb.asm.Type.BOOLEAN:
        return Boolean.TYPE;
      case org.objectweb.asm.Type.CHAR:
        return Character.TYPE;
      case org.objectweb.asm.Type.BYTE:
        return Byte.TYPE;
      case org.objectweb.asm.Type.SHORT:
        return Short.TYPE;
      case org.objectweb.asm.Type.INT:
        return Integer.TYPE;
      case org.objectweb.asm.Type.LONG:
        return Long.TYPE;
      case org.objectweb.asm.Type.FLOAT:
        return Float.TYPE;
      case org.objectweb.asm.Type.DOUBLE:
        return Double.TYPE;
      case org.objectweb.asm.Type.ARRAY:
        return Array.newInstance(classFor(type.getElementType(), classLoader), new int[type.getDimensions()])
            .getClass();
      case org.objectweb.asm.Type.OBJECT:
      default:
        return Class.forName(type.getClassName(), true, classLoader);
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
   * Returns whether the type might contains properties or not.
   */
  public static boolean mightContainsProperties(Class<?> type) {
    return type != Object.class
        && type != String.class
        && type != Date.class
        && type != Calendar.class
        && !Primitives.isPrimitive(type)
        && !Iterables.isIterable(type)
        && !Types.isGroovyType(type);
  }

  public static boolean isInternalType(Class<?> type) {
    String packageName = type.getPackage().getName();
    return packageName.startsWith("java.");
  }
}