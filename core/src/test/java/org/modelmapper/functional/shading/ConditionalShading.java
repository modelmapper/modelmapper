package org.modelmapper.functional.shading;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.modelmapper.AbstractTest;
import org.modelmapper.Condition;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

/**
 * Tests that property mappings are shaded if a higher portion of the property hierarchy is skipped.
 * 
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class ConditionalShading extends AbstractTest {
  static class Order {
    Customer customer;

    public Customer getCustomer() {
      return customer;
    }

    public void setCustomer(Customer customer) {
      this.customer = customer;
    }
  }

  static class Customer {
    Address address;
    String name;

    public Address getAddress() {
      return address;
    }

    public void setAddress(Address address) {
      this.address = address;
    }
  }

  static class Address {
    String street = "abc";

    public String getStreet() {
      return street;
    }

    public void setStreet(String street) {
      this.street = street;
    }

  }

  static class OrderDTO {
    String customerAddressStreet = "123";
    String customerName = "joe";

    public String getCustomerAddressStreet() {
      return customerAddressStreet;
    }

    public void setCustomerAddressStreet(String customerAddressStreet) {
      this.customerAddressStreet = customerAddressStreet;
    }
  }

  static class TestCondition implements Condition<Object, Object> {
    boolean applies;

    TestCondition(boolean applies) {
      this.applies = applies;
    }

    public boolean applies(MappingContext<Object, Object> context) {
      return applies;
    }
  }

  public void shouldConditionallyMap() {
    modelMapper.addMappings(new PropertyMap<OrderDTO, Order>() {
      protected void configure() {
        when(new TestCondition(true)).map(source).setCustomer(null);
      }
    });

    Order order = modelMapper.map(new OrderDTO(), Order.class);
    assertEquals(order.getCustomer().getAddress().getStreet(), "123");
  }

  public void shouldShadeAllProperties() {
    modelMapper.addMappings(new PropertyMap<OrderDTO, Order>() {
      protected void configure() {
        when(new TestCondition(false)).map().setCustomer(null);
      }
    });

    Order order = modelMapper.map(new OrderDTO(), Order.class);
    assertNull(order.getCustomer());
  }

  public void shouldShadeSomeProperties() {
    modelMapper.addMappings(new PropertyMap<OrderDTO, Order>() {
      protected void configure() {
        when(new TestCondition(false)).map().getCustomer().setAddress(null);
      }
    });

    Order order = modelMapper.map(new OrderDTO(), Order.class);
    assertNotNull(order.getCustomer());
    assertEquals(order.getCustomer().name, "joe");
    assertNull(order.getCustomer().getAddress());
  }
}
