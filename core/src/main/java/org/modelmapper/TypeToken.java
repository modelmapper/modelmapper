/*
 * Copyright 2013 the original author or authors.
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
package org.modelmapper;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import org.modelmapper.internal.util.Assert;
import org.modelmapper.internal.util.Types;

/**
 * Represents a generic type {@code T}. Subclassing TypeToken allows for type information to be
 * preserved at runtime.
 * 
 * <p>
 * Example: To create a type literal for {@code List<String>}, you can create an empty anonymous
 * inner class:
 * 
 * <p>
 * {@code TypeToken<List<String>> list = new TypeToken<List<String>>();}
 * 
 * @author Jonathan Halterman
 * @param <T> Represented type
 */
public class TypeToken<T> {
  private final Type type;
  private final Class<T> rawType;

  /**
   * Creates a new type token for {@code T}.
   * 
   * @throws IllegalArgumentException if {@code T} is not provided or is a TypeVariable
   */
  @SuppressWarnings("unchecked")
  protected TypeToken() {
    Type superclass = getClass().getGenericSuperclass();
    Assert.isTrue(superclass instanceof ParameterizedType, "%s is not parameterized", superclass);
    type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
    Assert.isTrue(
        !(type instanceof TypeVariable),
        "Cannot construct a TypeToken for a TypeVariable. Try new TypeToken<%s>(getClass()) instead.",
        type);
    rawType = (Class<T>) Types.rawTypeFor(type);
  }

  @SuppressWarnings("unchecked")
  private TypeToken(Type type) {
    this.type = type;
    rawType = (Class<T>) Types.rawTypeFor(type);
  }

  /**
   * Returns a TypeLiteral for the {@code type}.
   * 
   * @throws IllegalArgumentException if {@code type} is null or if a raw type cannot be resolved
   */
  public static <T> TypeToken<T> of(Type type) {
    Assert.notNull(type, "type");
    return new TypeToken<T>(type);
  }

  @Override
  public final boolean equals(Object o) {
    return o instanceof TypeToken<?> && type.equals(((TypeToken<?>) o).type);
  }

  /**
   * Returns the raw type for {@code T}.
   * 
   * <ul>
   * <li>If {@code T} is a {@code Class}, {@code T} itself is returned.
   * <li>If {@code T} is a {@link ParameterizedType}, the raw type is returned
   * <li>If {@code T} is a {@link GenericArrayType}, the raw type is returned
   * <li>If {@code T} is a {@link TypeVariable} or {@link WildcardType}, the raw type of the first
   * upper bound is returned
   * </ul>
   */
  public final Class<T> getRawType() {
    return rawType;
  }

  /**
   * Returns the generic type {@code T}.
   */
  public final Type getType() {
    return type;
  }

  @Override
  public final int hashCode() {
    return type.hashCode();
  }

  @Override
  public final String toString() {
    return type.toString();
  }
}