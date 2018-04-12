/*
 * Copyright 2011-2014 the original author or authors.
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

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.modelmapper.Condition;
import org.modelmapper.ConfigurationException;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.Provider;
import org.modelmapper.builder.ConditionExpression;
import org.modelmapper.internal.ExplicitMappingVisitor.VisitedMapping;
import org.modelmapper.internal.PropertyInfoImpl.FieldPropertyInfo;
import org.modelmapper.internal.PropertyInfoImpl.MethodAccessor;
import org.modelmapper.internal.PropertyInfoImpl.ValueReaderPropertyInfo;
import org.modelmapper.internal.util.Assert;
import org.modelmapper.internal.util.Members;
import org.modelmapper.internal.util.Types;
import org.modelmapper.spi.PropertyType;
import org.modelmapper.spi.ValueReader;
import org.objectweb.asm.ClassReader;

/**
 * Builds explicit property mappings.
 *
 * @author Jonathan Halterman
 */
public class ExplicitMappingBuilder<S, D> implements ConditionExpression<S, D> {
  private static final Pattern DOT_PATTERN = Pattern.compile("\\.");
  private static Method PROPERTY_MAP_CONFIGURE;

  private final Class<S> sourceType;
  private final Class<D> destinationType;
  private final InheritingConfiguration configuration;
  public volatile S source;
  public volatile D destination;
  private final Errors proxyErrors = new Errors();
  private final Errors errors = new Errors();
  private List<VisitedMapping> visitedMappings;
  private final Map<Object, ExplicitMappingInterceptor> proxyInterceptors = new IdentityHashMap<Object, ExplicitMappingInterceptor>();
  private final Set<MappingImpl> propertyMappings = new HashSet<MappingImpl>();

  /** Per mapping state */
  private int currentMappingIndex;
  private VisitedMapping currentMapping;
  private MappingOptions options = new MappingOptions();
  private List<Accessor> sourceAccessors;
  private Object sourceConstant;

  static {
    PROPERTY_MAP_CONFIGURE = Members.methodFor(PropertyMap.class, "configure",
        ExplicitMappingBuilder.class);
    PROPERTY_MAP_CONFIGURE.setAccessible(true);
  }

  static class MappingOptions {
    Condition<?, ?> condition;
    Converter<?, ?> converter;
    Provider<?> provider;
    int skipType;
    boolean mapFromSource;
  }

  ExplicitMappingBuilder(Class<S> sourceType, Class<D> destinationType,
      InheritingConfiguration configuration) {
    this.sourceType = sourceType;
    this.destinationType = destinationType;
    this.configuration = configuration;
  }

  public D skip() {
    map();
    options.skipType = 1;
    return destination;
  }

  public void skip(Object destination) {
    map(destination);
    options.skipType = 2;
  }

  public void skip(Object source, Object destination) {
    map(source, destination);
    options.skipType = 3;
  }

  public D map() {
    saveLastMapping();
    getNextMapping();
    return destination;
  }

  public D map(Object subject) {
    saveLastMapping();
    getNextMapping();
    recordSourceValue(subject);
    return destination;
  }

  public void map(Object source, Object destination) {
    saveLastMapping();
    getNextMapping();
    recordSourceValue(source);
  }

  public <T> T source(String sourcePropertyPath) {
    if (sourcePropertyPath == null)
      errors.errorNullArgument("sourcePropertyPath");
    if (sourceAccessors != null)
      saveLastMapping();

    String[] propertyNames = DOT_PATTERN.split(sourcePropertyPath);
    sourceAccessors = new ArrayList<Accessor>(propertyNames.length);
    ValueReader<?> valueReader = configuration.valueAccessStore.getFirstSupportedReader(sourceType);
    if (valueReader != null)
      for (String propertyName : propertyNames)
        sourceAccessors.add(new ValueReaderPropertyInfo(valueReader, Object.class, propertyName));
    else {
      Accessor accessor = null;
      for (String propertyName : propertyNames) {
        Class<?> propertyType = accessor == null ? sourceType : accessor.getType();
        TypeInfoRegistry.typeInfoFor(propertyType, configuration).getAccessors();
        accessor = PropertyInfoRegistry.accessorFor(propertyType, propertyName, configuration);
        if (accessor == null) {
          errors.errorInvalidSourcePath(sourcePropertyPath, propertyType, propertyName);
          return null;
        }

        sourceAccessors.add(accessor);
      }
    }

    return null;
  }

  public ConditionExpression<S, D> using(Converter<?, ?> converter) {
    saveLastMapping();
    if (converter == null)
      errors.errorNullArgument("converter");
    Assert.state(options.converter == null, "using() can only be called once per mapping.");
    options.converter = converter;
    return this;
  }

  public ConditionExpression<S, D> when(Condition<?, ?> condition) {
    saveLastMapping();
    if (condition == null)
      errors.errorNullArgument("condition");
    Assert.state(options.condition == null, "when() can only be called once per mapping.");
    options.condition = condition;
    return this;
  }

  public ConditionExpression<S, D> with(Provider<?> provider) {
    saveLastMapping();
    if (provider == null)
      errors.errorNullArgument("provider");
    Assert.state(options.provider == null, "withProvider() can only be called once per mapping.");
    options.provider = provider;
    return this;
  }

