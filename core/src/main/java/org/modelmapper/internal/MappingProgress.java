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

import org.modelmapper.config.Configuration;
import org.modelmapper.spi.NameableType;
import org.modelmapper.spi.PropertyInfo;

/**
 * Tracks progress while intercepting a mapping's method invocation chain. Friend of
 * MappingBuilderImpl.
 * 
 * @param <T> PropertyInfo type
 * 
 * @author Jonathan Halterman
 */
abstract class MappingProgress<T extends PropertyInfo> {
  protected final MappingBuilderImpl<?, ?> builder;
  protected final Configuration config;
  protected final List<T> propertyInfo = new ArrayList<T>();

  MappingProgress(MappingBuilderImpl<?, ?> builder) {
    this.builder = builder;
    this.config = builder.configuration;
  }

  static class DestinationProgress extends MappingProgress<Mutator> {
    Object argument;

    DestinationProgress(MappingBuilderImpl<?, ?> builder) {
      super(builder);
    }

    @Override
    public void encountered(Class<?> proxyType, Method method, Object[] args) {
      if (PropertyResolver.MUTATORS.isValid(method)) {
        String propertyName = config.getDestinationNameTransformer().transform(method.getName(),
            NameableType.METHOD);
        propertyInfo.add(PropertyInfoRegistry.mutatorFor(proxyType, method, config, propertyName));
        argument = args.length == 1 ? args[0] : null;
      } else if (PropertyResolver.ACCESSORS.isValid(method)) {
        // Find mutator corresponding to accessor
        Mutator mutator = TypeInfoRegistry.typeInfoFor(proxyType, config).mutatorForAccessor(
            method.getName());
        if (mutator != null)
          propertyInfo.add(mutator);
        else
          builder.errors.missingMutatorForAccessor(method);
      } else
        builder.errors.invalidDestinationMethod(method);

      if (argument != null && argument == builder.source)
        builder.errors.missingSource();
    }

    @Override
    public void reset() {
      propertyInfo.clear();
      argument = null;
    }
  }

  static class SourceProgress extends MappingProgress<Accessor> {
    /** Property info for the next mapping in the PropertyMapping. */
    protected final List<Accessor> nextPropertyInfo = new ArrayList<Accessor>();

    SourceProgress(MappingBuilderImpl<?, ?> builder) {
      super(builder);
    }

    @Override
    public void encountered(Class<?> proxyType, Method method, Object[] args) {
      if (PropertyResolver.ACCESSORS.isValid(method)) {
        String propertyName = config.getSourceNameTransformer().transform(method.getName(),
            NameableType.METHOD);
        Accessor propInfo = PropertyInfoRegistry.accessorFor(proxyType, method, config,
            propertyName);
        if (!builder.destinationRequested)
          propertyInfo.add(propInfo);
        else
          nextPropertyInfo.add(propInfo);
      } else
        builder.errors.invalidSourceMethod(method);
    }

    /** Returns the current property info, else the next property info if the current is empty. */
    List<Accessor> propertyInfo() {
      return propertyInfo.isEmpty() ? nextPropertyInfo : propertyInfo;
    }

    /** Moves the next property info to the current property info and clears the next property info. */
    @Override
    void reset() {
      if (!propertyInfo.isEmpty()) {
        propertyInfo.clear();
        propertyInfo.addAll(nextPropertyInfo);
      }

      nextPropertyInfo.clear();
    }
  }

  boolean contains(Object object) {
    return propertyInfo.contains(object);
  }

  abstract void encountered(Class<?> proxyType, Method method, Object[] args);;

  abstract void reset();
}