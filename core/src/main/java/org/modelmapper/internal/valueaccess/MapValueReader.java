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
package org.modelmapper.internal.valueaccess;

import java.util.Collection;
import java.util.Map;

import org.modelmapper.spi.ValueReader;

/**
 * Handles reading from java.util.Map instances.
 * 
 * @author Jonathan Halterman
 */
public class MapValueReader implements ValueReader<Map<String, Object>> {
  public Object get(Map<String, Object> source, String memberName) {
    if (!source.containsKey(memberName))
      throw new IllegalArgumentException();
    return source.get(memberName);
  }

  public Collection<String> memberNames(Map<String, Object> source) {
    return source.keySet();
  }
}
