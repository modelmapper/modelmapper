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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.spi.NameableType;

/**
 * Produces method interceptors that collect getters/setters invoke of the
 * proxy object, and generate {@link MappingImpl}
 *
 * @author Chun Han Hsiao
 */
class PropertyReferenceCollector {
  private InheritingConfiguration config;
  private ExplicitMappingBuilder.MappingOptions options;
  private List<Accessor> accessors;
  private List<Mutator> mutators;
  private Errors errors;
  private Errors proxyErrors;

  PropertyReferenceCollector(InheritingConfiguration config, ExplicitMappingBuilder.MappingOptions options) {
    this.config = config;
    this.options = options;
    this.accessors = new ArrayList<Accessor>();
    this.mutators = new ArrayList<Mutator>();
    this.errors = new Errors();
    this.proxyErrors = new Errors();
  }

  public final class SourceInterceptor implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
      visitSource(proxy.getClass(), method);
      if (Void.class.isAssignableFrom(method.getReturnType()))
        return null;
      try {
        return ProxyFactory.proxyFor(method.getReturnType(), this, proxyErrors);
      } catch (ErrorsException e) {
        return null;
      }
    }
  }

  public final class DestinationInterceptor implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
      visitDestination(proxy.getClass(), method);
      if (Void.class.isAssignableFrom(method.getReturnType()))
        return null;
      try {
        return ProxyFactory.proxyFor(method.getReturnType(), this, proxyErrors);
      } catch (ErrorsException e) {
        return null;
      }
    }
  }

  public SourceInterceptor newSourceInterceptor() {
    return new SourceInterceptor();
  }

  public DestinationInterceptor newDestinationInterceptor() {
    return new DestinationInterceptor();
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

  public Errors getProxyErrors() {
    return proxyErrors;
  }
}
