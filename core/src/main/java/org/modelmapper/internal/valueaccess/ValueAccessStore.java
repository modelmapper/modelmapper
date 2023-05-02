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

import java.util.List;
import org.modelmapper.internal.TypeResolvingList;
import org.modelmapper.spi.ValueReader;

/**
 * Store for ValueReaders.
 * 
 * @author Jonathan Halterman
 */
public final class ValueAccessStore {
  private final TypeResolvingList<ValueReader<?>> valueReaders = new TypeResolvingList<ValueReader<?>>(
      ValueReader.class);

  public ValueAccessStore() {
    // Register defaults
    valueReaders.add(new MapValueReader());
    valueReaders.add(new RecordValueReader());
  }

  /**
   * Returns the first ValueReader that supports reading values from the {@code type}, else
   * {@code null} if none is found.
   */
  @SuppressWarnings("unchecked")
  public <T> ValueReader<T> getFirstSupportedReader(Class<T> type) {
    return (ValueReader<T>) valueReaders.first(type);
  }

  public List<ValueReader<?>> getValueReaders() {
    return valueReaders;
  }
}
