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

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Jonathan Halterman
 */
public class ToStringBuilder implements Serializable {

  private static final long serialVersionUID = 9098003517961566960L;

  private final Map<String, Object> properties = new LinkedHashMap<String, Object>();
  private final String name;

  public ToStringBuilder(Class<?> type) {
    this.name = type.getSimpleName();
  }

  public ToStringBuilder add(String name, Object value) {
    value = value.getClass().isArray() ? new ArrayWrapper(value) : value;

    if (properties.put(name, value) != null)
      throw new RuntimeException("Duplicate property: " + name);
    return this;
  }

  @Override
  public String toString() {
    return name + properties.toString().replace('{', '[').replace('}', ']');
  }

  static class ArrayWrapper {
    private final Object[] array;

    ArrayWrapper(Object array) {
      this.array = (Object[]) array;
    }

    @Override
    public String toString() {
      return Arrays.toString(array);
    }
  }
}
