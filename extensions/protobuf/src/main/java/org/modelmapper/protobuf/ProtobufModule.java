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

import org.modelmapper.ModelMapper;
import org.modelmapper.Module;
import org.modelmapper.protobuf.primitive.BoolConverters;
import org.modelmapper.protobuf.primitive.DoubleConverters;
import org.modelmapper.protobuf.primitive.IntConverters;
import org.modelmapper.protobuf.primitive.StringConverters;

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

    modelMapper.getConfiguration().getConverters().add(new MessageToBuilderConverter());

    modelMapper.addConverter(BoolConverters.BOOL_TO_BOOL_VALUE);
    modelMapper.addConverter(BoolConverters.BOOL_VALUE_TO_BOOL);
    modelMapper.addConverter(BoolConverters.BOOL_TO_BUILDER);
    modelMapper.addConverter(BoolConverters.BUILDER_TO_BOOL);

    modelMapper.addConverter(IntConverters.LONG_TO_LONG_VALUE);
    modelMapper.addConverter(IntConverters.LONG_VALUE_TO_LONG);
    modelMapper.addConverter(IntConverters.LONG_TO_BUILDER);
    modelMapper.addConverter(IntConverters.BUILDER_TO_LONG);

    modelMapper.addConverter(IntConverters.INT_TO_INT_VALUE);
    modelMapper.addConverter(IntConverters.INT_VALUE_TO_INT);
    modelMapper.addConverter(IntConverters.INT_TO_BUILDER);
    modelMapper.addConverter(IntConverters.BUILDER_TO_INT);

    modelMapper.addConverter(DoubleConverters.DOUBLE_TO_DOUBLE_VALUE);
    modelMapper.addConverter(DoubleConverters.DOUBLE_VALUE_TO_DOUBLE);
    modelMapper.addConverter(DoubleConverters.DOUBLE_TO_BUILDER);
    modelMapper.addConverter(DoubleConverters.BUILDER_TO_DOUBLE);

    modelMapper.addConverter(StringConverters.STRING_TO_STRING_VALUE);
    modelMapper.addConverter(StringConverters.STRING_VALUE_TO_STRING);
    modelMapper.addConverter(StringConverters.STRING_TO_BUILDER);
    modelMapper.addConverter(StringConverters.BUILDER_TO_STRING);
  }
}
