package org.modelmapper.gettingstarted;

import static org.testng.Assert.assertEquals;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

/**
 * http://modelmapper.org/getting-started/
 */
public class GettingStartedExample {
  public static void main(String... args) throws Exception {
    mapAutomatically();
    mapExplicitly();
  }

  /**
   * This example demonstrates how ModelMapper automatically maps properties from Order to OrderDTO.
   */
  static void mapAutomatically() {
    Order order = createOrder();
    ModelMapper modelMapper = new ModelMapper();
    OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
    assertOrdersEqual(order, orderDTO);
  }

  /**
   * This example demonstrates how ModelMapper can be used to explicitly map properties from an
   * Order to OrderDTO.
   */
  static void mapExplicitly() {
    Order order = createOrder();
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.addMappings(new PropertyMap<Order, OrderDTO>() {
      @Override
      protected void configure() {
        map().setBillingStreet(source.getBillingAddress().getStreet());
        map(source.billingAddress.getCity(), destination.billingCity);
      }
    });

    OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
    assertOrdersEqual(order, orderDTO);
  }

  static Order createOrder() {
    Customer customer = new Customer(new Name("Joe", "Smith"));
    Address billingAddress = new Address("2233 Pike Street", "Seattle");
    return new Order(customer, billingAddress);
  }

  static void assertOrdersEqual(Order order, OrderDTO orderDTO) {
    assertEquals(orderDTO.getCustomerFirstName(), order.getCustomer().getName().getFirstName());
    assertEquals(orderDTO.getCustomerLastName(), order.getCustomer().getName().getLastName());
    assertEquals(orderDTO.getBillingStreet(), order.getBillingAddress().getStreet());
    assertEquals(orderDTO.getBillingCity(), order.getBillingAddress().getCity());
  }
}
