package org.modelmapper;

import java.lang.reflect.Constructor;

public class UseConstructorOverrideException extends RuntimeException{
  private final Constructor<?> otherConstructor;

  public Constructor<?> getOtherConstructor() {
    return otherConstructor;
  }

  public <T> UseConstructorOverrideException(Constructor<?> otherConstructor) {
    this.otherConstructor = otherConstructor;
  }
}
