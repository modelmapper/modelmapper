package org.modelmapper.flattening.example2;

public class Person {
  private Address address;

  public void setAddress(Address address) {
    this.address = address;
  }

  public Address getAddress() {
    return address;
  }
}
