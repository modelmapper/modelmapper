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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.NoOp;

import org.modelmapper.internal.util.Types;

/**
 * Produces proxied instances of mappable types that participate in mapping creation.
 * 
 * @author Jonathan Halterman
 */
class ProxyFactory {
  private static final NamingPolicy NAMING_POLICY = new DefaultNamingPolicy() {
    @Override
    protected String getTag() {
      return "ByModelMapper";
    }
  };

  private static final CallbackFilter METHOD_FILTER = new CallbackFilter() {
    public int accept(Method method) {
      return method.isBridge()
          || (method.getName().equals("finalize") && method.getParameterTypes().length == 0)
          || (method.getReturnType().getName().equals("groovy.lang.MetaClass") && (method.getName()
              .equals("getMetaClass") || method.getName().startsWith("$"))) ? 1 : 0;
    }
  };

  static boolean isProxy(Class<?> type) {
    return Enhancer.isEnhanced(type);
  }

  static Class<?> proxyClassFor(Class<?> type) throws ErrorsException {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(type);
    enhancer.setUseFactory(false);
    enhancer.setUseCache(true);
    enhancer.setNamingPolicy(NAMING_POLICY);
    enhancer.setCallbackFilter(METHOD_FILTER);
    enhancer.setCallbackTypes(new Class[] { MethodInterceptor.class, NoOp.class });

    try {
      return enhancer.createClass();
    } catch (Throwable t) {
      throw new Errors().errorEnhancingClass(type, t).toException();
    }
  }

  /**
   * @throws ErrorsException if the proxy for {@code type} cannot be generated or instantiated
   */
  static <T> T proxyFor(Class<T> type, ExplicitMappingProgress<?> mappingProgress)
      throws ErrorsException {
    if (Modifier.isFinal(type.getModifiers()))
      return null;

    Class<?> enhanced = proxyClassFor(type);

    try {
      Enhancer.registerCallbacks(enhanced, new Callback[] {
          new ExplicitMappingInterceptor(mappingProgress), NoOp.INSTANCE });
      mappingProgress.enterConstructor();
      T result = Types.construct(enhanced, type);
      return result;
    } catch (Throwable t) {
      throw new Errors().errorInstantiatingProxy(type, t).toException();
    } finally {
      mappingProgress.leaveConstructor();
      Enhancer.registerCallbacks(enhanced, null);
    }
  }
}
