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
package org.modelmapper.internal.valueaccess;

import net.jodah.typetools.TypeResolver;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.modelmapper.internal.util.Assert;
import org.modelmapper.internal.util.CopyOnWriteLinkedHashMap;
import org.modelmapper.spi.ValueReader;

/**
 * Store for ValueReaders and ValueWriters.
 * 
 * @author Jonathan Halterman
 */
public final class ValueAccessStore {
  private final TypeResolvingList<ValueReader<?>> valueReaders = new TypeResolvingList<ValueReader<?>>(
      ValueReader.class);

  /**
   * CopyOnWriteArrayList that resolves ValueReader/ValueWriter and indexes values on write based on
   * their source/destination types.
   */
  private static class TypeResolvingList<T> extends CopyOnWriteArrayList<T> {
    private static final long serialVersionUID = 1252949487997828943L;
    private final Map<T, Class<?>> valueAccessors = new CopyOnWriteLinkedHashMap<T, Class<?>>();
    private final Class<? super T> valueAccessorType;

    TypeResolvingList(Class<? super T> valueAccessorType) {
      this.valueAccessorType = valueAccessorType;
    }

    @Override
    public void add(int index, T element) {
      putValueAccessor(element);
      super.add(index, element);
    }

    @Override
    public boolean add(T element) {
      putValueAccessor(element);
      return super.add(element);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
      boolean changed = false;
      for (T e : c) {
        putValueAccessor(e);
        changed = super.add(e);
      }

      return changed;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
      valueAccessors.clear();
      super.clear();
    }

    @Override
    public T remove(int index) {
      T result = super.remove(index);
      valueAccessors.remove(result);
      return result;
    }

    @Override
    public boolean remove(Object o) {
      valueAccessors.remove(o);
      return super.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
      boolean result = super.removeAll(c);
      for (Object e : c)
        valueAccessors.remove(e);
      return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
      throw new UnsupportedOperationException();
    }

    @Override
    public T set(int index, T element) {
      putValueAccessor(element);
      return super.set(index, element);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
      throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    private void putValueAccessor(T valueAccessor) {
      Assert.notNull(valueAccessor, "element");
      Class<?> typeArgument = TypeResolver.resolveRawArgument(valueAccessorType, valueAccessor.getClass());
      Assert.notNull(typeArgument, "Must declare source type argument <T> for the "
          + valueAccessorType.getSimpleName());
      valueAccessors.put(valueAccessor, typeArgument);
    }
  }

  public ValueAccessStore() {
    // Register defaults
    valueReaders.add(new MapValueReader());
  }

  /**
   * Returns the first ValueReader that supports reading values from the {@code type}, else
   * {@code null} if none is found.
   */
  @SuppressWarnings("unchecked")
  public <T> ValueReader<T> getFirstSupportedReader(Class<T> type) {
    for (Map.Entry<ValueReader<?>, Class<?>> entry : valueReaders.valueAccessors.entrySet())
      if (entry.getValue().isAssignableFrom(type))
        return (ValueReader<T>) entry.getKey();
    return null;
  }

  public List<ValueReader<?>> getValueReaders() {
    return valueReaders;
  }
}
