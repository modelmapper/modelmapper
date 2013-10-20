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
package org.modelmapper.jackson;

import java.io.IOException;
import java.util.Collection;

import org.modelmapper.internal.util.Lists;
import org.modelmapper.spi.ValueReader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;

/**
 * JsonNode ValueReader implementation.
 * 
 * @author Jonathan Halterman
 */
public class JsonNodeValueReader implements ValueReader<JsonNode> {
  public Object get(JsonNode source, String memberName) {
    JsonNode propertyNode = source.get(memberName);
    if (propertyNode == null)
      throw new IllegalArgumentException();

    switch (propertyNode.getNodeType()) {
      case BOOLEAN:
        return propertyNode.asBoolean();
      case NULL:
        return null;
      case NUMBER:
        return ((NumericNode) propertyNode).numberValue();
      case POJO:
        return ((POJONode) propertyNode).getPojo();
      case STRING:
        return propertyNode.asText();
      case BINARY:
        try {
          return propertyNode.binaryValue();
        } catch (IOException ignore) {
          return null;
        }

      case ARRAY:
      case OBJECT:
      case MISSING:
      default:
        return propertyNode;
    }
  }

  public Collection<String> memberNames(JsonNode source) {
    if (source.isObject())
      return Lists.from(((ObjectNode) source).fieldNames());
    return null;
  }

  @Override
  public String toString() {
    return "Jackson";
  }
}
