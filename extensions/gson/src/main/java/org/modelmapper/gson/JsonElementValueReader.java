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
package org.modelmapper.gson;

import java.util.Collection;
import java.util.Set;

import org.modelmapper.internal.util.Lists;
import org.modelmapper.spi.ValueReader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * JsonElement ValueReader implementation.
 * 
 * @author Jonathan Halterman
 */
public class JsonElementValueReader implements ValueReader<JsonElement> {
  public Object get(JsonElement source, String memberName) {
    if (source.isJsonObject()) {
      JsonObject subjObj = source.getAsJsonObject();
      JsonElement propertyElement = subjObj.get(memberName);
      if (propertyElement == null)
        return null;

      if (propertyElement.isJsonObject())
        return propertyElement.getAsJsonObject();
      if (propertyElement.isJsonArray())
        return propertyElement.getAsJsonArray();
      if (propertyElement.isJsonPrimitive()) {
        JsonPrimitive jsonPrim = propertyElement.getAsJsonPrimitive();
        if (jsonPrim.isBoolean())
          return jsonPrim.getAsBoolean();
        if (jsonPrim.isNumber())
          return jsonPrim.getAsNumber();
        if (jsonPrim.isString())
          return jsonPrim.getAsString();
      }
    }

    return null;
  }

  public Member<JsonElement> getMember(JsonElement source, String memberName) {
    final Object value = get(source, memberName);
    if (value == null)
      return null;

    if (value instanceof JsonElement) {
      return new Member<JsonElement>(this, JsonElement.class, (JsonElement) value);
    }

    return new Member<JsonElement>(this, value.getClass());
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Collection<String> memberNames(JsonElement source) {
    if (source.isJsonObject())
      return Lists.from((Set) ((JsonObject) source).entrySet());
    return null;
  }

  @Override
  public String toString() {
    return "Gson";
  }
}
