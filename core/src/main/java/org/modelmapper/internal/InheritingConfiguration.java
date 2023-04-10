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

import java.util.List;

import org.modelmapper.Condition;
import org.modelmapper.Provider;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.convention.NameTransformers;
import org.modelmapper.convention.NamingConventions;
import org.modelmapper.internal.converter.AssignableConverter;
import org.modelmapper.internal.converter.ConverterStore;
import org.modelmapper.internal.converter.MergingCollectionConverter;
import org.modelmapper.internal.converter.NonMergingCollectionConverter;
import org.modelmapper.internal.util.Assert;
import org.modelmapper.internal.valueaccess.ValueAccessStore;
import org.modelmapper.internal.valuemutate.ValueMutateStore;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MatchingStrategy;
import org.modelmapper.spi.NameTokenizer;
import org.modelmapper.spi.NameTransformer;
import org.modelmapper.spi.NamingConvention;
import org.modelmapper.spi.ValueReader;
import org.modelmapper.spi.ValueWriter;

/**
 * Inheritable mapping configuration implementation.
 *
 * @author Jonathan Halterman
 */
public class InheritingConfiguration implements Configuration {
  private final Configuration parent;
  public final TypeMapStore typeMapStore;
  public final ConverterStore converterStore;
  public final ValueAccessStore valueAccessStore;
  public final ValueMutateStore valueMutateStore;
  private NameTokenizer destinationNameTokenizer;
  private NameTransformer destinationNameTransformer;
  private NamingConvention destinationNamingConvention;
  private AccessLevel fieldAccessLevel;
  private MatchingStrategy matchingStrategy;
  private AccessLevel methodAccessLevel;
  private Provider<?> provider;
  private Condition<?, ?> propertyCondition;
  private NameTokenizer sourceNameTokenizer;
  private NameTransformer sourceNameTransformer;
  private NamingConvention sourceNamingConvention;
  private Boolean fieldMatchingEnabled;
  private Boolean ambiguityIgnored;
  private Boolean fullTypeMatchingRequired;
  private Boolean implicitMatchingEnabled;
  private Boolean preferNestedProperties;
  private Boolean skipNullEnabled;
  private Boolean collectionsMergeEnabled;
  private Boolean useOSGiClassLoaderBridging;

  /**
   * Creates an initial InheritingConfiguration.
   */
  public InheritingConfiguration() {
    parent = null;
    typeMapStore = new TypeMapStore(this);
    converterStore = new ConverterStore();
    valueAccessStore = new ValueAccessStore();
    valueMutateStore = new ValueMutateStore();
    sourceNameTokenizer = NameTokenizers.CAMEL_CASE;
    destinationNameTokenizer = NameTokenizers.CAMEL_CASE;
    sourceNamingConvention = NamingConventions.JAVABEANS_ACCESSOR;
    destinationNamingConvention = NamingConventions.JAVABEANS_MUTATOR;
    sourceNameTransformer = NameTransformers.JAVABEANS_ACCESSOR;
    destinationNameTransformer = NameTransformers.JAVABEANS_MUTATOR;
    matchingStrategy = MatchingStrategies.STANDARD;
    fieldAccessLevel = AccessLevel.PUBLIC;
    methodAccessLevel = AccessLevel.PUBLIC;
    fieldMatchingEnabled = Boolean.FALSE;
    ambiguityIgnored = Boolean.FALSE;
    fullTypeMatchingRequired = Boolean.FALSE;
    implicitMatchingEnabled = Boolean.TRUE;
    preferNestedProperties = Boolean.TRUE;
    skipNullEnabled = Boolean.FALSE;
    useOSGiClassLoaderBridging = Boolean.FALSE;
    collectionsMergeEnabled = Boolean.FALSE;
  }

  /**
   * Creates a new InheritingConfiguration from the {@code source} configuration.
   */
  InheritingConfiguration(InheritingConfiguration source, boolean inherit) {
    // Stores are not inheritable
    typeMapStore = source.typeMapStore;
    converterStore = source.converterStore;
    valueAccessStore = source.valueAccessStore;
    valueMutateStore = source.valueMutateStore;

    if (inherit) {
      this.parent = source;
    } else {
      parent = null;
      sourceNameTokenizer = source.sourceNameTokenizer;
      destinationNameTokenizer = source.destinationNameTokenizer;
      sourceNamingConvention = source.sourceNamingConvention;
      destinationNamingConvention = source.destinationNamingConvention;
      sourceNameTransformer = source.sourceNameTransformer;
      destinationNameTransformer = source.destinationNameTransformer;
      matchingStrategy = source.matchingStrategy;
      fieldAccessLevel = source.fieldAccessLevel;
      methodAccessLevel = source.methodAccessLevel;
      fieldMatchingEnabled = source.fieldMatchingEnabled;
      ambiguityIgnored = source.ambiguityIgnored;
      provider = source.provider;
      propertyCondition = source.propertyCondition;
      fullTypeMatchingRequired = source.fullTypeMatchingRequired;
      implicitMatchingEnabled = source.implicitMatchingEnabled;
      preferNestedProperties = source.preferNestedProperties;
      skipNullEnabled = source.skipNullEnabled;
      collectionsMergeEnabled = source.collectionsMergeEnabled;
    }
  }

