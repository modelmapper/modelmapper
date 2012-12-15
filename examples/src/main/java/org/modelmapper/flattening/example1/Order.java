package org.modelmapper.flattening.example1;

public class Order {
  private Customer customer;
  private Address billingAddress;
  private Address shippingAddress;

  public Order(Customer customer, Address billingAddress, Address shippingAddress) {
    this.customer = customer;
    this.billingAddress = billingAddress;
    this.shippingAddress = shippingAddress;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public Address getShippingAddress() {
    return shippingAddress;
  }

  public void setShippingAddress(Address shippingAddress) {
    this.shippingAddress = shippingAddress;
  }

  public Address getBillingAddress() {
    return billingAddress;
  }

  public void setBillingAddress(Address billingAddress) {
    this.billingAddress = billingAddress;
  }
}
