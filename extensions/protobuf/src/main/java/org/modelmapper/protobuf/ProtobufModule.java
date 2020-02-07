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

import static org.modelmapper.spi.StrongTypeConditionalConverter.wrap;

import com.google.protobuf.BoolValue;
import com.google.protobuf.DoubleValue;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.modelmapper.Module;
import org.modelmapper.protobuf.primitive.BoolConverters;
import org.modelmapper.protobuf.primitive.DoubleConverters;
import org.modelmapper.protobuf.primitive.IntConverters;
import org.modelmapper.protobuf.primitive.StringConverters;
import org.modelmapper.spi.ConditionalConverter;

/**
 * Module to support protocol buffer with ModelMapper..
 *
 * @author Chun Han Hsiao
 */
public class ProtobufModule implements Module {
  @Override
  public void setupModule(ModelMapper modelMapper) {
    modelMapper.getConfiguration().addValueReader(new ProtobufValueReader());
    modelMapper.getConfiguration().addValueWriter(new ProtobufValueWriter());

    List<ConditionalConverter<?, ?>> converters = modelMapper.getConfiguration().getConverters();
    converters.add(new MessageToBuilderConverter());

    converters.add(wrap(Boolean.class, BoolValue.class, BoolConverters.BOOL_TO_BOOL_VALUE));
    converters.add(wrap(BoolValue.class, Boolean.class, BoolConverters.BOOL_VALUE_TO_BOOL));
    converters.add(wrap(Boolean.class, BoolValue.Builder.class, BoolConverters.BOOL_TO_BUILDER));
    converters.add(wrap(BoolValue.Builder.class, Boolean.class, BoolConverters.BUILDER_TO_BOOL));

    converters.add(wrap(Long.class, Int64Value.class, IntConverters.LONG_TO_LONG_VALUE));
    converters.add(wrap(Int64Value.class, Long.class, IntConverters.LONG_VALUE_TO_LONG));
    converters.add(wrap(Long.class, Int64Value.Builder.class, IntConverters.LONG_TO_BUILDER));
    converters.add(wrap(Int64Value.Builder.class, Long.class, IntConverters.BUILDER_TO_LONG));

    converters.add(wrap(Integer.class, Int32Value.class, IntConverters.INT_TO_INT_VALUE));
    converters.add(wrap(Int32Value.class, Integer.class, IntConverters.INT_VALUE_TO_INT));
    converters.add(wrap(Integer.class, Int32Value.Builder.class, IntConverters.INT_TO_BUILDER));
    converters.add(wrap(Int32Value.Builder.class, Integer.class, IntConverters.BUILDER_TO_INT));

    converters.add(wrap(Double.class, DoubleValue.class, DoubleConverters.DOUBLE_TO_DOUBLE_VALUE));
    converters.add(wrap(DoubleValue.class, Double.class, DoubleConverters.DOUBLE_VALUE_TO_DOUBLE));
    converters.add(wrap(Double.class, DoubleValue.Builder.class, DoubleConverters.DOUBLE_TO_BUILDER));
    converters.add(wrap(DoubleValue.Builder.class, Double.class, DoubleConverters.BUILDER_TO_DOUBLE));

    converters.add(wrap(String.class, StringValue.class, StringConverters.STRING_TO_STRING_VALUE));
    converters.add(wrap(StringValue.class, String.class, StringConverters.STRING_VALUE_TO_STRING));
    converters.add(wrap(String.class, StringValue.Builder.class, StringConverters.STRING_TO_BUILDER));
    converters.add(wrap(StringValue.Builder.class, String.class, StringConverters.BUILDER_TO_STRING));
  }
}
