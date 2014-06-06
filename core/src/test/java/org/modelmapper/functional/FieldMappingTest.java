package org.modelmapper.functional;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractConverter;
import org.modelmapper.AbstractProvider;
import org.modelmapper.AbstractTest;
import org.modelmapper.Conditions;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.Provider;
import org.modelmapper.convention.MatchingStrategies;
import org.testng.annotations.Test;

/**
 * Tests the mapping of fields using a PropertyMap.
 * 
 * @author Jonathan Halterman
 */
@Test
public class FieldMappingTest extends AbstractTest {
  static class Order {
    Customer customer;

    public Customer getCustomer() {
      return customer;
    }
  }

  static class Customer {
    String sId;
    String s1;
    Address address;

    public Address getAddress() {
      return address;
    }
  }

  static class Address {
    String streetName;
    String sZip;

    public String getStreetName() {
      return streetName;
    }

    public String getSZip() {
      return sZip;
    }
  }

  static class OrderDTO {
    CustomerDTO customer;

    public CustomerDTO getCustomer() {
      return customer;
    }
  }

  static class CustomerDTO {
    int id;
    String d1;
    String d2;
    AddressDTO address;

    public void setId(int id) {
      this.id = id;
    }

    public void setD2(String d2) {
      this.d2 = d2;
    }
  }

  static class AddressDTO {
    String street;
    int zip;

    public void setStreet(String street) {
      this.street = street;
    }

    public void setZip(int zip) {
      this.zip = zip;
    }
  }

  public void shouldMapFieldReferencesFromPropertyMap() {
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    modelMapper.addMappings(new PropertyMap<Order, OrderDTO>() {
      @Override
      protected void configure() {
        Converter<String, Integer> c = new AbstractConverter<String, Integer>() {
          @Override
          protected Integer convert(String source) {
            return Integer.valueOf(source);
          }
        };

        Provider<?> p = new AbstractProvider<Object>() {
          @Override
          protected Object get() {
            return null;
          }
        };

        // Various mappings with fields
        using(c).map(source.getCustomer().sId).customer.setId(0);
        when(Conditions.isNotNull()).map().getCustomer().address.setStreet(source.customer.getAddress().streetName);
        when(Conditions.isNotNull()).with(p).map(source.customer.getAddress().sZip,
            destination.getCustomer().address.zip);
        map("d1", destination.customer.d1);
        map().getCustomer().setD2("d2");
      }
    });

    Order order = new Order();
    order.customer = new Customer();
    order.customer.sId = "123";
    order.customer.address = new Address();
    order.customer.address.streetName = "1234 main street";
    order.customer.address.sZip = "92222";

    OrderDTO dto = modelMapper.map(order, OrderDTO.class);
    assertEquals(dto.customer.id + "", order.customer.sId);
    assertEquals(dto.customer.address.street, order.customer.address.streetName);
    assertEquals(dto.customer.address.zip + "", order.customer.address.sZip);
    assertEquals(dto.customer.d1, "d1");
    assertEquals(dto.customer.d2, "d2");
  }

  static class Source {
    String value;
  }

  static class D1 {
    D3 d;

    D3 getD() {
      return d;
    }
  }

  static class D2 {
    String value;

    void setValue(String value) {
      this.value = value;
    }
  }

  static class D3 extends D2 {
  }

  public void shouldMapFieldsWithSubclass() {
    modelMapper.addMappings(new PropertyMap<Source, D1>() {
      @Override
      protected void configure() {
        map().getD().setValue(source.value);
      }
    });

    Source source = new Source();
    source.value = "test";

    D1 dest = modelMapper.map(source, D1.class);
    assertEquals(dest.d.value, source.value);
  }
}
