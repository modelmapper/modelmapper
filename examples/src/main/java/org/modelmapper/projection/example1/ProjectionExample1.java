package org.modelmapper.projection.example1;

import static org.testng.Assert.assertEquals;

import org.modelmapper.ModelMapper;

public class ProjectionExample1 {
  public static void main(String... args) {
    OrderInfo orderInfo = new OrderInfo();
    orderInfo.setCustomerName("Joe Smith");
    orderInfo.setStreetAddress("1234 Main Street");

    ModelMapper modelMapper = new ModelMapper();
    Order order = modelMapper.map(orderInfo, Order.class);

    assertEquals(order.getCustomer().getName(), orderInfo.getCustomerName());
    assertEquals(order.getAddress().getStreet(), orderInfo.getStreetAddress());
  }
}
