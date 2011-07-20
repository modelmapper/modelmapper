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
import java.util.Iterator;

/**
 * @author Jonathan Halterman
 */
public class ArrayIterator implements Iterator<Object> {
  private final Object array;
  private final int length;
  private int index;

  /**
   * @throws IllegalArgumentException if {@code array} is not an array
   */
  public ArrayIterator(Object array) {
    Assert.isTrue(array.getClass().isArray());
    this.array = array;
    length = Array.getLength(array);
  }

  public boolean hasNext() {
    return index < length;
  }

  public Object next() {
    return Array.get(array, index++);
  }

  public void remove() {
  }
}
