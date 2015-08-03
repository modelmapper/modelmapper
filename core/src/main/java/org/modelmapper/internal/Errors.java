/*
 * Copyright 2011 the original author or authors.
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
package org.modelmapper.internal;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

import org.modelmapper.ConfigurationException;
import org.modelmapper.MappingException;
import org.modelmapper.TypeMap;
import org.modelmapper.ValidationException;
import org.modelmapper.internal.util.Strings;
import org.modelmapper.internal.util.Types;
import org.modelmapper.spi.ErrorMessage;
import org.modelmapper.spi.PropertyInfo;
import org.modelmapper.spi.PropertyMapping;

public final class Errors {
  private List<ErrorMessage> errors;

  @SuppressWarnings("rawtypes") private static final Converter<?>[] converters = new Converter[] {
      new Converter<Class>(Class.class) {
        public String toString(Class type) {
          return type.getName();
        }
      }, new Converter<Member>(Member.class) {
        public String toString(Member member) {
          return Types.toString(member);
        }
      }, new Converter<PropertyInfo>(PropertyInfo.class) {
        public String toString(PropertyInfo propertyInfo) {
          return Types.toString(propertyInfo.getMember());
        }
      }, new Converter<Collection>(Collection.class) {
        public String toString(Collection collection) {
          StringBuilder builder = new StringBuilder();
          boolean first = true;
          for (Object o : collection) {
            if (first)
              first = false;
            else
              builder.append("\n");
            builder.append("\t").append(Errors.convert(o));
          }
          return builder.toString();
        }
      } };

  private static abstract class Converter<T> {
    final Class<T> type;

    Converter(Class<T> type) {
      this.type = type;
    }

    boolean appliesTo(Object subject) {
      return subject != null && type.isAssignableFrom(subject.getClass());
    }

    String convert(Object subject) {
      return toString(type.cast(subject));
    }

    abstract String toString(T subject);
  }

  public Errors() {
  }

  /** Returns the formatted message for an exception with the specified messages. */
  public static String format(String heading, Collection<ErrorMessage> errorMessages) {
    @SuppressWarnings("resource")
    Formatter fmt = new Formatter().format(heading).format(":%n%n");
    int index = 1;
    boolean displayCauses = getOnlyCause(errorMessages) == null;

    for (ErrorMessage errorMessage : errorMessages) {
      fmt.format("%s) %s%n", index++, errorMessage.getMessage());

      Throwable cause = errorMessage.getCause();
      if (displayCauses && cause != null) {
        StringWriter writer = new StringWriter();
        cause.printStackTrace(new PrintWriter(writer));
        fmt.format("Caused by: %s", writer.getBuffer());
      }

      fmt.format("%n");
    }

    if (errorMessages.size() == 1)
      fmt.format("1 error");
    else
      fmt.format("%s errors", errorMessages.size());

    return fmt.toString();
  }

  public static String format(String messageFormat, Object... arguments) {
    for (int i = 0; i < arguments.length; i++)
      arguments[i] = Errors.convert(arguments[i]);
    return String.format(messageFormat, arguments);
  }

  /**
   * Returns the cause throwable if there is exactly one cause in {@code messages}. If there are
   * zero or multiple messages with causes, null is returned.
   */
  public static Throwable getOnlyCause(Collection<ErrorMessage> messages) {
    Throwable onlyCause = null;
    for (ErrorMessage message : messages) {
      Throwable messageCause = message.getCause();
      if (messageCause == null) {
        continue;
      }

      if (onlyCause != null) {
        return null;
      }

      onlyCause = messageCause;
    }

    return onlyCause;
  }

  private static Object convert(Object source) {
    for (Converter<?> converter : converters)
      if (converter.appliesTo(source))
        return converter.convert(source);
    return source;
  }

  public Errors addMessage(Throwable cause, String message, Object... arguments) {
    addMessage(new ErrorMessage(format(message, arguments), cause));
    return this;
  }

  public Errors addMessage(ErrorMessage message) {
    if (errors == null)
      errors = new ArrayList<ErrorMessage>();
    errors.add(message);
    return this;
  }

  public Errors addMessage(String message, Object... arguments) {
    return addMessage(null, message, arguments);
  }

  public Errors errorGettingValue(Member member, Throwable t) {
    return addMessage(t, "Failed to get value from %s", member);
  }

  public Errors errorInstantiatingDestination(Class<?> type, Throwable t) {
    return addMessage(
        t,
        "Failed to instantiate instance of destination %s. Ensure that %s has a non-private no-argument constructor.",
        type, type);
  }

  public Errors errorMapping(Object source, Class<?> destinationType) {
    return addMessage("Error mapping %s to %s", source, Types.toString(destinationType));
  }

  public Errors errorMapping(Object source, Type destinationType, Throwable t) {
    return addMessage(t, "Error mapping %s to %s", source, Types.toString(destinationType));
  }

  public Errors errorSettingValue(Member member, Object value, Throwable t) {
    return addMessage(t, "Failed to set value '%s' on %s", value, member);
  }

  public Errors errorTooLarge(Object source, Class<?> destinationType) {
    return addMessage("Value '%s' is too large for %s", source, Types.toString(destinationType));
  }

  public Errors errorTooSmall(Object source, Class<?> destinationType) {
    return addMessage("Value '%s' is too small for %s", source, Types.toString(destinationType));
  }

  public Errors errorUnmappedProperties(TypeMap<?, ?> typeMap, List<PropertyInfo> unmappedProperties) {
    return addMessage("Unmapped destination properties found in %s:\n\n%s", typeMap,
        unmappedProperties);
  }

  public Errors errorUnsupportedMapping(Class<?> sourceType, Class<?> destinationType) {
    return addMessage("Missing type map configuration or unsupported mapping for %s to %s.",
        sourceType, destinationType);
  }

  public List<ErrorMessage> getMessages() {
    if (errors == null)
      return Collections.emptyList();
    return errors;
  }

  public boolean hasErrors() {
    return errors != null;
  }

  public Errors invalidProvidedDestinationInstance(Object destination, Class<?> requiredType) {
    return addMessage("The provided destination instance %s is not of the required type %s.",
        destination, requiredType);
  }

  public Errors merge(Collection<ErrorMessage> errorMessages) {
    for (ErrorMessage message : errorMessages)
      addMessage(message);
    return this;
  }

  public Errors merge(Errors errors) {
    for (ErrorMessage message : errors.getMessages())
      addMessage(message);
    return this;
  }

  public void throwConfigurationExceptionIfErrorsExist() {
    if (hasErrors())
      throw new ConfigurationException(getMessages());
  }

  public void throwValidationExceptionIfErrorsExist() {
    if (hasErrors())
      throw new ValidationException(getMessages());
  }

  public ConfigurationException toConfigurationException() {
    return new ConfigurationException(getMessages());
  }

  public ErrorsException toException() {
    return new ErrorsException(this);
  }

  public MappingException toMappingException() {
    return new MappingException(getMessages());
  }

  Errors ambiguousDestination(List<? extends PropertyMapping> mappings) {
    List<String> sourcePropertyInfo = new ArrayList<String>();
    for (PropertyMapping mapping : mappings)
      sourcePropertyInfo.add(Strings.joinMembers(mapping.getSourceProperties()));

    return addMessage(
        "The destination property %s matches multiple source property hierarchies:\n\n%s",
        Strings.joinMembers(mappings.get(0).getDestinationProperties()), sourcePropertyInfo);
  }

  Errors conditionalSkipWithoutSource() {
    return addMessage("A conditional skip can only be used with skip(Object, Object).");
  }

  Errors duplicateMapping(PropertyInfo destinationProperty) {
    return addMessage("A mapping already exists for %s.", destinationProperty);
  }

  Errors errorAccessingConfigure(Throwable t) {
    return addMessage(t, "Failed to access PropertyMap.configure().");
  }

  Errors errorAccessingProperty(PropertyInfo propertyInfo) {
    return addMessage("Failed to access %s.", propertyInfo);
  }

  Errors errorConverting(org.modelmapper.Converter<?, ?> converter, Class<?> sourceType,
      Class<?> destinationType, Throwable throwable) {
    return addMessage(throwable, "Converter %s failed to convert %s to %s.", converter, sourceType,
        destinationType);
  }

  Errors errorEnhancingClass(Class<?> type, Throwable t) {
    return addMessage(t,
        "Failed to generate proxy class for %s. Ensure that %s has a non-private constructor.",
        type, type);
  }

  Errors errorInstantiatingProxy(Class<?> type, Throwable t) {
    return addMessage(
        t,
        "Failed to instantiate proxied instance of %s. Ensure that %s has a non-private constructor.",
        type, type);
  }

  Errors errorResolvingClass(Throwable t, String className) {
    return addMessage("Error resolving class %s", className);
  }

  Errors errorReadingClass(Throwable t, String className) {
    return addMessage("Error reading class %s", className);
  }

  Errors errorInvalidSourcePath(String sourcePath, Class<?> unresolveableType,
      String unresolveableProperty) {
    return addMessage("The source path %s is invalid: %s.%s cannot be resolved.", sourcePath,
        unresolveableType, unresolveableProperty);
  }

  Errors errorNullArgument(String parameter) {
    return addMessage("The %s cannot be null", parameter);
  }

  Errors invalidDestinationMethod(Method method) {
    return addMessage(
        "Invalid destination method %s. Ensure that method has one parameter and returns void.",
        method);
  }

  Errors invalidDestinationField(Field field) {
    return addMessage("Invalid destination field %s. Ensure that field is not static.", field);
  }

  Errors invalidSourceMethod(Method method) {
    return addMessage(
        "Invalid source method %s. Ensure that method has zero parameters and does not return void.",
        method);
  }

  Errors invalidSourceField(Field field) {
    return addMessage("Invalid source field %s. Ensure that field is not static.", field);
  }

  Errors invocationAgainstFinalClass(Class<?> type) {
    return addMessage("Cannot map final type %s.", type);
  }

  Errors invocationAgainstFinalMethod(Member member) {
    return addMessage("Cannot map final method %s.", member);
  }

  Errors mappingForEnum() {
    return addMessage("Cannot create mapping for enum.");
  }

  Errors missingDestination() {
    return addMessage("A mapping is missing a required destination member.");
  }

  Errors missingMutatorForAccessor(Method method) {
    return addMessage("No corresponding mutator was found for %s.", method);
  }

  Errors missingSource() {
    return addMessage("A mapping is missing a required source member.");
  }

  Errors sourceOutsideOfMap() {
    return addMessage("'source' cannot be used outside of a map statement.");
  }

  void throwMappingExceptionIfErrorsExist() {
    if (hasErrors())
      throw new MappingException(getMessages());
  }
}
