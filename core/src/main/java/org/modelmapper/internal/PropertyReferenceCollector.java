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
import org.modelmapper.config.Configuration;
import org.modelmapper.internal.PropertyInfoImpl.MethodAccessor;
import org.modelmapper.spi.NameableType;
import org.modelmapper.spi.SourceGetter;
import org.modelmapper.spi.TypeSafeSourceGetter;

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

  // This field will be set with a value if the mapping is map from source
  private Class<?> sourceType;
  // This field will be set with a value if the mapping is map from constant
  private Object constant;

  private Errors errors;
  private Errors proxyErrors;

  public static <S, D> List<Accessor> collect(TypeMapImpl<S, D> typeMap, TypeSafeSourceGetter<S, ?> sourceGetter) {
    PropertyReferenceCollector collector = new PropertyReferenceCollector(typeMap.configuration, null);
    try {
      S source = ProxyFactory.proxyFor(typeMap.getSourceType(), collector.newSourceInterceptor(), collector.getProxyErrors());
      Object sourceProperty = sourceGetter.get(source);
      if (source == sourceProperty)
        collector.mapFromSource(typeMap.getSourceType());
      if (collector.isNoSourceGetter())
        collector.mapFromConstant(sourceProperty);
    } catch (NullPointerException e) {
      if (collector.getProxyErrors().hasErrors())
        throw collector.getProxyErrors().toException();
      throw e;
    } catch (ErrorsException e) {
      throw e.getErrors().toConfigurationException();
    }
    return collector.accessors;
  }

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
        return ProxyFactory.proxyFor(resolveReturnType(method), this, proxyErrors);
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
        return ProxyFactory.proxyFor(resolveReturnType(method), this, proxyErrors);
      } catch (ErrorsException e) {
        return null;
      }
    }
  }

  private static Class<?> resolveReturnType(Method method) {
    Accessor accessor = new MethodAccessor(method.getDeclaringClass(), method, method.getName());
    return accessor.getType();
  }

  SourceInterceptor newSourceInterceptor() {
    return new SourceInterceptor();
  }

  DestinationInterceptor newDestinationInterceptor() {
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

  MappingImpl collect() {
    if (mutators.isEmpty())
      errors.addMessage("Illegal DestinationSetter defined");
    errors.throwConfigurationExceptionIfErrorsExist();
    if (sourceType != null)
      return new SourceMappingImpl(sourceType, mutators, options);
    if (accessors.isEmpty())
      return new ConstantMappingImpl(constant, mutators, options);
    return new PropertyMappingImpl(accessors, mutators, options);
  }

  public Errors getErrors() {
    return errors;
  }

  Errors getProxyErrors() {
    return proxyErrors;
  }

  void mapFromSource(Class<?> sourceType) {
    this.sourceType = sourceType;
  }

  void mapFromConstant(Object constant) {
    this.constant = constant;
  }

  boolean isNoSourceGetter() {
    return accessors.isEmpty();
  }
}
