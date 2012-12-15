package org.modelmapper.flattening.example1;

import static org.testng.Assert.assertEquals;

import org.modelmapper.ModelMapper;

public class FlatteningExample1 {
  public static void main(String... args) {
    Customer customer = new Customer("Joe Smith");
    Address billingAddress = new Address("2233 Pike Street", "Seattle");
    Address shippingAddress = new Address("1234 Market Street", "San Francisco");
    Order order = new Order(customer, billingAddress, shippingAddress);

    ModelMapper modelMapper = new ModelMapper();
    OrderDTO dto = modelMapper.map(order, OrderDTO.class);

    assertEquals(dto.getCustomerName(), order.getCustomer().getName());
    assertEquals(dto.getShippingStreetAddress(), order.getShippingAddress().getStreet());
    assertEquals(dto.getShippingCity(), order.getShippingAddress().getCity());
    assertEquals(dto.getBillingStreetAddress(), order.getBillingAddress().getStreet());
    assertEquals(dto.getBillingCity(), order.getBillingAddress().getCity());
  }
}