  @Override
  public <T> Configuration addValueReader(ValueReader<T> valueReader) {
    getValueReaders().add(valueReader);
    return this;
  }

  @Override
  public <T> Configuration addValueWriter(ValueWriter<T> valueWriter) {
    getValueWriters().add(valueWriter);
    return this;
  }

  @Override
  public Configuration copy() {
    return new InheritingConfiguration(this, false);
  }

  /**
   * Determines equality from the name transformers, access levels and field matching configuration.
   */
  @Override
  @SuppressWarnings("all")
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;

    InheritingConfiguration other = (InheritingConfiguration) obj;
    if (!getSourceNameTransformer().equals(other.getSourceNameTransformer()))
      return false;
    if (!getDestinationNameTransformer().equals(other.getDestinationNameTransformer()))
      return false;
    if (getFieldAccessLevel() != other.getFieldAccessLevel())
      return false;
    if (getMethodAccessLevel() != other.getMethodAccessLevel())
      return false;
    if (isFieldMatchingEnabled() != other.isFieldMatchingEnabled())
      return false;
    if (!getSourceNamingConvention().equals(other.getSourceNamingConvention()))
      return false;
    if (!getDestinationNamingConvention().equals(other.getDestinationNamingConvention()))
      return false;
    return true;
  }

  @Override
  public List<ConditionalConverter<?, ?>> getConverters() {
    return converterStore.getConverters();
  }

  @Override
  public NameTokenizer getDestinationNameTokenizer() {
    return destinationNameTokenizer == null
        ? Assert.notNull(parent).getDestinationNameTokenizer()
        : destinationNameTokenizer;
  }

  @Override
  public NameTransformer getDestinationNameTransformer() {
    return destinationNameTransformer == null
        ? Assert.notNull(parent).getDestinationNameTransformer()
        : destinationNameTransformer;
  }

  @Override
  public NamingConvention getDestinationNamingConvention() {
    return destinationNamingConvention == null
        ? Assert.notNull(parent).getDestinationNamingConvention()
        : destinationNamingConvention;
  }

  @Override
  public AccessLevel getFieldAccessLevel() {
    return fieldAccessLevel == null
        ? Assert.notNull(parent).getFieldAccessLevel()
        : fieldAccessLevel;
  }

  @Override
  public MatchingStrategy getMatchingStrategy() {
    return matchingStrategy == null
        ? Assert.notNull(parent).getMatchingStrategy()
        : matchingStrategy;
  }

  @Override
  public AccessLevel getMethodAccessLevel() {
    return methodAccessLevel == null
        ? Assert.notNull(parent).getMethodAccessLevel()
        : methodAccessLevel;
  }

  @Override
  public Condition<?, ?> getPropertyCondition() {
    if (parent != null)
      return propertyCondition == null
          ? parent.getPropertyCondition()
          : propertyCondition;
    return propertyCondition;
  }

  @Override
  public Provider<?> getProvider() {
    if (parent != null)
      return provider == null
          ? Assert.notNull(parent).getProvider()
          : provider;
    return provider;
  }

  @Override
  public NameTokenizer getSourceNameTokenizer() {
    return sourceNameTokenizer == null
        ? Assert.notNull(parent).getSourceNameTokenizer()
        : sourceNameTokenizer;
  }

  @Override
  public NameTransformer getSourceNameTransformer() {
    return sourceNameTransformer == null
        ? Assert.notNull(parent).getSourceNameTransformer()
        : sourceNameTransformer;
  }

  @Override
  public NamingConvention getSourceNamingConvention() {
    return sourceNamingConvention == null
        ? Assert.notNull(parent).getSourceNamingConvention()
        : sourceNamingConvention;
  }

  @Override
  public List<ValueReader<?>> getValueReaders() {
    return valueAccessStore.getValueReaders();
  }

  @Override
  public List<ValueWriter<?>> getValueWriters() {
    return valueMutateStore.getValueWriters();
  }

  /**
   * Produces a hash code from the name transformers, access levels and field matching
   * configuration.
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + getSourceNameTransformer().hashCode();
    result = prime * result + getDestinationNameTransformer().hashCode();
    result = prime * result + getFieldAccessLevel().hashCode();
    result = prime * result + getMethodAccessLevel().hashCode();
    result = prime * result + (isFieldMatchingEnabled() ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean isAmbiguityIgnored() {
    return ambiguityIgnored == null
        ? Assert.notNull(parent).isAmbiguityIgnored()
        : ambiguityIgnored;
  }

  @Override
  public boolean isFieldMatchingEnabled() {
    return fieldMatchingEnabled == null
        ? Assert.notNull(parent).isFieldMatchingEnabled()
        : fieldMatchingEnabled;
  }

  @Override
  public boolean isFullTypeMatchingRequired() {
    return fullTypeMatchingRequired == null
        ? Assert.notNull(parent).isFullTypeMatchingRequired()
        : fullTypeMatchingRequired;
  }

  @Override
  public boolean isImplicitMappingEnabled() {
    return implicitMatchingEnabled == null
        ? Assert.notNull(parent).isImplicitMappingEnabled()
        : implicitMatchingEnabled;
  }

  @Override
  public boolean isPreferNestedProperties() {
    return preferNestedProperties == null
        ? Assert.notNull(parent).isPreferNestedProperties()
        : preferNestedProperties;
  }

  @Override
  public boolean isSkipNullEnabled() {
    return skipNullEnabled == null
        ? Assert.notNull(parent).isSkipNullEnabled()
        : skipNullEnabled;
  }

  @Override
  public boolean isUseOSGiClassLoaderBridging() {
    return useOSGiClassLoaderBridging == null
        ? Assert.notNull(parent).isUseOSGiClassLoaderBridging()
        : useOSGiClassLoaderBridging;
  }

  @Override
  public boolean isDeepCopyEnabled() {
    return !converterStore.hasConverter(AssignableConverter.class);
  }

  @Override
  public boolean isCollectionsMergeEnabled() {
    return converterStore.hasConverter(MergingCollectionConverter.class);
  }

  @Override
  public Configuration setAmbiguityIgnored(boolean ignore) {
    this.ambiguityIgnored = ignore;
    return this;
  }

  @Override
  public Configuration setDestinationNameTokenizer(NameTokenizer nameTokenizer) {
    destinationNameTokenizer = Assert.notNull(nameTokenizer);
    return this;
  }

  @Override
  public Configuration setDestinationNameTransformer(NameTransformer nameTransformer) {
    destinationNameTransformer = Assert.notNull(nameTransformer);
    return this;
  }

  @Override
  public Configuration setDestinationNamingConvention(NamingConvention namingConvention) {
    destinationNamingConvention = Assert.notNull(namingConvention);
    return this;
  }

  @Override
  public Configuration setFieldAccessLevel(AccessLevel accessLevel) {
    fieldAccessLevel = Assert.notNull(accessLevel);
    return this;
  }

  @Override
  public Configuration setFieldMatchingEnabled(boolean enabled) {
    fieldMatchingEnabled = enabled;
    return this;
  }

  @Override
  public Configuration setFullTypeMatchingRequired(boolean required) {
    fullTypeMatchingRequired = required;
    return this;
  }

  @Override
  public Configuration setImplicitMappingEnabled(boolean enabled) {
    implicitMatchingEnabled = enabled;
    return this;
  }

  @Override
  public Configuration setPreferNestedProperties(boolean enabled) {
    preferNestedProperties = enabled;
    return this;
  }

  @Override
  public Configuration setSkipNullEnabled(boolean enabled) {
    skipNullEnabled = enabled;
    return this;
  }

  @Override
  public Configuration setDeepCopyEnabled(boolean enabled) {
    if (enabled && converterStore.hasConverter(AssignableConverter.class))
      converterStore.removeConverter(AssignableConverter.class);
    else if (!enabled && !converterStore.hasConverter(AssignableConverter.class))
      converterStore.addConverter(new AssignableConverter());
    return this;
  }

  @Override
  public Configuration setCollectionsMergeEnabled(boolean enabled) {
    if (enabled) {
      converterStore.replaceConverter(NonMergingCollectionConverter.class, new MergingCollectionConverter());
    } else {
      converterStore.replaceConverter(MergingCollectionConverter.class, new NonMergingCollectionConverter());
    }
    return this;
  }

  @Override
  public Configuration setMatchingStrategy(MatchingStrategy matchingStrategy) {
    this.matchingStrategy = Assert.notNull(matchingStrategy);
    return this;
  }

  @Override
  public Configuration setMethodAccessLevel(AccessLevel accessLevel) {
    methodAccessLevel = Assert.notNull(accessLevel);
    return this;
  }

  @Override
  public Configuration setPropertyCondition(Condition<?, ?> condition) {
    propertyCondition = Assert.notNull(condition);
    return this;
  }

  @Override
  public Configuration setProvider(Provider<?> provider) {
    this.provider = Assert.notNull(provider);
    return this;
  }

  @Override
  public Configuration setSourceNameTokenizer(NameTokenizer nameTokenizer) {
    sourceNameTokenizer = Assert.notNull(nameTokenizer);
    return this;
  }

  @Override
  public Configuration setSourceNameTransformer(NameTransformer nameTransformer) {
    sourceNameTransformer = Assert.notNull(nameTransformer);
    return this;
  }

  @Override
  public Configuration setSourceNamingConvention(NamingConvention namingConvention) {
    sourceNamingConvention = Assert.notNull(namingConvention);
    return this;
  }

  @Override
  public Configuration setUseOSGiClassLoaderBridging(boolean useOSGiClassLoaderBridging) {
    this.useOSGiClassLoaderBridging = useOSGiClassLoaderBridging;
    return this;
  }
}