  /**
   * Builds and returns all property mappings defined in the {@code propertyMap}.
   */
  Collection<MappingImpl> build(PropertyMap<S, D> propertyMap) {
    try {
      PROPERTY_MAP_CONFIGURE.invoke(propertyMap, this);
      saveLastMapping();
    } catch (IllegalAccessException e) {
      errors.errorAccessingConfigure(e);
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();
      if (cause instanceof ConfigurationException)
        throw (ConfigurationException) cause;
      else
        errors.addMessage(cause, "Failed to configure mappings");
    } catch (NullPointerException e) {
      if (proxyErrors.hasErrors()) {
        throw proxyErrors.toException();
      }
      throw e;
    }

    errors.throwConfigurationExceptionIfErrorsExist();
    return propertyMappings;
  }

  /**
   * Visits the {@code propertyMap} and captures and validates mappings.
   */
  public void visitPropertyMap(PropertyMap<S, D> propertyMap) {
    String propertyMapClassName = propertyMap.getClass().getName();

    try {
      ClassReader cr = new ClassReader(propertyMap.getClass().getClassLoader().getResourceAsStream(
          propertyMapClassName.replace('.', '/') + ".class"));
      ExplicitMappingVisitor visitor = new ExplicitMappingVisitor(errors, configuration,
          propertyMapClassName, destinationType.getName(), propertyMap.getClass().getClassLoader());
      cr.accept(visitor, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
      visitedMappings = visitor.mappings;
    } catch (IOException e) {
      errors.errorReadingClass(e, propertyMapClassName);
    }

    validateVisitedMappings();
    errors.throwConfigurationExceptionIfErrorsExist();
    createProxies();
  }

  /**
   * Validates mappings that were visited by ExplicitMappingVisitor.
   */
  private void validateVisitedMappings() {
    for (VisitedMapping mapping : visitedMappings) {
      if (mapping.destinationMutators.isEmpty())
        errors.missingDestination();
    }
  }

  /**
   * Creates the source and destination proxy models.
   */
  private void createProxies() {
    source = createProxy(sourceType);
    destination = createProxy(destinationType);

    for (VisitedMapping mapping : visitedMappings) {
      createAccessorProxies(source, mapping.sourceAccessors);
      createAccessorProxies(destination, mapping.destinationAccessors);
    }
  }

  private void createAccessorProxies(Object proxy, List<Accessor> accessors) {
    for (Accessor accessor : accessors) {
      if (accessor instanceof MethodAccessor) {
        ExplicitMappingInterceptor interceptor = proxyInterceptors.get(proxy);
        String methodName = accessor.getMember().getName();
        proxy = interceptor.methodProxies.get(methodName);
        if (proxy == null) {
          proxy = createProxy(accessor.getType());
          interceptor.methodProxies.put(methodName, proxy);
        }
      } else if (accessor instanceof FieldPropertyInfo) {
        FieldPropertyInfo field = (FieldPropertyInfo) accessor;
        Object nextProxy = field.getValue(proxy);
        if (nextProxy == null) {
          nextProxy = createProxy(field.getType());
          field.setValue(proxy, nextProxy);
        }
        proxy = nextProxy;
      }
    }
  }

  public final class ExplicitMappingInterceptor implements InvocationHandler {
    private final Map<String, Object> methodProxies = new HashMap<String, Object>();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
      if (args.length == 1) {
        sourceConstant = args[0];
        if (sourceConstant != null && sourceConstant == source)
          errors.missingSource();
      }
      return methodProxies.get(method.getName());
    }
  }

  private <T> T createProxy(Class<T> type) {
    ExplicitMappingInterceptor interceptor = new ExplicitMappingInterceptor();

    try {
      T proxy = ProxyFactory.proxyFor(type, interceptor, proxyErrors, configuration.isUseOSGiClassLoaderBridging());
      proxyInterceptors.put(proxy, interceptor);
      return proxy;
    } catch (ErrorsException e) {
      return null;
    }
  }

  private void getNextMapping() {
    if (currentMappingIndex < visitedMappings.size())
      currentMapping = visitedMappings.get(currentMappingIndex++);
  }

  private void recordSourceValue(Object sourceValue) {
    if (sourceValue != null) {
      if (sourceValue == source)
        options.mapFromSource = true;
      else if (!Types.isProxied(sourceValue.getClass()))
        sourceConstant = sourceValue;
    }
  }

  /**
   * Validates the current mapping that was recorded via a MapExpression.
   */
  private void validateRecordedMapping() {
    // If mapping a field without a source
    if (options.skipType == 0
        && (currentMapping.sourceAccessors == null || currentMapping.sourceAccessors.isEmpty())
        && currentMapping.destinationMutators.get(currentMapping.destinationMutators.size() - 1)
            .getPropertyType()
            .equals(PropertyType.FIELD) && options.converter == null && !options.mapFromSource
        && sourceConstant == null)
      errors.missingSource();
    else if (options.skipType == 2 && options.condition != null)
      errors.conditionalSkipWithoutSource();
  }

  private void saveLastMapping() {
    if (currentMapping != null) {
      try {
        MappingImpl mapping;
        if (currentMapping.sourceAccessors.isEmpty())
          currentMapping.sourceAccessors = sourceAccessors;
        validateRecordedMapping();

        if (options.mapFromSource)
          mapping = new SourceMappingImpl(sourceType, currentMapping.destinationMutators, options);
        else if (currentMapping.sourceAccessors == null)
          mapping = new ConstantMappingImpl(sourceConstant, currentMapping.destinationMutators,
              options);
        else
          mapping = new PropertyMappingImpl(currentMapping.sourceAccessors,
              currentMapping.destinationMutators, options);

        if (!propertyMappings.add(mapping))
          errors.duplicateMapping(mapping.getLastDestinationProperty());
      } finally {
        currentMapping = null;
        options = new MappingOptions();
        sourceAccessors = null;
        sourceConstant = null;
      }
    }
  }
}