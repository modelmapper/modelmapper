package org.modelmapper.internal;

import org.modelmapper.spi.PropertyInfo;
import org.modelmapper.spi.PropertyType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class ConstructorMutator extends PropertyInfoImpl<Method> implements Mutator, org.modelmapper.spi.PropertyInfo {


  public ConstructorMutator(String name, Class<?> initialType) {
    super(name, initialType);
  }

  @Override
  public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
    return null;
  }

  @Override
  public void setValue(Object subject, Object value) {

  }

  @Override
  public TypeInfo<?> getTypeInfo(InheritingConfiguration configuration) {
    return TypeInfoRegistry.typeInfoFor(type, configuration);
  }

  @Override
  public Type getGenericType() {
    return type;
  }
}
