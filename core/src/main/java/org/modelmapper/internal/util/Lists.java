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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jonathan Halterman
 */
public final class Lists {
  private Lists() {
  }

  public static <T> List<T> from(Iterator<T> iterator) {
    List<T> list = new ArrayList<T>();
    while (iterator.hasNext())
      list.add(iterator.next());
    return list;
  }

  public static <T> List<T> from(Set<Map.Entry<T, ?>> entries) {
    List<T> list = new ArrayList<T>();
    for (Map.Entry<T, ?> entry : entries)
      list.add(entry.getKey());
    return list;
  }
}
