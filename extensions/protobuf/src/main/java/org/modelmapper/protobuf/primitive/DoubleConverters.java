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

import com.google.protobuf.DoubleValue;
import com.google.protobuf.Int64Value;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

/**
 * Converters for double.
 *
 * @author Chun Han Hsiao
 */
public class DoubleConverters {
  public static final Converter<DoubleValue.Builder, Double> BUILDER_TO_DOUBLE = new Converter<DoubleValue.Builder, Double>() {
    @Override
    public Double convert(MappingContext<DoubleValue.Builder, Double> context) {
      if (context.getSource() != null)
        return context.getSource().getValue();
      return null;
    }
  };

  public static final Converter<DoubleValue, Double> DOUBLE_VALUE_TO_DOUBLE = new Converter<DoubleValue, Double>() {
    @Override
    public Double convert(MappingContext<DoubleValue, Double> context) {
      if (context.getSource() != null)
        return context.getSource().getValue();
      return null;
    }
  };

  public static final Converter<Double, DoubleValue.Builder> DOUBLE_TO_BUILDER = new Converter<Double, DoubleValue.Builder>() {
    @Override
    public DoubleValue.Builder convert(MappingContext<Double, DoubleValue.Builder> context) {
      if (context.getSource() != null)
        return DoubleValue.newBuilder().setValue(context.getSource());
      return null;
    }
  };

  public static final Converter<Double, DoubleValue> DOUBLE_TO_DOUBLE_VALUE = new Converter<Double, DoubleValue>() {
    @Override
    public DoubleValue convert(MappingContext<Double, DoubleValue> context) {
      if (context.getSource() != null)
        return DoubleValue.of(context.getSource());
      return null;
    }
  };
}
