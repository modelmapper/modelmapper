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

/**
 * @author Jonathan Halterman
 */
class TypePair<S, D> {
  private final Class<S> sourceType;
  private final Class<D> destinationType;
  private final String name;
  private final int hashCode;

  private TypePair(Class<S> sourceType, Class<D> destinationType, String name) {
    this.sourceType = sourceType;
    this.destinationType = destinationType;
    this.name = name;
    hashCode = computeHashCode();
  }

  static <T1, T2> TypePair<T1, T2> of(Class<T1> sourceType, Class<T2> destinationType, String name) {
    return new TypePair<T1, T2>(sourceType, destinationType, name);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this)
      return true;
    if (!(other instanceof TypePair<?, ?>))
      return false;
    TypePair<?, ?> otherPair = (TypePair<?, ?>) other;
    if (name == null) {
      if (otherPair.name != null)
        return false;
    } else if (!name.equals(otherPair.name))
      return false;
    return sourceType.equals(otherPair.sourceType)
        && destinationType.equals(otherPair.destinationType);
  }

  @Override
  public final int hashCode() {
    return hashCode;
  }

  @Override
  public String toString() {
    String str = sourceType.getName() + " to " + destinationType.getName();
    if (name != null)
      str += " as " + name;
    return str;
  }

  Class<D> getDestinationType() {
    return destinationType;
  }

  Class<S> getSourceType() {
    return sourceType;
  }

  private int computeHashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + sourceType.hashCode();
    result = prime * result + destinationType.hashCode();
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }
}