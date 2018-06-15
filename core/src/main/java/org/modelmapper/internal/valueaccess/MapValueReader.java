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
  @Override
  public Object get(Map<String, Object> source, String memberName) {
    return source.get(memberName);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Member<Map<String, Object>> getMember(Map<String, Object> source, String memberName) {
    final Object value = get(source, memberName);
    return new Member<Map<String, Object>>(Map.class) {
      @Override
      public Map<String, Object> getOrigin() {
        if (value instanceof Map)
          return (Map<String, Object>) value;
        return null;
      }

      @Override
      public Object get(Map<String, Object> source, String memberName) {
        return MapValueReader.this.get(source, memberName);
      }
    };
  }

  @Override
  public Collection<String> memberNames(Map<String, Object> source) {
    return source.keySet();
  }

}
