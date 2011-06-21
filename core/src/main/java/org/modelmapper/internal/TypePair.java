/**
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

/**
 * @author Jonathan Halterman
 */
class TypePair<S, D> {
  private final Class<S> sourceType;
  private final Class<D> destinationType;
  private final int hashCode;

  private TypePair(Class<S> sourceType, Class<D> destinationType) {
    this.sourceType = sourceType;
    this.destinationType = destinationType;
    hashCode = computeHashCode();
  }

  static <T1, T2> TypePair<T1, T2> of(Class<T1> sourceType, Class<T2> destinationType) {
    return new TypePair<T1, T2>(sourceType, destinationType);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this)
      return true;
    if (!(other instanceof TypePair<?, ?>))
      return false;
    TypePair<?, ?> otherKey = (TypePair<?, ?>) other;
    return sourceType.equals(otherKey.sourceType)
        && destinationType.equals(otherKey.destinationType);
  }

  @Override
  public final int hashCode() {
    return hashCode;
  }

  @Override
  public String toString() {
    return sourceType.getName() + " to " + destinationType.getName();
  }

  private int computeHashCode() {
    return sourceType.hashCode() * 31 + destinationType.hashCode();
  }

  Class<D> getDestinationType() {
    return destinationType;
  }

  Class<S> getSourceType() {
    return sourceType;
  }
}