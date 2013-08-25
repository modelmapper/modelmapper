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

import org.modelmapper.internal.util.Types;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * Intercepts invocations against mappable types that occur during explicit mapping creation.
 * 
 * @author Jonathan Halterman
 */
final class ExplicitMappingInterceptor implements MethodInterceptor {
  private final ExplicitMappingProgress<?> mappingProgress;

  ExplicitMappingInterceptor(ExplicitMappingProgress<?> mappingProgress) {
    this.mappingProgress = mappingProgress;
  }

  public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
      throws Throwable {
    mappingProgress.encountered(Types.deProxy(obj.getClass()), method, args);
    
    return method.getReturnType() == void.class ? null : ProxyFactory.proxyFor(
        method.getReturnType(), mappingProgress);
  }
}