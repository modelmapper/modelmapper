package org.modelmapper;

import java.lang.reflect.Constructor;

/**
 * Describe a constructor parameter
 */
public class ConstructorParam {
  private final Constructor<?> constructor;
  private final int index;
  private final String name;
  private final Class<?> type;

  /**
   * Creates a ConstructorParam.
   *
   * @param constructor the constructor
   * @param index the positional index of the constructor parameter
   * @param name the literal name used for matchin
   * @param type type of the parameter
   */
  public ConstructorParam(Constructor<?> constructor, int index, String name, Class<?> type) {
    this.constructor = constructor;
    this.index = index;
    this.name = name;
    this.type = type;
  }

  public Constructor<?> getConstructor() {
    return constructor;
  }

  public int getIndex() {
    return index;
  }

  public String getName() {
    return name;
  }

  public Class<?> getType() {
    return type;
  }
}
