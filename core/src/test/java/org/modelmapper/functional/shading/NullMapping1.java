package org.modelmapper.functional.shading;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * Tests that property mappings are shaded if a higher portion of the property hierarchy is set to
 * null.
 * 
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class NullMapping1 extends AbstractTest {
  static class Order {
    Customer customer;

    public void setCustomer(Customer customer) {
      this.customer = customer;
    }
  }

  static class Customer {
    String name;
  }

  static class OrderDTO {
    String customerName = "joe";
  }

  public void shouldMap() {
    Order order = modelMapper.map(new OrderDTO(), Order.class);
    assertEquals(order.customer.name, "joe");
  }

  public void shouldShadeWhenNullEncountered() {
    modelMapper.addMappings(new PropertyMap<OrderDTO, Order>() {
      protected void configure() {
        map().setCustomer(null);
      }
    });

    Order order = modelMapper.map(new OrderDTO(), Order.class);
    assertNull(order.customer);
  }
}