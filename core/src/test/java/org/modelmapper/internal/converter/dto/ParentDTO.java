package org.modelmapper.internal.converter.dto;

public class ParentDTO {

  private String name;
  private String secondName;
  private ChildDTO child;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSecondName() {
    return secondName;
  }

  public void setSecondName(String secondName) {
    this.secondName = secondName;
  }

  public ChildDTO getChild() {
    return child;
  }

  public void setChild(ChildDTO child) {
    this.child = child;
  }
}
