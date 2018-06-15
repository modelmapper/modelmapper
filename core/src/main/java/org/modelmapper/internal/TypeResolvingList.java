/*
 * Copyright 2018 the original author or authors.
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

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.jodah.typetools.TypeResolver;
import org.modelmapper.internal.util.Assert;
import org.modelmapper.internal.util.CopyOnWriteLinkedHashMap;

/**
 * CopyOnWriteArrayList that resolves based on given types.
 *
 * @author Chun Han Hsiao
 */
public class TypeResolvingList<T> extends AbstractList<T> {
  private final Map<T, Class<?>> elements = new CopyOnWriteLinkedHashMap<T, Class<?>>();
  private final Class<? super T> valueAccessorType;

  public TypeResolvingList(Class<? super T> valueAccessorType) {
    this.valueAccessorType = valueAccessorType;
  }

  @Override
  public void add(int index, T element) {
    addElement(element);
  }

  @Override
  public int size() {
    return elements.size();
  }

  @Override
  public boolean add(T element) {
    return addElement(element);
  }

  @Override
  public T get(int index) {
    Iterator<T> iterator = elements.keySet().iterator();
    for (int i = 0; i < index; i++) {
      iterator.next();
    }
    return iterator.next();
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    boolean changed = false;
    for (T e : c) {
      changed = addElement(e) || changed;
    }

    return changed;
  }

  @Override
  public boolean addAll(int index, Collection<? extends T> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    elements.clear();
  }

  @Override
  public T remove(int index) {
    T element = get(index);
    elements.remove(element);
    return element;
  }

  @Override
  public boolean remove(Object o) {
    elements.remove(o);
    return true;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    for (Object e : c) {
      @SuppressWarnings("unchecked")
      T t = (T) e;
      elements.remove(t);
    }
    return true;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public T set(int index, T element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<T> subList(int fromIndex, int toIndex) {
    throw new UnsupportedOperationException();
  }

  private boolean addElement(T element) {
    Assert.notNull(element, "element");
    Class<?> typeArgument = TypeResolver.resolveRawArgument(valueAccessorType, element.getClass());
    Assert.notNull(typeArgument, "Must declare source type argument <T> for the "
        + valueAccessorType.getSimpleName());
    elements.put(element, typeArgument);
    return true;
  }

  public T first(Class<?> type) {
    for (Map.Entry<T, Class<?>> entry : elements.entrySet())
      if (entry.getValue().isAssignableFrom(type))
        return entry.getKey();
    return null;
  }
}
