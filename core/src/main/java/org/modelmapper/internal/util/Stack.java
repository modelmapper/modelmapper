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

import java.util.ArrayList;

/**
 * LIFO Stack semantics around an ArrayList for random access support. Indexed access via
 * {@link #get(int)} and iteration via {@link #iterator()} is in reverse order (FIFO). Not
 * threadsafe.
 * 
 * @author Jonathan Halterman
 */
public class Stack<T> extends ArrayList<T> {
  private static final long serialVersionUID = 0L;

  /**
   * Pushes to the top of the stack.
   */
  public void push(T element) {
    add(element);
  }

  /**
   * Pops from the top of the stack.
   */
  public T pop() {
    return remove(size() - 1);
  }

  /**
   * Peeks at the top of the stack.
   */
  public T peek() {
    return get(size() - 1);
  }
}
