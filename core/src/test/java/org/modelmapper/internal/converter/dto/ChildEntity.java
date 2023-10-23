package org.modelmapper.internal.converter.dto;

public class ChildEntity {

  private String name;

  public ChildEntity() {
  }

  public ChildEntity(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
