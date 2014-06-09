package org.modelmapper.functional;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractConverter;
import org.modelmapper.AbstractProvider;
import org.modelmapper.Conditions;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.Provider;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests the mapping of fields using a PropertyMap.
 * 
 * @author Jonathan Halterman
 */
@Test
public class FieldMappingTest {
  ModelMapper modelMapper;
  Order order;

  @BeforeMethod
  protected void beforeMethod() {
    modelMapper = new ModelMapper();
    modelMapper.getConfiguration()
        .setFieldMatchingEnabled(true)
        .setFieldAccessLevel(AccessLevel.PACKAGE_PRIVATE)
        .setMethodAccessLevel(AccessLevel.PACKAGE_PRIVATE)
        .setMatchingStrategy(MatchingStrategies.STRICT);

    order = new Order();
    order.customer = new Customer();
    order.customer.sId = "123";
    order.customer.s3 = "s3";
    order.customer.address = new Address();
    order.customer.address.streetName = "1234 main street";
    order.customer.address.sZip = "92222";
  }

  static class Order {
    Customer customer;

    public Customer getCustomer() {
      return customer;
    }
  }

  static class Customer {
    String sId;
    String s1;
    String s3;
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
    String d3;
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
        map(source.customer.s3, destination.customer.d3);
      }
    });

    OrderDTO dto = modelMapper.map(order, OrderDTO.class);
    assertOrdersEqual(dto);
  }

  private void assertOrdersEqual(OrderDTO dto) {
    assertEquals(dto.customer.id + "", order.customer.sId);
    assertEquals(dto.customer.address.street, order.customer.address.streetName);
    assertEquals(dto.customer.address.zip + "", order.customer.address.sZip);
    assertEquals(dto.customer.d1, "d1");
    assertEquals(dto.customer.d2, "d2");
    assertEquals(dto.customer.d3, order.customer.s3);
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

  public void shouldMapConstantToDestinationField() {
    modelMapper.addMappings(new PropertyMap<Address, AddressDTO>() {
      @Override
      protected void configure() {
        map("1234 Main Street", destination.street);
        map(source.sZip, destination.zip);
      }
    });

    Address address = new Address();
    address.sZip = "92123";
    AddressDTO dto = modelMapper.map(address, AddressDTO.class);
    assertEquals(dto.street, "1234 Main Street");
    assertEquals(dto.zip + "", address.sZip);
  }
}
