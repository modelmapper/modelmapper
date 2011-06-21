package org.modelmapper.flattening.example1;

import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Assert;

public class FlatteningExample1 {
  public static void main(String... args) {
    Order order = new Order();

    Customer customer = new Customer();
    customer.setName("Joe");
    order.setCustomer(customer);

    Address billingAddress = new Address();
    billingAddress.setStreet("1234 Main Street");
    billingAddress.setCity("Seattle");
    order.setBillingAddress(billingAddress);

    Address shippingAddress = new Address();
    shippingAddress.setStreet("3456 Park Ave");
    shippingAddress.setCity("New York");
    order.setShippingAddress(shippingAddress);

    ModelMapper modelMapper = new ModelMapper();
    OrderDTO dto = modelMapper.map(order, OrderDTO.class);
    Assert.isTrue(dto.getCustomerName().equals(order.getCustomer().getName()));
    Assert.isTrue(dto.getShippingStreetAddress().equals(order.getShippingAddress().getStreet()));
    Assert.isTrue(dto.getShippingCity().equals(order.getShippingAddress().getCity()));
    Assert.isTrue(dto.getBillingStreetAddress().equals(order.getBillingAddress().getStreet()));
    Assert.isTrue(dto.getBillingCity().equals(order.getBillingAddress().getCity()));
  }
}
