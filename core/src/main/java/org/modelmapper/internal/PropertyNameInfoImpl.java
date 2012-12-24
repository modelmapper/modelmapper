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
import org.modelmapper.spi.MatchingStrategy.PropertyNameInfo;
import org.modelmapper.spi.NameableType;
import org.modelmapper.spi.PropertyInfo;

/**
 * PropertyNameInfo implementation that tracks source and destination properties.
 * 
 * @author Jonathan Halterman
 */
class PropertyNameInfoImpl implements PropertyNameInfo {
  private final Class<?> sourceClass;
  private final Configuration configuration;
  private String[] sourceClassTokens;
  private Stack<String[]> sourcePropertyTypeTokens;
  private final Stack<String[]> sourcePropertyTokens = new Stack<String[]>();
  private final Stack<String[]> destinationPropertyTokens = new Stack<String[]>();
  private final Stack<PropertyInfo> sourceProperties = new Stack<PropertyInfo>();
  private final Stack<PropertyInfo> destinationProperties = new Stack<PropertyInfo>();

  PropertyNameInfoImpl(Class<?> sourceClass, Configuration configuration) {
    this.sourceClass = sourceClass;
    this.configuration = configuration;
  }

  public List<PropertyInfo> getDestinationProperties() {
    return destinationProperties;
  }

  public List<String[]> getDestinationPropertyTokens() {
    return destinationPropertyTokens;
  }

  public String[] getSourceClassTokens() {
    if (sourceClassTokens == null) {
      String className = configuration.getSourceNameTransformer().transform(
          sourceClass.getSimpleName(), NameableType.CLASS);
      sourceClassTokens = configuration.getSourceNameTokenizer().tokenize(className,
          NameableType.CLASS);
    }

    return sourceClassTokens;
  }

  public List<PropertyInfo> getSourceProperties() {
    return (List<PropertyInfo>) sourceProperties;
  }

  public List<String[]> getSourcePropertyTokens() {
    return sourcePropertyTokens;
  }

  public List<String[]> getSourcePropertyTypeTokens() {
    if (sourcePropertyTypeTokens == null) {
      sourcePropertyTypeTokens = new Stack<String[]>();
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
    destinationPropertyTokens.push(configuration.getDestinationNameTokenizer().tokenize(
        destinationName, nameableType));
    destinationProperties.push(destinationProperty);
  }

  void pushSource(String sourceName, Accessor sourceProperty) {
    NameableType nameableType = NameableType.forPropertyType(sourceProperty.getPropertyType());
    sourcePropertyTokens.push(configuration.getSourceNameTokenizer().tokenize(sourceName,
        nameableType));
    sourceProperties.push(sourceProperty);
    pushSourcePropertyType(sourceProperty);
  }

  void pushSourcePropertyType(PropertyInfo sourceProperty) {
    if (sourcePropertyTypeTokens == null)
      return;
    String typeName = configuration.getSourceNameTransformer().transform(
        sourceProperty.getType().getSimpleName(), NameableType.CLASS);
    sourcePropertyTypeTokens.add(configuration.getSourceNameTokenizer().tokenize(typeName,
        NameableType.CLASS));
  }
}