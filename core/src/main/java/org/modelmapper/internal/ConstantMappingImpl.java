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

import org.modelmapper.internal.ExplicitMappingBuilder.MappingOptions;
import org.modelmapper.internal.util.Strings;
import org.modelmapper.internal.util.Types;
import org.modelmapper.spi.ConstantMapping;
import org.modelmapper.spi.PropertyInfo;

/**
 * @author Jonathan Halterman
 */
class ConstantMappingImpl extends MappingImpl implements ConstantMapping {
  private final Object constant;

  /**
   * Creates a merged ConstantMapping.
   */
  private ConstantMappingImpl(ConstantMappingImpl mapping, List<? extends PropertyInfo> mergedMutators) {
    super(mapping, mergedMutators);
    this.constant = mapping.constant;
  }

  /**
   * Creates an explicit ConstantMapping.
   */
  ConstantMappingImpl(Object constant, List<Mutator> destinationMutators, MappingOptions options) {
    super(destinationMutators, options);
    this.constant = constant;
  }

  @Override
  public Object getConstant() {
    return constant;
  }

  @Override
  public String toString() {
    return String.format("ConstantMapping[%s -> %s]", constant,
        Strings.joinWithFirstType(getDestinationProperties()));
  }

  @Override
  public InternalMapping createMergedCopy(List<? extends PropertyInfo> mergedAccessors,
      List<? extends PropertyInfo> mergedMutators) {
    return new ConstantMappingImpl(this, mergedMutators);
  }

  @Override
  public Class<?> getSourceType() {
    return constant == null ? Object.class : Types.deProxiedClass(constant);
  }
}
