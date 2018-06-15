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

import net.jodah.typetools.TypeResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.modelmapper.spi.PropertyInfo;
import org.modelmapper.spi.PropertyType;
import org.modelmapper.spi.ValueReader;
import org.modelmapper.spi.ValueWriter;

/**
 * Abstract PropertyInfo implementation that provides {@link #equals(Object)} and
 * {@link #hashCode()} operations based on the property name.
 * 
 * @author Jonathan Halterman
 */
abstract class PropertyInfoImpl<M extends Member> implements PropertyInfo {
  protected final Class<?> initialType;
  protected final M member;
  protected final Class<?> type;
  protected final String name;
  private final PropertyType propertyType;

  static abstract class AbstractMethodInfo extends PropertyInfoImpl<Method> {
    private AbstractMethodInfo(Class<?> initialType, Method method, String name) {
      super(initialType, method, PropertyType.METHOD, name);
      method.setAccessible(true);
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
      return member.getAnnotation(annotationClass);
    }
  }

  static class FieldPropertyInfo extends PropertyInfoImpl<Field> implements Accessor, Mutator {
    FieldPropertyInfo(Class<?> initialType, Field field, String name) {
      super(initialType, field, PropertyType.FIELD, name);
      field.setAccessible(true);
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
      return member.getAnnotation(annotationClass);
    }

    public Type getGenericType() {
      return member.getGenericType();
    }

    public Object getValue(Object subject) {
      try {
        return member.get(subject);
      } catch (Exception e) {
        throw new Errors().errorGettingValue(member, e).toMappingException();
      }
    }

    public void setValue(Object subject, Object value) {
      try {
        member.set(subject, value);
      } catch (Exception e) {
        throw new Errors().errorSettingValue(member, value, e).toMappingException();
      }
    }

    public TypeInfo<?> getTypeInfo(InheritingConfiguration configuration) {
      return TypeInfoRegistry.typeInfoFor(this, configuration);
    }
  }

  static class MethodAccessor extends AbstractMethodInfo implements Accessor {
    MethodAccessor(Class<?> initialType, Method method, String name) {
      super(initialType, method, name);
    }

    public Type getGenericType() {
      return member.getGenericReturnType();
    }

    public Object getValue(Object subject) {
      try {
        return member.invoke(subject);
      } catch (IllegalAccessException e) {
        new Errors().errorAccessingProperty(this).throwMappingExceptionIfErrorsExist();
        return null;
      } catch (Exception e) {
        throw new Errors().errorGettingValue(member, e).toMappingException();
      }
    }

    public TypeInfo<?> getTypeInfo(InheritingConfiguration configuration) {
      return TypeInfoRegistry.typeInfoFor(this, configuration);
    }
  }

  static class MethodMutator extends AbstractMethodInfo implements Mutator {
    MethodMutator(Class<?> initialType, Method method, String name) {
      super(initialType, method, name);
    }

    public Type getGenericType() {
      return member.getGenericParameterTypes()[0];
    }

    public void setValue(Object subject, Object value) {
      try {
        member.invoke(subject, value);
      } catch (Exception e) {
        throw new Errors().errorSettingValue(member, value, e).toMappingException();
      }
    }

    public TypeInfo<?> getTypeInfo(InheritingConfiguration configuration) {
      return TypeInfoRegistry.typeInfoFor(type, configuration);
    }
  }

  static class ValueReaderPropertyInfo extends PropertyInfoImpl<Member> implements Accessor {
    private ValueReader.Member<Object> valueReaderMember;

    @SuppressWarnings("unchecked")
    ValueReaderPropertyInfo(ValueReader.Member<?> valueReaderMember, Class<?> initialType, String name) {
      super(initialType, null, PropertyType.GENERIC, name);
      this.valueReaderMember = (ValueReader.Member<Object>) valueReaderMember;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
      return null;
    }

