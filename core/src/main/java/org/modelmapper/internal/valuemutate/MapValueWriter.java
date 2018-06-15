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
package org.modelmapper.internal.valuemutate;

import java.util.Collection;
import java.util.Map;
import org.modelmapper.spi.ValueWriter;

/**
 * Handles writing value to java.util.Map instances.
 * 
 * @author Chun Han Hsiao
 */
public class MapValueWriter implements ValueWriter<Map<String, Object>> {
  @Override
  public void setValue(Map<String, Object> destination, Object value, String memberName) {
    destination.put(memberName, value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Member<Map<String, Object>> getMember(Class<Map<String, Object>> destinationType, final String memberName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<String> memberNames(Class<Map<String, Object>> destinationType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isResolveMembersSupport() {
    return false;
  }
}
