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

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.Condition;
import org.modelmapper.Converter;
import org.modelmapper.Provider;
import org.modelmapper.internal.ExplicitMappingBuilder.MappingOptions;
import org.modelmapper.internal.util.Strings;
import org.modelmapper.spi.PropertyInfo;

/**
 * @author Jonathan Halterman
 */
abstract class MappingImpl implements InternalMapping, Comparable<MappingImpl> {
  private final List<PropertyInfo> destinationMutators;
  private final boolean explicit;
  private final String path;
  private int skipType;
  private Condition<?, ?> condition;
  protected Provider<?> provider;
  protected Converter<?, ?> converter;

  /**
   * Creates an implicit mapping.
   */
  MappingImpl(List<? extends PropertyInfo> destinationMutators) {
    this.destinationMutators = new ArrayList<PropertyInfo>(destinationMutators);
    path = Strings.join(destinationMutators);
    this.explicit = false;
  }

  /**
   * Creates an explicit mapping.
   */
  MappingImpl(List<? extends PropertyInfo> destinationMutators, MappingOptions options) {
    this.destinationMutators = new ArrayList<PropertyInfo>(destinationMutators);
    path = Strings.join(destinationMutators);
    this.skipType = options.skipType;
    this.condition = options.condition;
    this.provider = options.provider;
    this.converter = options.converter;
    explicit = true;
  }

  /**
   * Creates a merged mapping.
   */
  MappingImpl(MappingImpl copy, List<? extends PropertyInfo> mergedMutators) {
    destinationMutators = new ArrayList<PropertyInfo>(copy.destinationMutators.size()
        + mergedMutators.size());
    destinationMutators.addAll(mergedMutators);
    destinationMutators.addAll(copy.destinationMutators);
    path = Strings.join(destinationMutators);
    skipType = copy.skipType;
    condition = copy.condition;
    provider = copy.provider;
    converter = copy.converter;
    explicit = copy.explicit;
  }

  @Override
  public int compareTo(MappingImpl mapping) {
    return path.compareToIgnoreCase(mapping.path);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || !(obj instanceof MappingImpl))
      return false;
    MappingImpl other = (MappingImpl) obj;
    return path.equals(other.path);
  }

  @Override
  public Condition<?, ?> getCondition() {
    return condition;
  }

  @Override
  public Converter<?, ?> getConverter() {
    return converter;
  }

  @Override
  public List<? extends PropertyInfo> getDestinationProperties() {
    return destinationMutators;
  }

  @Override
  public PropertyInfo getLastDestinationProperty() {
    return destinationMutators.get(destinationMutators.size() - 1);
  }

  @Override
  public Provider<?> getProvider() {
    return provider;
  }

  @Override
  public int hashCode() {
    return path.hashCode();
  }

  @Override
  public boolean isSkipped() {
    return skipType != 0;
  }

  @Override
  public String getPath() {
    return path;
  }

  @Override
  public boolean isExplicit() {
    return explicit;
  }

  MappingOptions getOptions() {
    MappingOptions options = new MappingOptions();
    options.skipType = skipType;
    options.condition = condition;
    options.converter = converter;
    options.provider = provider;
    return options;
  }
}
