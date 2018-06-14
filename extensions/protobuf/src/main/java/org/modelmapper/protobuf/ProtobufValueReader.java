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
package org.modelmapper.protobuf;

import java.util.Collection;
import org.modelmapper.spi.ValueReader;

/**
 * Record ValueReader implementation.
 *
 * @author Edward Hsiao
 */
public class ProtobufValueReader implements ValueReader<Object> {
  @Override
  public Object get(Object source, String memberName) {
    return null;
  }

  @Override
  public Member<Object> getMember(Object source, String memberName) {
    return null;
  }

  @Override
  public Collection<String> memberNames(Object source) {
    return null;
  }
}
