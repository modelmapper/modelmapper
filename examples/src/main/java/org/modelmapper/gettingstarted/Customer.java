package org.modelmapper.gettingstarted;

public class Customer {
  private Name name;

  public Customer(Name name) {
    this.name = name;
  }

  public Name getName() {
    return name;
  }

  public void setName(Name name) {
    this.name = name;
  }
}
