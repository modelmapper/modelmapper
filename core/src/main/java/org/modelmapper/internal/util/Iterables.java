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
package org.modelmapper.internal.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Jonathan Halterman
 */
public final class Iterables {
  private Iterables() {
  }

  /**
   * Returns whether a given type is a iterable or not.
   *
   * @param type the type
   * @return {@code true} if the type is an iterable, otherwise, return {@code false} .
   */
  public static boolean isIterable(Class<?> type) {
    return type.isArray() || Collection.class.isAssignableFrom(type);
  }

  /**
   * Gets the length of an iterable.
   *
   * @param iterable the iterable
   * @return the length of the iterable
   */
  @SuppressWarnings("unchecked")
  public static int getLength(Object iterable) {
    Assert.state(isIterable(iterable.getClass()));
    return iterable.getClass().isArray() ? Array.getLength(iterable) : ((Collection<Object>) iterable).size();
  }

  /**
   * Creates a iterator from given iterable.
   *
   * @param iterable the iterable
   * @return a iterator
   */
  @SuppressWarnings("unchecked")
  public static Iterator<Object> iterator(Object iterable) {
    Assert.state(isIterable(iterable.getClass()));
    return iterable.getClass().isArray() ? new ArrayIterator(iterable)
        : ((Iterable<Object>) iterable).iterator();
  }

  /**
   * Gets the element from an iterable with given index.
   *
   * @param iterable the iterable
   * @param index the index
   * @return null if the iterable doesn't have the element at index, otherwise, return the element
   */
  @SuppressWarnings("unchecked")
  public static Object getElement(Object iterable, int index) {
    if (iterable.getClass().isArray())
      return getElementFromArrary(iterable, index);
    if (iterable instanceof Collection)
      return getElementFromCollection((Collection<Object>) iterable, index);

    return null;
  }

  /**
   * Gets the element from an array with given index.
   *
   * @param array the array
   * @param index the index
   * @return null if the array doesn't have the element at index, otherwise, return the element
   */
  public static Object getElementFromArrary(Object array, int index) {
    try {
      return Array.get(array, index);
    } catch (ArrayIndexOutOfBoundsException e) {
      return null;
    }
  }

  /**
   * Gets the element from a collection with given index.
   *
   * @param collection the collection
   * @param index the index
   * @return null if the collection doesn't have the element at index, otherwise, return the element
   */
  public static Object getElementFromCollection(Collection<Object> collection, int index) {
    if (collection.size() < index + 1)
      return null;

    if (collection instanceof List)
      return ((List<Object>) collection).get(index);

    Iterator<Object> iterator = collection.iterator();
    for (int i = 0; i < index; i++) {
      iterator.next();
    }
    return iterator.next();
  }

  /**
   * Creates sub iterable started from {@code fromIndex}.
   *
   * @param iterable the iterable
   * @param fromIndex the index start from
   * @return a iterable
   */
  public static Iterable<Object> subIterable(Object iterable, int fromIndex) {
    if (iterable == null || getLength(iterable) < fromIndex)
      return Collections.emptyList();

    final Iterator<Object> iterator = iterator(iterable);
    for (int i = 0; i < fromIndex; i++)
      iterator.next();
    return new Iterable<Object>() {
      @Override
      public Iterator<Object> iterator() {
        return iterator;
      }
    };
  }
}
