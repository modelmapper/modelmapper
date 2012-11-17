package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * https://github.com/jhalterman/modelmapper/issues/19
 */
@Test
public class GC19 extends AbstractTest {
  static class Order {
    Billing billing;
    Shipping shipping;
  }

  static class Billing {
    Address address;
  }

  static class Shipping {
    Address address;
  }

  static class Address {
    String id;
    String street;

    public String getStreet() {
      return street;
    }
  }

  static class OrderDTO {
    BillingDTO billing;
    ShippingDTO shipping;
  }

  static class BillingDTO {
    AddressDTO address;
  }

  static class ShippingDTO {
    AddressDTO address;
  }

  static class AddressDTO {
    String id;
    String streetName;

    public void setStreetName(String streetName) {
      this.streetName = streetName;
    }
  }

  public void test() {
    modelMapper.addMappings(new PropertyMap<Address, AddressDTO>() {
      protected void configure() {
        map().setStreetName(source.getStreet());
      }
    });

    Order order = new Order();
    order.billing = new Billing();
    order.billing.address = new Address();
    order.billing.address.id = "1";
    order.billing.address.street = "north";
    order.shipping = new Shipping();
    order.shipping.address = new Address();
    order.shipping.address.id = "2";
    order.shipping.address.street = "south";

    OrderDTO dto = modelMapper.map(order, OrderDTO.class);
    assertEquals(dto.billing.address.id, "1");
    assertEquals(dto.billing.address.streetName, "north");
    assertEquals(dto.shipping.address.id, "2");
    assertEquals(dto.shipping.address.streetName, "south");
  }
}
