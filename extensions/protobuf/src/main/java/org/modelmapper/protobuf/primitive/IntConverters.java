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

import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

/**
 * Converters for bool.
 *
 * @author Chun Han Hsiao
 */
public class IntConverters {
  public static final Converter<Int32Value.Builder, Integer> BUILDER_TO_INT = new Converter<Int32Value.Builder, Integer>() {
    @Override
    public Integer convert(MappingContext<Int32Value.Builder, Integer> context) {
      if (context.getSource() != null)
        return context.getSource().getValue();
      return null;
    }
  };

  public static final Converter<Int64Value.Builder, Long> BUILDER_TO_LONG = new Converter<Int64Value.Builder, Long>() {
    @Override
    public Long convert(MappingContext<Int64Value.Builder, Long> context) {
      if (context.getSource() != null)
        return context.getSource().getValue();
      return null;
    }
  };

  public static final Converter<Int32Value, Integer> INT_VALUE_TO_INT = new Converter<Int32Value, Integer>() {
    @Override
    public Integer convert(MappingContext<Int32Value, Integer> context) {
      if (context.getSource() != null)
        return context.getSource().getValue();
      return null;
    }
  };

  public static final Converter<Int64Value, Long> LONG_VALUE_TO_LONG = new Converter<Int64Value, Long>() {
    @Override
    public Long convert(MappingContext<Int64Value, Long> context) {
      if (context.getSource() != null)
        return context.getSource().getValue();
      return null;
    }
  };

  public static final Converter<Integer, Int32Value.Builder> INT_TO_BUILDER = new Converter<Integer, Int32Value.Builder>() {
    @Override
    public Int32Value.Builder convert(MappingContext<Integer, Int32Value.Builder> context) {
      if (context.getSource() != null)
        return Int32Value.newBuilder().setValue(context.getSource());
      return null;
    }
  };

  public static final Converter<Long, Int64Value.Builder> LONG_TO_BUILDER = new Converter<Long, Int64Value.Builder>() {
    @Override
    public Int64Value.Builder convert(MappingContext<Long, Int64Value.Builder> context) {
      if (context.getSource() != null)
        return Int64Value.newBuilder().setValue(context.getSource());
      return null;
    }
  };

  public static final Converter<Integer, Int32Value> INT_TO_INT_VALUE = new Converter<Integer, Int32Value>() {
    @Override
    public Int32Value convert(MappingContext<Integer, Int32Value> context) {
      if (context.getSource() != null)
        return Int32Value.of(context.getSource());
      return null;
    }
  };

  public static final Converter<Long, Int64Value> LONG_TO_LONG_VALUE = new Converter<Long, Int64Value>() {
    @Override
    public Int64Value convert(MappingContext<Long, Int64Value> context) {
      if (context.getSource() != null)
        return Int64Value.of(context.getSource());
      return null;
    }
  };
}
