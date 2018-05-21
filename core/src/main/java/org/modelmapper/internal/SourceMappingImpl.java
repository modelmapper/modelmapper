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

import org.modelmapper.internal.ExplicitMappingBuilder.MappingOptions;
import org.modelmapper.internal.util.Strings;
import org.modelmapper.spi.PropertyInfo;
import org.modelmapper.spi.SourceMapping;

/**
 * @author Jonathan Halterman
 */
class SourceMappingImpl extends MappingImpl implements SourceMapping {
  private final Class<?> sourceType;

  /**
   * Creates an explicit SourceMappingImpl.
   */
  SourceMappingImpl(Class<?> sourceType, List<Mutator> destinationMutators, MappingOptions options) {
    super(destinationMutators, options);
    this.sourceType = sourceType;
  }

  public Class<?> getSourceType() {
    return sourceType;
  }

  @Override
  public String toString() {
    return String.format("SourceMapping[%s -> %s]", sourceType,
        Strings.joinWithFirstType(destinationMutators));
  }

  @Override
  MappingImpl createMergedCopy(List<? extends PropertyInfo> mergedAccessors,
      List<? extends PropertyInfo> mergedMutators) {
    List<PropertyInfo> mutators = new ArrayList<PropertyInfo>();
    mutators.addAll(mergedMutators);
    mutators.addAll(destinationMutators);
    return new PropertyMappingImpl(mergedAccessors, mutators, getOptions());
  }
}
