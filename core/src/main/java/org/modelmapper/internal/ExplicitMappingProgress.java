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
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.spi.NameableType;
import org.modelmapper.spi.PropertyInfo;

/**
 * Tracks progress while intercepting a mapping's method invocation chain. Friend of
 * ExplicitMappingBuilder.
 * 
 * @param <T> PropertyInfo type
 * 
 * @author Jonathan Halterman
 */
abstract class ExplicitMappingProgress<T extends PropertyInfo> {
  protected final ExplicitMappingBuilder<?, ?> builder;
  protected final InheritingConfiguration config;
  protected final List<T> propertyInfo = new ArrayList<T>();
  protected int inConstructor;

  ExplicitMappingProgress(ExplicitMappingBuilder<?, ?> builder) {
    this.builder = builder;
    this.config = builder.configuration;
  }

  static class DestinationProgress extends ExplicitMappingProgress<Mutator> {
    Object argument;

    DestinationProgress(ExplicitMappingBuilder<?, ?> builder) {
      super(builder);
    }

    @Override
    public void encountered(Class<?> proxyType, Method method, Object[] args) {
      if (inConstructor > 0)
        return;

      if (PropertyInfoResolver.MUTATORS.isValid(method)) {
        String propertyName = config.getDestinationNameTransformer().transform(method.getName(),
            NameableType.METHOD);
        propertyInfo.add(PropertyInfoRegistry.mutatorFor(proxyType, method, config, propertyName));
        argument = args.length == 1 ? args[0] : null;
        if (argument != null && argument == builder.source)
          builder.errors.missingSource();
        builder.saveMapping();
      } else if (PropertyInfoResolver.ACCESSORS.isValid(method)) {
        // Find mutator corresponding to accessor
        Mutator mutator = TypeInfoRegistry.typeInfoFor(proxyType, config).mutatorForAccessorMethod(
            method.getName());
        if (mutator != null)
          propertyInfo.add(mutator);
        else
          builder.errors.missingMutatorForAccessor(method);
      } else
        builder.errors.invalidDestinationMethod(method);
    }

    @Override
    public void reset() {
      propertyInfo.clear();
      argument = null;
    }
  }

  static class SourceProgress extends ExplicitMappingProgress<Accessor> {
    SourceProgress(ExplicitMappingBuilder<?, ?> builder) {
      super(builder);
    }

    @Override
    public void encountered(Class<?> proxyType, Method method, Object[] args) {
      if (inConstructor > 0)
        return;

      if (PropertyInfoResolver.ACCESSORS.isValid(method)) {
        String propertyName = config.getSourceNameTransformer().transform(method.getName(),
            NameableType.METHOD);
        Accessor propInfo = PropertyInfoRegistry.accessorFor(proxyType, method, config,
            propertyName);
        propertyInfo.add(propInfo);
      } else
        builder.errors.invalidSourceMethod(method);
    }

    /** Moves the next property info to the current property info and clears the next property info. */
    @Override
    void reset() {
      propertyInfo.clear();
    }
  }

  boolean contains(Object object) {
    return propertyInfo.contains(object);
  }

  void enterConstructor() {
    this.inConstructor++;
  }

  void leaveConstructor() {
    this.inConstructor--;
  }

  abstract void encountered(Class<?> proxyType, Method method, Object[] args);

  abstract void reset();
}