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

import org.modelmapper.Provider;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.convention.NameTransformers;
import org.modelmapper.convention.NamingConventions;
import org.modelmapper.internal.converter.ConverterStore;
import org.modelmapper.internal.util.Assert;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MatchingStrategy;
import org.modelmapper.spi.NameTokenizer;
import org.modelmapper.spi.NameTransformer;
import org.modelmapper.spi.NamingConvention;

/**
 * Inheritable mapping configuration implementation.
 * 
 * @author Jonathan Halterman
 */
public class InheritingConfiguration implements Configuration {
  private final Configuration parent;
  public final TypeMapStore typeMapStore;
  public final ConverterStore converterStore;
  private NameTokenizer destinationNameTokenizer;
  private NameTransformer destinationNameTransformer;
  private NamingConvention destinationNamingConvention;
  private AccessLevel fieldAccessLevel;
  private MatchingStrategy matchingStrategy;
  private AccessLevel methodAccessLevel;
  private Provider<?> provider;
  private NameTokenizer sourceNameTokenizer;
  private NameTransformer sourceNameTransformer;
  private NamingConvention sourceNamingConvention;
  Boolean enableFieldMatching;
  Boolean ignoreAmbiguity;

  /**
   * Creates an initial InheritingConfiguration.
   */
  public InheritingConfiguration() {
    parent = null;
    typeMapStore = new TypeMapStore(this);
    converterStore = new ConverterStore();
    sourceNameTokenizer = NameTokenizers.CAMEL_CASE;
    destinationNameTokenizer = NameTokenizers.CAMEL_CASE;
    sourceNamingConvention = NamingConventions.JAVABEANS_ACCESSOR;
    destinationNamingConvention = NamingConventions.JAVABEANS_MUTATOR;
    sourceNameTransformer = NameTransformers.JAVABEANS_ACCESSOR;
    destinationNameTransformer = NameTransformers.JAVABEANS_MUTATOR;
    matchingStrategy = MatchingStrategies.STANDARD;
    fieldAccessLevel = AccessLevel.PUBLIC;
    methodAccessLevel = AccessLevel.PUBLIC;
    enableFieldMatching = Boolean.FALSE;
    ignoreAmbiguity = Boolean.FALSE;
  }

  /**
   * Creates a new InheritingConfiguration from the {@code source} configuration.
   */
  InheritingConfiguration(InheritingConfiguration source, boolean inherit) {
    // Stores are not inheritable
    typeMapStore = source.typeMapStore;
    converterStore = source.converterStore;

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
      enableFieldMatching = source.enableFieldMatching;
      ignoreAmbiguity = source.ignoreAmbiguity;
    }
  }

  public Configuration addConverter(ConditionalConverter<?, ?> converter) {
    Assert.notNull(converter, "converter");
    converterStore.add(converter);
    return this;
  }

  public Configuration copy() {
    return new InheritingConfiguration(this, false);
  }
  public Configuration enableFieldMatching(boolean enabled) {
    enableFieldMatching = enabled;
    return this;
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

  public Configuration ignoreAmbiguity(boolean ignore) {
    this.ignoreAmbiguity = ignore;
    return this;
  }

  public boolean isAmbiguityIgnored() {
    return ignoreAmbiguity == null ? parent.isAmbiguityIgnored() : ignoreAmbiguity;
  }

  public boolean isFieldMatchingEnabled() {
    return enableFieldMatching == null ? parent.isFieldMatchingEnabled() : enableFieldMatching;
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

  public Configuration setMatchingStrategy(MatchingStrategy matchingStrategy) {
    this.matchingStrategy = Assert.notNull(matchingStrategy);
    return this;
  }

  public Configuration setMethodAccessLevel(AccessLevel accessLevel) {
    methodAccessLevel = Assert.notNull(accessLevel);
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
