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

import java.util.Map;

import org.modelmapper.config.Configuration;
import org.modelmapper.spi.NameableType;

/**
 * A TypeInfo implementation that lazily reflects members.
 * 
 * @author Jonathan Halterman
 */
class TypeInfoImpl<T> implements TypeInfo<T> {
  /** Source object from which memberNames are read. */
  private final T source;
  private final Class<T> type;
  private final InheritingConfiguration configuration;
  private volatile Map<String, Accessor> accessors;
  private volatile Map<String, Mutator> mutators;

  TypeInfoImpl(T source, Class<T> sourceType, InheritingConfiguration configuration) {
    this.source = source;
    this.type = sourceType;
    this.configuration = configuration;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (!(obj instanceof TypeInfo))
      return false;
    TypeInfo<?> typeInfo = (TypeInfo<?>) obj;
    return type.equals(typeInfo.getType()) && configuration.equals(typeInfo.getConfiguration());
  }

  /**
   * Lazily initializes and gets accessors.
   */
  public Map<String, Accessor> getAccessors() {
    if (accessors == null)
      synchronized (this) {
        if (accessors == null)
          accessors = PropertyInfoSetResolver.resolveAccessors(source, type, configuration);
      }

    return accessors;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  /**
   * Lazily initializes and gets mutators.
   */
  public Map<String, Mutator> getMutators() {
    if (mutators == null)
      synchronized (this) {
        if (mutators == null)
          mutators = PropertyInfoSetResolver.resolveMutators(type, configuration);
      }

    return mutators;
  }

  public Class<T> getType() {
    return type;
  }

  @Override
  public int hashCode() {
    return type.hashCode() * 31 + configuration.hashCode();
  }

  @Override
  public String toString() {
    return type.toString();
  }

  Mutator mutatorForAccessorMethod(String accessorMethodName) {
    return getMutators().get(
        configuration.getSourceNameTransformer().transform(accessorMethodName, NameableType.METHOD));
  }
}
