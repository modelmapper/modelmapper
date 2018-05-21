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
import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.PropertyInfo;

/**
 * @author Jonathan Halterman
 */
abstract class MappingImpl implements Mapping, Comparable<MappingImpl> {
  protected final List<PropertyInfo> destinationMutators;
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
        + (mergedMutators == null ? 0 : mergedMutators.size()));
    destinationMutators.addAll(mergedMutators);
    destinationMutators.addAll(copy.destinationMutators);
    path = Strings.join(destinationMutators);
    skipType = copy.skipType;
    condition = copy.condition;
    provider = copy.provider;
    converter = copy.converter;
    explicit = copy.explicit;
  }

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

  public Condition<?, ?> getCondition() {
    return condition;
  }

  public Converter<?, ?> getConverter() {
    return converter;
  }

  public List<? extends PropertyInfo> getDestinationProperties() {
    return destinationMutators;
  }

  public PropertyInfo getLastDestinationProperty() {
    return destinationMutators.get(destinationMutators.size() - 1);
  }

  public Provider<?> getProvider() {
    return provider;
  }

  @Override
  public int hashCode() {
    return path.hashCode();
  }

  public boolean isSkipped() {
    return skipType != 0;
  };

  /**
   * Creates a merged mapping whose source path begins with the {@code mergedAccessors} and
   * destination path begins with the {@code mergedMutators}.
   */
  abstract MappingImpl createMergedCopy(List<? extends PropertyInfo> mergedAccessors,
      List<? extends PropertyInfo> mergedMutators);

  /**
   * Returns a string key representing the path of the destination property hierarchy.
   */
  String getPath() {
    return path;
  }

  /**
   * Returns whether the mapping is explicit or implicit.
   */
  boolean isExplicit() {
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
