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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.NoOp;

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
  private static final NamingPolicy NAMING_POLICY = new DefaultNamingPolicy() {
    @Override
    protected String getTag() {
      return "ByModelMapper";
    }
  };
  private static final CallbackFilter METHOD_FILTER = new CallbackFilter() {
    public int accept(Method method) {
      return method.isBridge()
          || method.getDeclaringClass().equals(Object.class)
          || (method.getName().equals("equals") && method.getParameterTypes().length == 1
              && method.getParameterTypes()[0].equals(Object.class) && method.getReturnType()
              .equals(Boolean.TYPE))
          || (method.getReturnType().getName().equals("groovy.lang.MetaClass") && (method.getName()
              .equals("getMetaClass") || method.getName().startsWith("$"))) ? 1 : 0;
    }
  };

  static Class<?> proxyClassFor(Class<?> type, Errors errors) throws ErrorsException {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(type);
    enhancer.setUseFactory(true);
    enhancer.setUseCache(true);
    enhancer.setNamingPolicy(NAMING_POLICY);
    enhancer.setCallbackFilter(METHOD_FILTER);
    enhancer.setCallbackTypes(new Class[] { MethodInterceptor.class, NoOp.class });

    try {
      return enhancer.createClass();
    } catch (Throwable t) {
      throw errors.errorEnhancingClass(type, t).toException();
    }
  }

  /**
   * @throws ErrorsException if the proxy for {@code type} cannot be generated or instantiated
   */
  @SuppressWarnings("unchecked")
  static <T> T proxyFor(Class<T> type, MethodInterceptor interceptor, Errors errors)
      throws ErrorsException {
    if (Modifier.isFinal(type.getModifiers()))
      return Primitives.defaultValueForWrapper(type);

    Class<?> enhanced = proxyClassFor(type, errors);

    try {
      T result = (T) OBJENESIS.newInstance(enhanced);
      setCallbacks(result, interceptor);
      return result;
    } catch (Throwable t) {
      throw errors.errorInstantiatingProxy(type, t).toException();
    }
  }

  private static void setCallbacks(Object enhanced, MethodInterceptor interceptor) throws Exception {
    Field callback1 = enhanced.getClass().getDeclaredField("CGLIB$CALLBACK_0");
    callback1.setAccessible(true);
    callback1.set(enhanced, interceptor);
    Field callback2 = enhanced.getClass().getDeclaredField("CGLIB$CALLBACK_1");
    callback2.setAccessible(true);
    callback2.set(enhanced, NoOp.INSTANCE);
  }
}
