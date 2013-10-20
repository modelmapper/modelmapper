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
import org.modelmapper.internal.converter.ConverterStore;
import org.modelmapper.internal.util.Assert;
import org.modelmapper.internal.valueaccess.ValueAccessStore;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MatchingStrategy;
import org.modelmapper.spi.NameTokenizer;
import org.modelmapper.spi.NameTransformer;
import org.modelmapper.spi.NamingConvention;
import org.modelmapper.spi.ValueReader;

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

  /**
   * Creates an initial InheritingConfiguration.
   */
  public InheritingConfiguration() {
    parent = null;
    typeMapStore = new TypeMapStore(this);
    converterStore = new ConverterStore();
    valueAccessStore = new ValueAccessStore();
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
  }

  /**
   * Creates a new InheritingConfiguration from the {@code source} configuration.
   */
  InheritingConfiguration(InheritingConfiguration source, boolean inherit) {
    // Stores are not inheritable
    typeMapStore = source.typeMapStore;
    converterStore = source.converterStore;
    valueAccessStore = source.valueAccessStore;

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
    }
  }

  public <T> Configuration addValueReader(ValueReader<T> valueReader) {
    getValueReaders().add(valueReader);
    return this;
  }

  public Configuration copy() {
    return new InheritingConfiguration(this, false);
  }

  /**
   * Determines equality from the name transformers, access levels and field matching configuration.
   */
  @Override
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
    return true;
  }

  public List<ConditionalConverter<?, ?>> getConverters() {
    return converterStore.getConverters();
  }

  public NameTokenizer getDestinationNameTokenizer() {
    return destinationNameTokenizer == null ? parent.getDestinationNameTokenizer()
        : destinationNameTokenizer;
  }

  public NameTransformer getDestinationNameTransformer() {
    return destinationNameTransformer == null ? parent.getDestinationNameTransformer()
        : destinationNameTransformer;
  }

  public NamingConvention getDestinationNamingConvention() {
    return destinationNamingConvention == null ? parent.getDestinationNamingConvention()
        : destinationNamingConvention;
  }

  public AccessLevel getFieldAccessLevel() {
    return fieldAccessLevel == null ? parent.getFieldAccessLevel() : fieldAccessLevel;
  }

  public MatchingStrategy getMatchingStrategy() {
    return matchingStrategy == null ? parent.getMatchingStrategy() : matchingStrategy;
  }

  public AccessLevel getMethodAccessLevel() {
    return methodAccessLevel == null ? parent.getMethodAccessLevel() : methodAccessLevel;
  }

  public Condition<?, ?> getPropertyCondition() {
    return propertyCondition;
  }

  public Provider<?> getProvider() {
    return provider;
  }

  public NameTokenizer getSourceNameTokenizer() {
    return sourceNameTokenizer == null ? parent.getSourceNameTokenizer() : sourceNameTokenizer;
  }

  public NameTransformer getSourceNameTransformer() {
    return sourceNameTransformer == null ? parent.getSourceNameTransformer()
        : sourceNameTransformer;
  }

  public NamingConvention getSourceNamingConvention() {
    return sourceNamingConvention == null ? parent.getSourceNamingConvention()
        : sourceNamingConvention;
  }

  public List<ValueReader<?>> getValueReaders() {
    return valueAccessStore.getValueReaders();
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

  public boolean isAmbiguityIgnored() {
    return ambiguityIgnored == null ? parent.isAmbiguityIgnored() : ambiguityIgnored;
  }

  public boolean isFieldMatchingEnabled() {
    return fieldMatchingEnabled == null ? parent.isFieldMatchingEnabled() : fieldMatchingEnabled;
  }

  public boolean isFullTypeMatchingRequired() {
    return fullTypeMatchingRequired == null ? parent.isFullTypeMatchingRequired()
        : fullTypeMatchingRequired;
  }

  public boolean isImplicitMappingEnabled() {
    return implicitMatchingEnabled == null ? parent.isImplicitMappingEnabled()
        : implicitMatchingEnabled;
  }

  public Configuration setAmbiguityIgnored(boolean ignore) {
    this.ambiguityIgnored = ignore;
    return this;
  }

  public Configuration setDestinationNameTokenizer(NameTokenizer nameTokenizer) {
    destinationNameTokenizer = Assert.notNull(nameTokenizer);
    return this;
  }

  public Configuration setDestinationNameTransformer(NameTransformer nameTransformer) {
    destinationNameTransformer = Assert.notNull(nameTransformer);
    return this;
  }

  public Configuration setDestinationNamingConvention(NamingConvention namingConvention) {
    destinationNamingConvention = Assert.notNull(namingConvention);
    return this;
  }

  public Configuration setFieldAccessLevel(AccessLevel accessLevel) {
    fieldAccessLevel = Assert.notNull(accessLevel);
    return this;
  }

  public Configuration setFieldMatchingEnabled(boolean enabled) {
    fieldMatchingEnabled = enabled;
    return this;
  }

  public Configuration setFullTypeMatchingRequired(boolean required) {
    fullTypeMatchingRequired = required;
    return this;
  }

  public Configuration setImplicitMappingEnabled(boolean enabled) {
    implicitMatchingEnabled = enabled;
    return this;
  }

  public Configuration setMatchingStrategy(MatchingStrategy matchingStrategy) {
    this.matchingStrategy = Assert.notNull(matchingStrategy);
    return this;
  }

  public Configuration setMethodAccessLevel(AccessLevel accessLevel) {
    methodAccessLevel = Assert.notNull(accessLevel);
    return this;
  }

  public Configuration setPropertyCondition(Condition<?, ?> condition) {
    propertyCondition = Assert.notNull(condition);
    return this;
  }

  public Configuration setProvider(Provider<?> provider) {
    this.provider = Assert.notNull(provider);
    return this;
  }

  public Configuration setSourceNameTokenizer(NameTokenizer nameTokenizer) {
    sourceNameTokenizer = Assert.notNull(nameTokenizer);
    return this;
  }

  public Configuration setSourceNameTransformer(NameTransformer nameTransformer) {
    sourceNameTransformer = Assert.notNull(nameTransformer);
    return this;
  }

  public Configuration setSourceNamingConvention(NamingConvention namingConvention) {
    sourceNamingConvention = Assert.notNull(namingConvention);
    return this;
  }
}