    @Override
    public Type getGenericType() {
      return type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getValue(Object subject) {
      return valueReaderMember.get(subject, name);
    }

    @Override
    public TypeInfo<?> getTypeInfo(InheritingConfiguration configuration) {
      return TypeInfoRegistry.typeInfoFor(type, configuration);
    }

    static ValueReaderPropertyInfo fromMember(final ValueReader.Member<?> valueReaderMember, String memberName) {
      if (valueReaderMember.getOrigin() != null) {
        return new ValueReaderPropertyInfo(valueReaderMember, valueReaderMember.getValueType(), memberName) {
          @Override
          public TypeInfo<?> getTypeInfo(InheritingConfiguration configuration) {
            return TypeInfoRegistry.typeInfoFor(valueReaderMember.getOrigin(),
                valueReaderMember.getValueType(), configuration);
          }
        };
      }
      return new ValueReaderPropertyInfo(valueReaderMember, valueReaderMember.getValueType(), memberName);
    }

    @SuppressWarnings("unchecked")
    static ValueReaderPropertyInfo create(final ValueReader<?> valueReader, String memberName) {
      final ValueReader<Object> uncheckedValueReader = (ValueReader<Object>) valueReader;
      ValueReader.Member<?> valueReaderMember = new ValueReader.Member<Object>(Object.class) {
        @Override
        public Object get(Object source, String memberName) {
          return uncheckedValueReader.get(source, memberName);
        }
      };
      return new ValueReaderPropertyInfo(valueReaderMember, valueReaderMember.getValueType(), memberName);
    }
  }
  static class ValueWriterPropertyInfo extends PropertyInfoImpl<Member> implements Mutator {
    private ValueWriter.Member<Object> valueWriterMember;

    @SuppressWarnings("unchecked")
    ValueWriterPropertyInfo(ValueWriter.Member<?> valueWriterMember, Class<?> initialType, String name) {
      super(initialType, null, PropertyType.GENERIC, name);
      this.valueWriterMember = (ValueWriter.Member<Object>) valueWriterMember;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
      return null;
    }

    @Override
    public Type getGenericType() {
      return type;
    }

    @Override
    public TypeInfo<?> getTypeInfo(InheritingConfiguration configuration) {
      return TypeInfoRegistry.typeInfoFor(type, configuration);
    }

    @Override
    public void setValue(Object subject, Object value) {
      valueWriterMember.setValue(subject, value);
    }

    static ValueWriterPropertyInfo fromMember(final ValueWriter.Member<?> valueWriterMember, String memberName) {
      if (valueWriterMember.getOrigin() != null) {
        return new ValueWriterPropertyInfo(valueWriterMember, valueWriterMember.getValueType(), memberName) {
          @Override
          public TypeInfo<?> getTypeInfo(InheritingConfiguration configuration) {
            return TypeInfoRegistry.typeInfoFor(valueWriterMember.getOrigin(),
                valueWriterMember.getValueType(), configuration);
          }
        };
      }
      return new ValueWriterPropertyInfo(valueWriterMember, valueWriterMember.getValueType(), memberName);
    }

    @SuppressWarnings("unchecked")
    static ValueWriterPropertyInfo create(final ValueWriter<?> valueWriter, final String memberName) {
      final ValueWriter<Object> uncheckedValueWriter = (ValueWriter<Object>) valueWriter;
      ValueWriter.Member<?> valueWriterMember = new ValueWriter.Member<Object>(Object.class) {
        @Override
        public void setValue(Object source, Object value) {
          uncheckedValueWriter.setValue(source, value, memberName);
        }
      };
      return new ValueWriterPropertyInfo(valueWriterMember, valueWriterMember.getValueType(), memberName);
    }
  }

  private PropertyInfoImpl(Class<?> initialType, M member, PropertyType propertyType, String name) {
    this.initialType = initialType;
    this.member = member;
    this.propertyType = propertyType;
    Type genericType = getGenericType();
    this.type = genericType == null ? initialType : TypeResolver.resolveRawClass(genericType,
        initialType);
    this.name = name;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || !(obj instanceof PropertyInfo))
      return false;
    PropertyInfoImpl<?> other = (PropertyInfoImpl<?>) obj;
    return member.getDeclaringClass().equals(other.member.getDeclaringClass())
        && name.equals(other.getName());
  }

  public Class<?> getInitialType() {
    return initialType;
  }

  public M getMember() {
    return member;
  }

  public String getName() {
    return name;
  }

  public PropertyType getPropertyType() {
    return propertyType;
  }

  public Class<?> getType() {
    return type;
  }

  @Override
  public int hashCode() {
    return (member == null ? 1 : member.getDeclaringClass().hashCode()) * 31 + name.hashCode();
  }

  @Override
  public String toString() {
    return member == null ? name : member.getDeclaringClass().getSimpleName() + "." + name;
  }
}