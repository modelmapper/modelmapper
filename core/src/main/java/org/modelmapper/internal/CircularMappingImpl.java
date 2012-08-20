/*
 * Copyright 2012 the original author or authors.
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

import org.modelmapper.internal.MappingBuilderImpl.MappingOptions;
import org.modelmapper.internal.util.Strings;
import org.modelmapper.spi.CircularMapping;
import org.modelmapper.spi.PropertyInfo;

/**
 * @author Jonathan Halterman
 */
class CircularMappingImpl extends PropertyMappingImpl implements CircularMapping {
  /**
   * Creates an implicit CircularMapping.
   */
  CircularMappingImpl(List<? extends PropertyInfo> sourceAccessors,
      List<? extends PropertyInfo> destinationMutators) {
    super(sourceAccessors, destinationMutators);
  }

  /**
   * Creates an explicit CircularMapping.
   */
  CircularMappingImpl(List<? extends PropertyInfo> sourceAccessors,
      List<Mutator> destinationMutators, MappingOptions options) {
    super(sourceAccessors, destinationMutators, options);
  }

  /**
   * Creates a merged CircularMapping.
   */
  CircularMappingImpl(CircularMappingImpl mapping, List<? extends PropertyInfo> mergedAccessors,
      List<? extends PropertyInfo> mergedMutators) {
    super(mapping, mergedAccessors, mergedMutators);
  }

  @Override
  public String toString() {
    return String.format("CircularMapping[%s -> %s]", Strings.joinWithFirstType(sourceAccessors),
        Strings.joinWithFirstType(destinationMutators));
  }

  @Override
  MappingImpl createMergedCopy(List<? extends PropertyInfo> mergedAccessors,
      List<? extends PropertyInfo> mergedMutators) {
    return new CircularMappingImpl(this, mergedAccessors, mergedMutators);
  }
}
