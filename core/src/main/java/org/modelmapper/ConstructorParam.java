package org.modelmapper;

/**
 * Describe a constructor parameter
 */
public class ConstructorParam {
  private final int index;
  private final String name;
  private final Class<?> type;

  /**
   *
   * @param index the positional index of the constructor parameter
   * @param name the literal name used for matchin
   * @param type type of the parameter
   */
  public ConstructorParam(int index, String name, Class<?> type) {
    this.index = index;
    this.name = name;
    this.type = type;
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
