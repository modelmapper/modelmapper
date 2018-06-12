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

import org.modelmapper.config.Configuration;
import org.modelmapper.internal.util.Stack;
import org.modelmapper.internal.util.ToStringBuilder;
import org.modelmapper.spi.NameableType;
import org.modelmapper.spi.PropertyInfo;
import org.modelmapper.spi.PropertyNameInfo;
import org.modelmapper.spi.Tokens;

/**
 * PropertyNameInfo implementation that tracks source and destination properties.
 * 
 * @author Jonathan Halterman
 */
class PropertyNameInfoImpl implements PropertyNameInfo {
  private final Class<?> sourceClass;
  private final Configuration configuration;
  private Tokens sourceClassTokens;
  private Stack<Tokens> sourcePropertyTypeTokens;
  private final Stack<Tokens> sourcePropertyTokens = new Stack<Tokens>();
  private final Stack<Tokens> destinationPropertyTokens = new Stack<Tokens>();
  private final Stack<PropertyInfo> sourceProperties = new Stack<PropertyInfo>();
  private final Stack<PropertyInfo> destinationProperties = new Stack<PropertyInfo>();

  PropertyNameInfoImpl(Class<?> sourceClass, Configuration configuration) {
    this.sourceClass = sourceClass;
    this.configuration = configuration;
  }

  @Override
  public List<PropertyInfo> getDestinationProperties() {
    return destinationProperties;
  }

  @Override
  public List<Tokens> getDestinationPropertyTokens() {
    return destinationPropertyTokens;
  }

  @Override
  public Tokens getSourceClassTokens() {
    if (sourceClassTokens == null) {
      String className = configuration.getSourceNameTransformer().transform(
          sourceClass.getSimpleName(), NameableType.CLASS);
      sourceClassTokens = Tokens.of(configuration.getSourceNameTokenizer().tokenize(className,
          NameableType.CLASS));
    }

    return sourceClassTokens;
  }

  @Override
  public List<PropertyInfo> getSourceProperties() {
    return sourceProperties;
  }

  @Override
  public List<Tokens> getSourcePropertyTokens() {
    return sourcePropertyTokens;
  }

  @Override
  public List<Tokens> getSourcePropertyTypeTokens() {
    if (sourcePropertyTypeTokens == null) {
      sourcePropertyTypeTokens = new Stack<Tokens>();
      for (PropertyInfo sourceProperty : sourceProperties)
        pushSourcePropertyType(sourceProperty);
    }

    return sourcePropertyTypeTokens;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(PropertyNameInfoImpl.class).add("sourceProperties", sourceProperties)
        .add("destinationProperties", destinationProperties)
        .toString();
  }

  void clearSource() {
    sourceProperties.clear();
    sourcePropertyTokens.clear();
    if (sourcePropertyTypeTokens != null)
      sourcePropertyTypeTokens.clear();
  }

  void popDestination() {
    destinationProperties.pop();
    destinationPropertyTokens.pop();
  }

  void popSource() {
    sourceProperties.pop();
    sourcePropertyTokens.pop();
    if (sourcePropertyTypeTokens != null)
      sourcePropertyTypeTokens.pop();
  }

  void pushDestination(String destinationName, Mutator destinationProperty) {
    NameableType nameableType = NameableType.forPropertyType(destinationProperty.getPropertyType());
    String[] tokens = configuration.getDestinationNameTokenizer().tokenize(
        destinationName, nameableType);
    destinationPropertyTokens.push(Tokens.of(tokens));
    destinationProperties.push(destinationProperty);
  }

  void pushSource(String sourceName, Accessor sourceProperty) {
    NameableType nameableType = NameableType.forPropertyType(sourceProperty.getPropertyType());
    String[] tokens = configuration.getSourceNameTokenizer().tokenize(sourceName,
        nameableType);
    sourcePropertyTokens.push(Tokens.of(tokens));
    sourceProperties.push(sourceProperty);
    pushSourcePropertyType(sourceProperty);
  }

  private void pushSourcePropertyType(PropertyInfo sourceProperty) {
    if (sourcePropertyTypeTokens == null)
      return;
    String typeName = configuration.getSourceNameTransformer().transform(
        sourceProperty.getType().getSimpleName(), NameableType.CLASS);
    String[] tokens = configuration.getSourceNameTokenizer().tokenize(typeName,
        NameableType.CLASS);
    sourcePropertyTypeTokens.add(Tokens.of(tokens));
  }
}