package org.modelmapper.gettingstarted;

import static org.testng.Assert.assertEquals;

import org.modelmapper.ModelMapper;

/**
 * http://modelmapper.org/getting-started/
 */
public class GettingStartedExample {
  public static void main(String... args) throws Exception {
    Customer customer = new Customer(new Name("Joe", "Smith"));
    Address billingAddress = new Address("2233 Pike Street", "Seattle");
    Order order = new Order(customer, billingAddress);

    ModelMapper modelMapper = new ModelMapper();
    OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);

    assertEquals(orderDTO.getCustomerFirstName(), order.getCustomer().getName().getFirstName());
    assertEquals(orderDTO.getCustomerLastName(), order.getCustomer().getName().getLastName());
    assertEquals(orderDTO.getBillingStreet(), order.getBillingAddress().getStreet());
    assertEquals(orderDTO.getBillingCity(), order.getBillingAddress().getCity());
  }
}
