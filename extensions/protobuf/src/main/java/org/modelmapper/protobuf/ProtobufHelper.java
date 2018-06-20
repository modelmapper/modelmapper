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

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.modelmapper.internal.Errors;

public class ProtobufHelper {
  private ProtobufHelper() {
  }

  public static boolean hasBuilder(Class<?> builderType, String field) {
    return builder(builderType, field) != null;
  }

  @SuppressWarnings("unchecked")
  public static Class<? extends Builder> builder(Class<?> builderType, String field) {
    try {
      String methodName = "get" + firstCharToUpperCase(field) + "Builder";
      return (Class<? extends Builder>) builderType.getMethod(methodName).getReturnType();
    } catch (NoSuchMethodException e) {
      return null;
    }
  }

  public static List<String> fields(Class<?> type) {
    if (Builder.class.isAssignableFrom(type))
      return fieldsOfMessage(messageOfBuilder(type));
    if (Message.class.isAssignableFrom(type))
      return fieldsOfMessage(type);
    throw new Errors().addMessage("Invalid protocol buffer type: %s", type.getName()).toConfigurationException();
  }

  public static Method getter(Class<?> type, String field) throws NoSuchMethodException {
    String methodName = "get" + firstCharToUpperCase(field);
    return type.getMethod(methodName);
  }

  public static Class<?> messageOfBuilder(Class<?> builderType) {
    try {
      Method buildMethod = builderType.getDeclaredMethod("build");
      return buildMethod.getReturnType();
    } catch (NoSuchMethodException e) {
      throw new Errors().addMessage(e, "Invalid protocol buffer type").toConfigurationException();
    }
  }

  public static Method setterForBuilder(Class<?> type, String field) throws NoSuchMethodException {
    String methodName = "set" + firstCharToUpperCase(field);
    for (Method method : type.getMethods()) {
      if (isSetterForBuilder(method, methodName))
        return method;
    }
    throw new NoSuchMethodException(methodName);
  }

  public static Method setter(Class<?> type, String field) throws NoSuchMethodException {
    String methodName = "set" + firstCharToUpperCase(field);
    for (Method method : type.getMethods()) {
      if (isSetterForPrimitive(method, methodName))
        return method;
    }
    throw new NoSuchMethodException(methodName);
  }

  public static Method hasMethod(Class<?> type, String field) throws NoSuchMethodException {
    String methodName = "has" + firstCharToUpperCase(field);
    return type.getMethod(methodName);
  }

  private static List<String> fieldsOfMessage(Class<?> type) {
    try {
      Method descriptorMethod = type.getDeclaredMethod("getDescriptor");
      Descriptor descriptor = (Descriptor) descriptorMethod.invoke(type);
      List<String> fields = new ArrayList<String>();
      for (FieldDescriptor field : descriptor.getFields()) {
        fields.add(field.getName());
      }
      return fields;
    } catch (NoSuchMethodException e) {
      throw new Errors().addMessage(e, "Invalid protocol buffer type").toConfigurationException();
    } catch (IllegalAccessException e) {
      throw new Errors().addMessage(e, "Invalid protocol buffer type").toConfigurationException();
    } catch (InvocationTargetException e) {
      throw new Errors().addMessage(e, "Invalid protocol buffer type").toConfigurationException();
    }
  }

  private static String firstCharToUpperCase(String str) {
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }

  private static boolean isSetterForPrimitive(Method method, String methodName) {
    if (!method.getName().equalsIgnoreCase(methodName))
      return false;
    Class<?>[] parameterTypes = method.getParameterTypes();
    return parameterTypes.length == 1 && !Message.Builder.class.isAssignableFrom(parameterTypes[0]);
  }

  private static boolean isSetterForBuilder(Method method, String methodName) {
    if (!method.getName().equalsIgnoreCase(methodName))
      return false;
    Class<?>[] parameterTypes = method.getParameterTypes();
    return parameterTypes.length == 1 && Message.Builder.class.isAssignableFrom(parameterTypes[0]);
  }
}
