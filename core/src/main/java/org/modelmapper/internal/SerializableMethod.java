package org.modelmapper.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.internal.util.Types;

public class SerializableMethod implements Member, Serializable {

  private static final long serialVersionUID = -2893086935392387994L;

  private Method method;

  public SerializableMethod(Method method) {
    this.method = method;
  }

  public Method getMethod() {
    return method;
  }

  public Class<?> getDeclaringClass() {
    return method.getDeclaringClass();
  }

  public String getName() {
    return method.getName();
  }

  public int getModifiers() {
    return method.getModifiers();
  }

  public boolean isSynthetic() {
    return method.isSynthetic();
  }

  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.writeObject(getDeclaringClass());
    stream.writeObject(getName());
    for (Class<?> paramType : method.getParameterTypes()) {
      stream.writeObject(paramType);
    }
  }

  private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
    Class<?> type = (Class<?>) stream.readObject();
    String name = (String) stream.readObject();
    List<Class<?>> parameterTypes = new ArrayList<Class<?>>();
    try {
      Class<?> paramType = (Class<?>) stream.readObject();
      while (true) {
        parameterTypes.add(paramType);
        paramType = (Class<?>) stream.readObject();
      }
    } catch (OptionalDataException ode) {
      //Ignore. Nothing more to read
    }
    method = Types.methodFor(type, name, parameterTypes.toArray(new Class[0]));
  }

  @Override
  public String toString() {
    return method.toString();
  }

  @Override
  public int hashCode() {
    return method.hashCode();
  }

  @Override
  public boolean equals(Object paramObject) {
    if(paramObject == null) {
      return false;
    }
    if(!paramObject.getClass().isAssignableFrom(SerializableMethod.class)) {
      return false;
    }
    return method.equals(((SerializableMethod)paramObject).getMethod());
  }
}
