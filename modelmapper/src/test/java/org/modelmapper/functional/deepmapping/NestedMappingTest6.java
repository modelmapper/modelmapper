package org.modelmapper.functional.deepmapping;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class NestedMappingTest6 extends AbstractTest {
  private static class Order {
    Customer customer = new Customer();
    Address address = new Address();
  }

  private static class Customer {
    int id = 3;
  }

  private static class Address {
    int id = 5;
  }

  private static class OrderDTO {
    int customerId = 1;
    int addressId = 2;
  }

  /**
   * <pre>
   * Customer customer/int id -> customerId
   * Address address/int id -> addressId;
   * </pre>
   */
  public void shouldMapOrderToOrderDTO() {
    OrderDTO dto = modelMapper.map(new Order(), OrderDTO.class);

    modelMapper.validate();
    assertEquals(dto.customerId, 3);
    assertEquals(dto.addressId, 5);
  }

  /**
   * <pre>
   * int customerId -> customer/id
   * int addressId -> address/id
   * </pre>
   */
  public void shouldMapOrderDTPOoOrder() {
    Order o = modelMapper.map(new OrderDTO(), Order.class);

    modelMapper.validate();
    assertEquals(o.customer.id, 1);
    assertEquals(o.address.id, 2);
  }
}
