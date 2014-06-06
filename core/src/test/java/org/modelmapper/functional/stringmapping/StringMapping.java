package org.modelmapper.functional.stringmapping;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.ConfigurationException;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

@Test
public class StringMapping extends AbstractTest {
  static class OrderParent {
    Customer customer;
  }

  static class Order extends OrderParent {
  }

  static class Customer {
    String name;
    Address address;
  }

  static class Address {
    String street;
    String city;
    String state;
  }

  static class OrderDTO {
    String customerName;
    String customerStreet;
    String customerCity;
    String customerState;

    public void setCustomerName(String customerName) {
      this.customerName = customerName;
    }

    public void setCustomerStreet(String customerStreet) {
      this.customerStreet = customerStreet;
    }

    public void setCustomerCity(String customerCity) {
      this.customerCity = customerCity;
    }

    public void setCustomerState(String customerState) {
      this.customerState = customerState;
    }
  }

  public void shouldMapFromSourceStrings() {
    modelMapper.addMappings(new PropertyMap<Order, OrderDTO>() {
      @Override
      protected void configure() {
        map().setCustomerName(this.<String>source("customer.name"));
        map().setCustomerStreet(this.<String>source("customer.address.street"));
        map(source("customer.address.city")).setCustomerCity(null);
        map(source("customer.address.state")).setCustomerState(null);
      }
    });

    Order order = new Order();
    order.customer = new Customer();
    order.customer.name = "joe";
    order.customer.address = new Address();
    order.customer.address.street = "1234 main street";
    order.customer.address.city = "SF";
    order.customer.address.state = "CA";
    OrderDTO dto = modelMapper.map(order, OrderDTO.class);

    assertEquals(dto.customerName, order.customer.name);
    assertEquals(dto.customerStreet, order.customer.address.street);
    assertEquals(dto.customerCity, order.customer.address.city);
    assertEquals(dto.customerState, order.customer.address.state);
  }

  @Test(expectedExceptions = ConfigurationException.class)
  public void shouldThrowOnInvalidSourcePath() {
    modelMapper.addMappings(new PropertyMap<Order, OrderDTO>() {
      @Override
      protected void configure() {
        map().setCustomerName(this.<String>source("customer.foo.boo"));
        map().setCustomerStreet(this.<String>source("bar.baz"));
      }
    });
  }
}
