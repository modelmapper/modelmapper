package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * https://github.com/jhalterman/modelmapper/issues/25
 * 
 * Since mapping merges take place when a TypeMap is created, expected merges may not take place.
 * Creating a TypeMap should not prevent future merges from occurring.
 * 
 * @author Jonathan Halterman
 */
@Test
public class GH25 extends AbstractTest {
  static class Order {
    Address address;
  }

  static class Address {
    String strNumber;
    String street;
  }

  static class OrderDTO {
    AddressDTO address;
  }

  static class ShippingDTO {
    AddressDTO address;
  }

  static class AddressDTO {
    Integer strNumber;
    String street;

    public void setStrNumber(Integer strNumber) {
      this.strNumber = strNumber;
    }
  }

  public void test() {
    modelMapper.addMappings(new PropertyMap<Order, OrderDTO>() {
      @Override
      protected void configure() {
      }
    });

    modelMapper.addMappings(new PropertyMap<Address, AddressDTO>() {
      protected void configure() {
        skip().setStrNumber(null);
      }
    });

    Order order = new Order();
    order.address = new Address();
    order.address.street = "Main";
    order.address.strNumber = "no10"; // Should be skipped

    OrderDTO dto = modelMapper.map(order, OrderDTO.class);

    assertEquals(dto.address.street, "Main");
    assertNull(dto.address.strNumber);
  }
}
