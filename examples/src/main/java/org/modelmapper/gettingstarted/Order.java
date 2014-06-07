package org.modelmapper.gettingstarted;

public class Order {
  Customer customer;
  Address billingAddress;

  public Order(Customer customer, Address billingAddress) {
    this.customer = customer;
    this.billingAddress = billingAddress;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public Address getBillingAddress() {
    return billingAddress;
  }

  public void setBillingAddress(Address billingAddress) {
    this.billingAddress = billingAddress;
  }
}
