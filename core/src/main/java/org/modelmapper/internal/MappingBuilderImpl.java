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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.Condition;
import org.modelmapper.ConfigurationException;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.Provider;
import org.modelmapper.builder.ConditionExpression;
import org.modelmapper.config.Configuration;
import org.modelmapper.internal.MappingProgress.DestinationProgress;
import org.modelmapper.internal.MappingProgress.SourceProgress;
import org.modelmapper.internal.util.Assert;
import org.modelmapper.internal.util.Types;

/**
 * Builds explicit property mappings.
 * 
 * @author Jonathan Halterman
 */
public class MappingBuilderImpl<S, D> implements ConditionExpression<S, D> {
  private static Method PROPERTY_MAP_CONFIGURE;
  private final Class<S> sourceType;
  private final Class<D> destinationType;
  final Configuration configuration;
  volatile S source;
  private volatile D destination;
  final Errors errors = new Errors();
  private final Set<MappingImpl> propertyMappings = new HashSet<MappingImpl>();
  private final SourceProgress sourceProgress;
  private final DestinationProgress destinationProgress;
  private MappingOptions options = new MappingOptions();
  boolean destinationRequested;

  static {
    PROPERTY_MAP_CONFIGURE = Types.methodFor(PropertyMap.class, "configure",
        MappingBuilderImpl.class);
    PROPERTY_MAP_CONFIGURE.setAccessible(true);
  }

  static class MappingOptions {
    Condition<?, ?> condition;
    Converter<?, ?> converter;
    Provider<?> provider;
    boolean skip;
    boolean mapFromSource;
  }

  MappingBuilderImpl(Class<S> sourceType, Class<D> destinationType, Configuration configuration) {
    this.sourceType = sourceType;
    this.destinationType = destinationType;
    this.configuration = configuration;
    sourceProgress = new SourceProgress(this);
    destinationProgress = new DestinationProgress(this);
  }

  public D skip() {
    saveLastMapping();
    options.skip = true;
    return getDestination();
  }

  public D map() {
    saveLastMapping();
    return getDestination();
  }

  public D map(Object source) {
    saveLastMapping();
    options.mapFromSource = source == this.source;
    return getDestination();
  }

  public ConditionExpression<S, D> using(Converter<?, ?> converter) {
    saveLastMapping();
    Assert.state(options.converter == null, "using() can only be called once per mapping.");
    options.converter = converter;
    return this;
  }

  public ConditionExpression<S, D> when(Condition<?, ?> condition) {
    saveLastMapping();
    Assert.state(options.condition == null, "when() can only be called once per mapping.");
    options.condition = condition;
    return this;
  }

  public ConditionExpression<S, D> withProvider(Provider<?> provider) {
    saveLastMapping();
    Assert.state(options.provider == null, "withProvider() can only be called once per mapping.");
    options.provider = provider;
    return this;
  }

  /**
   * Builds and returns all property mappings defined in the {@code propertyMap}.
   * 
   * @return map of destination property names to mappings
   */
  Collection<MappingImpl> build(PropertyMap<S, D> propertyMap) {
    try {
      PROPERTY_MAP_CONFIGURE.invoke(propertyMap, this);
      saveLastMapping();
    } catch (IllegalAccessException e) {
      errors.errorAccessingConfigure(e);
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();

      if (cause instanceof NullPointerException) {
        errors.invocationAgainstFinalClassOrMethod();
      } else if (cause instanceof ConfigurationException)
        errors.merge(((ConfigurationException) cause).getErrorMessages());
      else
        errors.addError("building mappings", cause);
    }

    errors.throwConfigurationExceptionIfErrorsExist();
    return propertyMappings;
  }

  private D getDestination() {
    destinationRequested = true;

    if (destination == null) {
      synchronized (MappingBuilderImpl.class) {
        if (destination == null) {
          try {
            destination = ProxyFactory.<D>proxyFor(destinationType, destinationProgress);
          } catch (ErrorsException e) {
            errors.merge(e.getErrors());
            errors.throwConfigurationExceptionIfErrorsExist();
          }
        }
      }
    }

    return destination;
  }

  public S getSource() {
    if (source == null) {
      synchronized (MappingBuilderImpl.class) {
        if (source == null) {
          try {
            source = ProxyFactory.<S>proxyFor(sourceType, sourceProgress);
          } catch (ErrorsException e) {
            errors.merge(e.getErrors());
            errors.throwConfigurationExceptionIfErrorsExist();
          }
        }
      }
    }

    return source;
  }

  private void saveLastMapping() {
    if (!destinationRequested) {
      // if (sourceProgress.hasProperties())
      // errors.sourceOutsideOfMap();
      return;
    }

    if (destinationProgress.propertyInfo.isEmpty())
      errors.missingDestination();
    // if (options.mapFromSource && options.converter == null)
    // errors.mapFromSourceWithoutConverter();

    try {
      if (!destinationProgress.propertyInfo.isEmpty()) {
        MappingImpl mapping = null;
        List<Accessor> sourcePropertyInfo = sourceProgress.propertyInfo();

        if (options.mapFromSource)
          mapping = new SourceMappingImpl(sourceType, destinationProgress.propertyInfo, options);
        else if (sourcePropertyInfo.isEmpty())
          mapping = new ConstantMappingImpl(destinationProgress.argument,
              destinationProgress.propertyInfo, options);
        else
          mapping = new PropertyMappingImpl(sourcePropertyInfo, destinationProgress.propertyInfo,
              options);

        if (!propertyMappings.add(mapping))
          errors.duplicateMapping(mapping.getLastDestinationProperty());
      }
    } finally {
      sourceProgress.reset();
      destinationProgress.reset();
      options = new MappingOptions();
      destinationRequested = false;
    }
  }
}
