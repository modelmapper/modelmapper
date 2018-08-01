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

import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import org.modelmapper.internal.Errors;
import org.modelmapper.spi.ValueReader;

/**
 * Protocol buffer ValueReader implementation.
 *
 * @author Chun Han Hsiao
 */
public class ProtobufValueReader implements ValueReader<MessageOrBuilder> {
  @Override
  public Object get(MessageOrBuilder source, String memberName) {
    try {
      Method method = ProtobufHelper.getter(source.getClass(), memberName);
      return method.invoke(source);
    } catch (NoSuchMethodException e) {
      throw new Errors().addMessage(e, "Cannot get the member").toMappingException();
    } catch (IllegalAccessException e) {
      throw new Errors().addMessage(e, "Cannot get the member").toMappingException();
    } catch (InvocationTargetException e) {
      throw new Errors().addMessage(e, "Cannot get the member").toMappingException();
    }
  }

  @Override
  public Member<MessageOrBuilder> getMember(MessageOrBuilder source, String memberName) {
    try {
      final Method getter = ProtobufHelper.getter(source.getClass(), memberName);
      final Object value = getter.invoke(source);
      if (Message.class.isAssignableFrom(getter.getReturnType())) {
        final Method hasMethod = ProtobufHelper.hasMethod(source.getClass(), memberName);
        return new Member<MessageOrBuilder>(getter.getReturnType()) {
          @Override
          public MessageOrBuilder getOrigin() {
            return MessageOrBuilder.class.isAssignableFrom(value.getClass())
                ? (MessageOrBuilder) value : null;
          }

          @Override
          public Object get(MessageOrBuilder source, String memberName) {
            try {
              if (Boolean.TRUE.equals(hasMethod.invoke(source)))
                return getter.invoke(source);
              return null;
            } catch (IllegalAccessException e) {
              throw new Errors().addMessage(e, "Cannot get the member").toMappingException();
            } catch (InvocationTargetException e) {
              throw new Errors().addMessage(e, "Cannot get the member").toMappingException();
            }
          }
        };
      } else
        return new Member<MessageOrBuilder>(getter.getReturnType()) {
          @Override
          public MessageOrBuilder getOrigin() {
            return MessageOrBuilder.class.isAssignableFrom(value.getClass())
                ? (MessageOrBuilder) value : null;
          }

          @Override
          public Object get(MessageOrBuilder source, String memberName) {
            try {
              return getter.invoke(source);
            } catch (IllegalAccessException e) {
              throw new Errors().addMessage(e, "Cannot get the member").toMappingException();
            } catch (InvocationTargetException e) {
              throw new Errors().addMessage(e, "Cannot get the member").toMappingException();
            }
          }
        };
    } catch (NoSuchMethodException e) {
      throw new Errors().addMessage(e, "Cannot get the member").toConfigurationException();
    } catch (IllegalAccessException e) {
      throw new Errors().addMessage(e, "Cannot get the member").toConfigurationException();
    } catch (InvocationTargetException e) {
      throw new Errors().addMessage(e, "Cannot get the member").toConfigurationException();
    }
  }

  @Override
  public Collection<String> memberNames(MessageOrBuilder source) {
    return ProtobufHelper.fields(source.getClass());
  }
}
