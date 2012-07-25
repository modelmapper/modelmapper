package org.modelmapper.functional.merging;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class MergeTypeMaps extends AbstractTest {
  static class OrderDTO {
    String customerAddressCity = "Portland";
    String customerName = "Chris";
  }

  static class Order {
    Customer customer;
  }

  static class Customer {
    Address address;
    String name;
  }

  static class Address {
    String city;
  }

  public void shouldMergeTypeMaps() {
    modelMapper.createTypeMap(OrderDTO.class, Customer.class);
    modelMapper.createTypeMap(OrderDTO.class, Order.class);

    Order order = modelMapper.map(new OrderDTO(), Order.class);
    assertEquals(order.customer.address.city, "Portland");
    assertEquals(order.customer.name, "Chris");
  }
}
