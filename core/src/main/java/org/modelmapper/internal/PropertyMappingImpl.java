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

import org.modelmapper.Converter;
import org.modelmapper.Provider;
import org.modelmapper.internal.ExplicitMappingBuilder.MappingOptions;
import org.modelmapper.internal.util.Strings;
import org.modelmapper.spi.PropertyInfo;
import org.modelmapper.spi.PropertyMapping;

/**
 * @author Jonathan Halterman
 */
class PropertyMappingImpl extends MappingImpl implements PropertyMapping {
  protected final List<PropertyInfo> sourceAccessors;
  protected boolean cyclic;

  /**
   * Creates an implicit PropertyMapping.
   */
  PropertyMappingImpl(List<? extends PropertyInfo> sourceAccessors,
      List<? extends PropertyInfo> destinationMutators, boolean cyclic) {
    super(destinationMutators);
    this.sourceAccessors = new ArrayList<PropertyInfo>(sourceAccessors);
    this.cyclic = cyclic;
  }

  /**
   * Creates an implicit merged PropertyMapping to a converter.
   */
  PropertyMappingImpl(List<? extends PropertyInfo> sourceAccessors,
      List<? extends PropertyInfo> destinationMutators, Provider<?> provider,
      Converter<?, ?> converter) {
    super(destinationMutators);
    this.provider = provider;
    this.converter = converter;
    this.sourceAccessors = new ArrayList<PropertyInfo>(sourceAccessors);
  }

  /**
   * Creates an explicit PropertyMapping.
   */
  PropertyMappingImpl(List<? extends PropertyInfo> sourceAccessors,
      List<Mutator> destinationMutators, MappingOptions options) {
    super(destinationMutators, options);
    this.sourceAccessors = new ArrayList<PropertyInfo>(sourceAccessors);
  }

  /**
   * Creates a merged PropertyMapping.
   */
  PropertyMappingImpl(PropertyMappingImpl mapping, List<? extends PropertyInfo> mergedAccessors,
      List<? extends PropertyInfo> mergedMutators) {
    super(mapping, mergedMutators);
    sourceAccessors = new ArrayList<PropertyInfo>(mapping.sourceAccessors.size()
        + (mergedAccessors == null ? 0 : mergedAccessors.size()));
    sourceAccessors.addAll(mergedAccessors);
    sourceAccessors.addAll(mapping.sourceAccessors);
    cyclic = mapping.cyclic;
  }

  public PropertyInfo getLastSourceProperty() {
    return sourceAccessors == null || sourceAccessors.isEmpty() ? null
        : sourceAccessors.get(sourceAccessors.size() - 1);
  }

  public List<? extends PropertyInfo> getSourceProperties() {
    return sourceAccessors;
  }

  @Override
  public String toString() {
    return String.format("PropertyMapping[%s -> %s]", Strings.joinWithFirstType(sourceAccessors),
        Strings.joinWithFirstType(destinationMutators));
  }

  @Override
  MappingImpl createMergedCopy(List<? extends PropertyInfo> mergedAccessors,
      List<? extends PropertyInfo> mergedMutators) {
    return new PropertyMappingImpl(this, mergedAccessors, mergedMutators);
  }
}
