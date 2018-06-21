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
package org.modelmapper.protobuf.primitive;

import com.google.protobuf.StringValue;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

/**
 * Converters for string.
 *
 * @author Chun Han Hsiao
 */
public class StringConverters {
  public static final Converter<StringValue.Builder, String> BUILDER_TO_STRING = new Converter<StringValue.Builder, String>() {
    @Override
    public String convert(MappingContext<StringValue.Builder, String> context) {
      if (context.getSource() != null)
        return context.getSource().getValue();
      return null;
    }
  };

  public static final Converter<StringValue, String> STRING_VALUE_TO_STRING = new Converter<StringValue, String>() {
    @Override
    public String convert(MappingContext<StringValue, String> context) {
      if (context.getSource() != null)
        return context.getSource().getValue();
      return null;
    }
  };

  public static final Converter<String, StringValue.Builder> STRING_TO_BUILDER = new Converter<String, StringValue.Builder>() {
    @Override
    public StringValue.Builder convert(MappingContext<String, StringValue.Builder> context) {
      if (context.getSource() != null)
        return StringValue.newBuilder().setValue(context.getSource());
      return null;
    }
  };

  public static final Converter<String, StringValue> STRING_TO_STRING_VALUE = new Converter<String, StringValue>() {
    @Override
    public StringValue convert(MappingContext<String, StringValue> context) {
      if (context.getSource() != null)
        return StringValue.of(context.getSource());
      return null;
    }
  };
}
