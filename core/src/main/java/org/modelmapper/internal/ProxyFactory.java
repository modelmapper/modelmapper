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
package org.modelmapper.internal;

import static net.bytebuddy.NamingStrategy.SuffixingRandom.NO_PREFIX;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.not;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatcher;
import org.modelmapper.internal.util.Primitives;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

/**
 * Produces proxied instances of mappable types that participate in mapping creation.
 *
 * @author Jonathan Halterman
 */
class ProxyFactory {
  private static final Objenesis OBJENESIS = new ObjenesisStd();
  private static final ElementMatcher<? super MethodDescription> METHOD_FILTER = not(
      named("hashCode").or(named("equals")));
  private static final Method PRIVATE_LOOKUP_IN;
  private static final Object LOOKUP;

  static {
    Method privateLookupIn;
    Object lookup;
    try {
      Class<?> methodHandles = Class.forName("java.lang.invoke.MethodHandles");
      lookup = methodHandles.getMethod("lookup").invoke(null);
      privateLookupIn = methodHandles.getMethod(
          "privateLookupIn",
          Class.class,
          Class.forName("java.lang.invoke.MethodHandles$Lookup")
      );
    } catch (Exception e) {
      privateLookupIn = null;
      lookup = null;
    }
    PRIVATE_LOOKUP_IN = privateLookupIn;
    LOOKUP = lookup;
  }

  /**
   * @throws ErrorsException if the proxy for {@code type} cannot be generated or instantiated
   */
  static <T> T proxyFor(Class<T> type, InvocationHandler interceptor, Errors errors)
      throws ErrorsException {
    return proxyFor(type, interceptor, errors, Boolean.FALSE);
  }

  /**
   * @throws ErrorsException if the proxy for {@code type} cannot be generated or instantiated
   */
  @SuppressWarnings("unchecked")
  static <T> T proxyFor(Class<T> type, InvocationHandler interceptor, Errors errors, boolean useOSGiClassLoaderBridging)
      throws ErrorsException {
    if (Primitives.isPrimitive(type))
      return Primitives.defaultValueForWrapper(type);
    if (type.equals(String.class))
      return null;
    if (Modifier.isFinal(type.getModifiers()))
      throw errors.invocationAgainstFinalClass(type).toException();

    try {
      final DynamicType.Unloaded<T> unloaded = new ByteBuddy()
          .with(new NamingStrategy.SuffixingRandom("ByteBuddy", NO_PREFIX))
          .subclass(type)
          .method(METHOD_FILTER)
          .intercept(InvocationHandlerAdapter.of(interceptor))
          .make();
      final ClassLoadingStrategy<ClassLoader> classLoadingStrategy = chooseClassLoadingStrategy(type);
      if (classLoadingStrategy != null) {
        return OBJENESIS.newInstance(unloaded
            .load(useOSGiClassLoaderBridging ? BridgeClassLoaderFactory.getClassLoader(type) : type.getClassLoader(), classLoadingStrategy)
            .getLoaded());
      } else {
        return OBJENESIS.newInstance(unloaded
            .load(useOSGiClassLoaderBridging ? BridgeClassLoaderFactory.getClassLoader(type) : type.getClassLoader())
            .getLoaded());
      }
    } catch (Throwable t) {
      throw errors.errorInstantiatingProxy(type, t).toException();
    }
  }

  private static <T> ClassLoadingStrategy<ClassLoader> chooseClassLoadingStrategy(Class<T> type) {
    try {
      final ClassLoadingStrategy<ClassLoader> strategy;
      if (ClassInjector.UsingLookup.isAvailable() && PRIVATE_LOOKUP_IN != null && LOOKUP != null) {
        Object privateLookup = PRIVATE_LOOKUP_IN.invoke(null, type, LOOKUP);
        strategy = ClassLoadingStrategy.UsingLookup.of(privateLookup);
      } else if (ClassInjector.UsingReflection.isAvailable()) {
        strategy = ClassLoadingStrategy.Default.INJECTION;
      } else {
        throw new IllegalStateException("No code generation strategy available");
      }
      return strategy;
    } catch (InvocationTargetException e) {
      throw new IllegalStateException("Failed to invoke 'privateLookupIn' method from java.lang.invoke.MethodHandles$Lookup.", e);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("Failed to invoke 'privateLookupIn' method from java.lang.invoke.MethodHandles$Lookup.", e);
    }
  }
}
