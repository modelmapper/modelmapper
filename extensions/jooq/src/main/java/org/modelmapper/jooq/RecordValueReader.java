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
package org.modelmapper.jooq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jooq.Field;
import org.jooq.Record;
import org.modelmapper.spi.ValueReader;

/**
 * Record ValueReader implementation.
 * 
 * @author Jonathan Halterman
 */
public class RecordValueReader implements ValueReader<Record> {
  public Object get(Record source, String memberName) {
    Field<?> field = matchField(source, memberName);
    if (field != null) {
      return source.getValue(field);
    }
    return null;
  }

  public Member<Record> getMember(Record source, String memberName) {
    Field<?> field = matchField(source, memberName);
    if (field != null) {
      return new Member<Record>(this, field.getType());
    }

    return null;
  }

  private Field<?> matchField(Record source, String memberName) {
    for (Field<?> field : source.fields())
      if (memberName.equalsIgnoreCase(field.getName()))
        return field;
    return null;
  }

  public Collection<String> memberNames(Record source) {
    Field<?>[] fields = source.fields();
    if (fields != null) {
      List<String> memberNames = new ArrayList<String>(fields.length);
      for (Field<?> field : fields)
        memberNames.add(field.getName());
      return memberNames;
    }

    return null;
  }

  @Override
  public String toString() {
    return "jOOQ";
  }
}
