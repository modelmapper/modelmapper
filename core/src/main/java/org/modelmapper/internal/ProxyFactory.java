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

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.not;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;

import org.modelmapper.internal.util.Primitives;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * Produces proxied instances of mappable types that participate in mapping creation.
 *
 * @author Jonathan Halterman
 */
class ProxyFactory {
  private static final Objenesis OBJENESIS = new ObjenesisStd();
  private static final ElementMatcher<? super MethodDescription> METHOD_FILTER = not(
      named("toString").or(named("hashCode").or(named("equals"))));

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
      return OBJENESIS.newInstance(new ByteBuddy()
          .subclass(type)
          .method(METHOD_FILTER)
          .intercept(InvocationHandlerAdapter.of(interceptor))
          .make()
          .load(useOSGiClassLoaderBridging ? BridgeClassLoaderFactory.getClassLoader(type) : type.getClassLoader())
          .getLoaded());
    } catch (Throwable t) {
      throw errors.errorInstantiatingProxy(type, t).toException();
    }
  }
}