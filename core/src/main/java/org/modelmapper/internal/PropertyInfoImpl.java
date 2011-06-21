package org.modelmapper.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.modelmapper.spi.PropertyInfo;
import org.modelmapper.spi.PropertyType;

/**
 * Abstract PropertyInfo implementation that provides {@link #equals(Object)} and
 * {@link #hashCode()} operations based on the property name.
 */
abstract class PropertyInfoImpl<M extends Member> implements PropertyInfo {
  protected final M member;
  protected final Class<?> type;
  protected final String name;
  private final PropertyType propertyType;

  private PropertyInfoImpl(M member, PropertyType propertyType, Class<?> type, String name) {
    this.member = member;
    this.propertyType = propertyType;
    this.type = type;
    this.name = name;
  }

  static abstract class AbstractMethodInfo extends PropertyInfoImpl<Method> {
    private AbstractMethodInfo(Method method, Class<?> type, String name) {
      super(method, PropertyType.METHOD, type, name);
      method.setAccessible(true);
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
      return member.getAnnotation(annotationClass);
    }
  }

  static class FieldPropertyInfo extends PropertyInfoImpl<Field> implements Accessor, Mutator {
    FieldPropertyInfo(Field field, String name) {
      super(field, PropertyType.FIELD, field.getType(), name);
      field.setAccessible(true);
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
      return member.getAnnotation(annotationClass);
    }

    @Override
    public Type getGenericType() {
      return member.getGenericType();
    }

    @Override
    public Object getValue(Object subject) {
      try {
        return member.get(subject);
      } catch (Exception e) {
        throw new Errors().errorGettingValue(member, e).toMappingException();
      }
    }

    @Override
    public void setValue(Object subject, Object value) {
      try {
        member.set(subject, value);
      } catch (Exception e) {
        throw new Errors().errorSettingValue(member, value, e).toMappingException();
      }
    }
  }

  static class MethodAccessor extends AbstractMethodInfo implements Accessor {
    MethodAccessor(Method method, String name) {
      super(method, method.getReturnType(), name);
    }

    @Override
    public Type getGenericType() {
      return member.getGenericReturnType();
    }

    @Override
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
  }

  static class MethodMutator extends AbstractMethodInfo implements Mutator {
    MethodMutator(Method method, String name) {
      super(method, method.getParameterTypes()[0], name);
    }

    @Override
    public Type getGenericType() {
      return member.getGenericParameterTypes()[0];
    }

    @Override
    public void setValue(Object subject, Object value) {
      try {
        member.invoke(subject, value);
      } catch (Exception e) {
        throw new Errors().errorSettingValue(member, value, e).toMappingException();
      }
    }
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

  @Override
  public M getMember() {
    return member;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public PropertyType getPropertyType() {
    return propertyType;
  }

  @Override
  public Class<?> getType() {
    return type;
  }

  @Override
  public int hashCode() {
    return member.getDeclaringClass().hashCode() * 31 + name.hashCode();
  }

  @Override
  public String toString() {
    return member.getDeclaringClass().getName() + "." + name;
  }
}