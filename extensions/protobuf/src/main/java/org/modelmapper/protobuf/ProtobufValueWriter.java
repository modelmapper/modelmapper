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

import com.google.protobuf.Message.Builder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.Errors;
import org.modelmapper.spi.ValueWriter;

/**
 * Protocol buffer ValueReader implementation.
 *
 * @author Chun Han Hsiao
 */
public class ProtobufValueWriter implements ValueWriter<Builder> {
  private ModelMapper modelMapper;

  public ProtobufValueWriter(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Override
  public void setValue(Builder destination, Object value, String memberName) {
    if (value == null)
      return;

    try {
      Class<? extends Builder> destinationType = destination.getClass();
      if (value instanceof Builder) {
        Method method = ProtobufHelper.setterForBuilder(destinationType, memberName);
        method.invoke(destination, value);
      } else if (value instanceof Iterable) {
        Class<?> iterableType = ProtobufHelper.iterableType(destinationType, memberName);
        Method method = ProtobufHelper.adder(destinationType, memberName);
        for (Object element : (Iterable<?>) value) {
          Object destElement = modelMapper.map(element, iterableType);
          method.invoke(destination, destElement);
        }
      } else {
        Method method = ProtobufHelper.setter(destination.getClass(), memberName);
        method.invoke(destination, value);
      }
    } catch (NoSuchMethodException e) {
      throw new Errors().addMessage(e, "Cannot set the member").toMappingException();
    } catch (IllegalAccessException e) {
      throw new Errors().addMessage(e, "Cannot set the member").toMappingException();
    } catch (InvocationTargetException e) {
      throw new Errors().addMessage(e, "Cannot set the member").toMappingException();
    }
  }

  @Override
  public Member<Builder> getMember(Class<Builder> destinationType, final String memberName) {
    try {
      final Class<?> memberType = ProtobufHelper.fieldType(destinationType, memberName);
      return new Member<Builder>(memberType) {
        @Override
        public void setValue(Builder destination, Object value) {
          ProtobufValueWriter.this.setValue(destination, value, memberName);
        }
      };
    } catch (NoSuchFieldException e) {
      throw new Errors().addMessage(e, "Cannot get the member").toMappingException();
    } catch (NoSuchMethodException e) {
      throw new Errors().addMessage(e, "Cannot get the member").toMappingException();
    }
  }

  @Override
  public Collection<String> memberNames(Class<Builder> destinationType) {
    return ProtobufHelper.fields(destinationType);
  }

  @Override
  public boolean isResolveMembersSupport() {
    return true;
  }
}
