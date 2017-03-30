/*
 * Copyright 2017 the original author or authors.
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

import static org.modelmapper.internal.ExplicitMappingBuilder.MappingOptions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.spi.NameableType;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * Produces method interceptors that collect getters/setters invoke of the
 * proxy object, and generate {@link MappingImpl}
 *
 * @author Chun Han Hsiao
 */
class PropertyReferenceCollector {
  private InheritingConfiguration config;
  private MappingOptions options;
  private List<Accessor> accessors;
  private List<Mutator> mutators;
  private Errors errors;

  PropertyReferenceCollector(InheritingConfiguration config, MappingOptions options) {
    this.config = config;
    this.options = options;
    this.accessors = new ArrayList<Accessor>();
    this.mutators = new ArrayList<Mutator>();
    this.errors = new Errors();
  }

  public MethodInterceptor newSourceInterceptor() {
    return new MethodInterceptor() {
      public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        visitSource(o.getClass(), method);

        if (Void.class.isAssignableFrom(method.getReturnType()))
          return null;
        return ProxyFactory.proxyFor(method.getReturnType(), this, errors);
      }
    };
  }

  public MethodInterceptor newDestinationInterceptor() {
    return new MethodInterceptor() {
      public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        visitDestination(o.getClass(), method);

        if (Void.class.isAssignableFrom(method.getReturnType()))
          return null;
        return ProxyFactory.proxyFor(method.getReturnType(), this, new Errors());
      }
    };
  }

  private void visitSource(Class<?> type, Method method) {
    if (PropertyInfoResolver.ACCESSORS.isValid(method)) {
      String propertyName = config.getSourceNameTransformer().transform(method.getName(),
          NameableType.METHOD);
      accessors.add(PropertyInfoRegistry.accessorFor(type, method, config,
          propertyName));
    } else
      errors.addMessage("Illegal SourceGetter method: %s.%s", type.getName(), method.getName());
  }

  private void visitDestination(Class<?> type, Method method) {
    if (PropertyInfoResolver.MUTATORS.isValid(method)) {
      String propertyName = config.getDestinationNameTransformer().transform(method.getName(),
          NameableType.METHOD);
      mutators.add(PropertyInfoRegistry.mutatorFor(type, method, config,
          propertyName));
    } else if (PropertyInfoResolver.ACCESSORS.isValid(method)) {
      Mutator mutator = TypeInfoRegistry.typeInfoFor(type, config).mutatorForAccessorMethod(
          method.getName());
      if (mutator != null)
        mutators.add(mutator);
      else
        errors.addMessage("No setter found: %s.%s", type.getName(), method.getName());
    } else
      errors.addMessage("Illegal DestinationSetter method: %s.%s", type.getName(), method.getName());
  }

  public MappingImpl collect() {
    if ((!isSkip()) && accessors.isEmpty())
      errors.addMessage("Illegal SourceGetter defined");
    if (mutators.isEmpty())
      errors.addMessage("Illegal DestinationSetter defined");

    errors.throwConfigurationExceptionIfErrorsExist();
    return new PropertyMappingImpl(accessors, mutators, options);
  }

  private boolean isSkip() {
    return options.skipType > 0;
  }

  public Errors getErrors() {
    return errors;
  }
}
