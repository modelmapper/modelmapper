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
package org.modelmapper.internal.valuemutate;

import java.util.List;
import org.modelmapper.internal.TypeResolvingList;
import org.modelmapper.spi.ValueWriter;

/**
 * Store for ValueReaders.
 * 
 * @author Jonathan Halterman
 */
public final class ValueMutateStore {
  private final TypeResolvingList<ValueWriter<?>> valueWriters = new TypeResolvingList<ValueWriter<?>>(
      ValueWriter.class);

  public ValueMutateStore() {
    // Register defaults
    valueWriters.add(new MapValueWriter());
  }

  /**
   * Returns the first ValueReader that supports reading values from the {@code type}, else
   * {@code null} if none is found.
   */
  @SuppressWarnings("unchecked")
  public <T> ValueWriter<T> getFirstSupportedWriter(Class<T> type) {
    return (ValueWriter<T>) valueWriters.first(type);
  }

  public List<ValueWriter<?>> getValueWriters() {
    return valueWriters;
  }
}
