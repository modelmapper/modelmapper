package org.modelmapper.flattening.example1;

public class OrderDTO {
  private String customerName;
  private String shippingStreetAddress;
  private String shippingCity;
  private String billingStreetAddress;
  private String billingCity;

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getShippingStreetAddress() {
    return shippingStreetAddress;
  }

  public void setShippingStreetAddress(String shippingStreetAddress) {
    this.shippingStreetAddress = shippingStreetAddress;
  }

  public String getShippingCity() {
    return shippingCity;
  }

  public void setShippingCity(String shippingCity) {
    this.shippingCity = shippingCity;
  }

  public String getBillingStreetAddress() {
    return billingStreetAddress;
  }

  public void setBillingStreetAddress(String billingStreetAddress) {
    this.billingStreetAddress = billingStreetAddress;
  }

  public String getBillingCity() {
    return billingCity;
  }

  public void setBillingCity(String billingCity) {
    this.billingCity = billingCity;
  }
}
