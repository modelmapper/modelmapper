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

import com.google.protobuf.BoolValue;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

/**
 * Converters for bool.
 *
 * @author Chun Han Hsiao
 */
public class BoolConverters {
  public static final Converter<BoolValue.Builder, Boolean> BUILDER_TO_BOOL = new Converter<BoolValue.Builder, Boolean>() {
    @Override
    public Boolean convert(MappingContext<BoolValue.Builder, Boolean> context) {
      if (context.getSource() != null)
        return context.getSource().getValue();
      return null;
    }
  };

  public static final Converter<BoolValue, Boolean> BOOL_VALUE_TO_BOOL = new Converter<BoolValue, Boolean>() {
    @Override
    public Boolean convert(MappingContext<BoolValue, Boolean> context) {
      if (context.getSource() != null)
        return context.getSource().getValue();
      return null;
    }
  };

  public static final Converter<Boolean, BoolValue.Builder> BOOL_TO_BUILDER = new Converter<Boolean, BoolValue.Builder>() {
    @Override
    public BoolValue.Builder convert(MappingContext<Boolean, BoolValue.Builder> context) {
      if (context.getSource() != null)
        return BoolValue.newBuilder().setValue(context.getSource());
      return null;
    }
  };

  public static final Converter<Boolean, BoolValue> BOOL_TO_BOOL_VALUE = new Converter<Boolean, BoolValue>() {
    @Override
    public BoolValue convert(MappingContext<Boolean, BoolValue> context) {
      if (context.getSource() != null)
        return BoolValue.of(context.getSource());
      return null;
    }
  };
}
