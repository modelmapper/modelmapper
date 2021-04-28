package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractConverter;
import org.modelmapper.AbstractTest;
import org.modelmapper.ExpressionMap;
import org.modelmapper.builder.ConfigurableConditionExpression;
import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.spi.SourceGetter;
import org.testng.annotations.Test;

@Test
public class GH608 extends AbstractTest {

  private AbstractConverter<Location, String> converter = new AbstractConverter<Location, String>() {
    @Override
    protected String convert(Location source) {
      return (source.getStreetNumber() == null ? "" : source.getStreetNumber())
          + (source.getStreetNumberSuffix() == null ? "" : source.getStreetNumberSuffix());
    }
  };
  private SourceGetter<Location> self = new SourceGetter<Location>() {
    @Override
    public Object get(Location source) {
      return source;
    }
  };
  private SourceGetter<Location> getStreetName = new SourceGetter<Location>() {
    @Override
    public Object get(Location source) {
      return source.getStreetName();
    }
  };
  private DestinationSetter<Address, String> setStreet = new DestinationSetter<Address, String>() {
    @Override
    public void accept(Address destination, String value) {
      destination.setStreet(value);
    }
  };
  private DestinationSetter<Address, String> setHouseNumber = new DestinationSetter<Address, String>() {
    @Override
    public void accept(Address destination, String value) {
      destination.setHouseNumber(value);
    }
  };

  @Test
  public void testOrder1() {
    modelMapper.getConfiguration().setAmbiguityIgnored(true);
    modelMapper.createTypeMap(Location.class, Address.class)
        .addMappings(new ExpressionMap<Location, Address>() {
          @Override
          public void configure(ConfigurableConditionExpression<Location, Address> mapping) {
            mapping.map(getStreetName, setStreet);
            mapping.using(converter).map(self, setHouseNumber);
          }
        });

    Location location = new Location("Example Street", "123", "d");
    Address address = new Address();

    modelMapper.map(location, address);

    assertEquals(address.toString(), "Address{street='Example Street', houseNumber='123d'}");
  }

  @Test
  public void testOrder2() {
    modelMapper.getConfiguration().setAmbiguityIgnored(true);

    modelMapper.createTypeMap(Location.class, Address.class)
        .addMappings(new ExpressionMap<Location, Address>() {
          @Override
          public void configure(ConfigurableConditionExpression<Location, Address> mapping) {
            mapping.using(converter).map(self, setHouseNumber);
            mapping.map(getStreetName, setStreet);
          }
        });

    Location location = new Location("Example Street", "123", "d");
    Address address = new Address();

    modelMapper.map(location, address);

    assertEquals(address.toString(), "Address{street='Example Street', houseNumber='123d'}");
  }

  private static class Address {

    private String street;
    private String houseNumber;

    public String getStreet() {
      return street;
    }

    public void setStreet(String street) {
      this.street = street;
    }

    public String getHouseNumber() {
      return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
      this.houseNumber = houseNumber;
    }

    @Override
    public String toString() {
      return "Address{" +
          "street='" + street + '\'' +
          ", houseNumber='" + houseNumber + '\'' +
          '}';
    }
  }

  private static class Location {

    private String streetName;
    private String streetNumber;
    private String streetNumberSuffix;

    public Location(String streetName, String streetNumber, String streetNumberSuffix) {
      this.streetName = streetName;
      this.streetNumber = streetNumber;
      this.streetNumberSuffix = streetNumberSuffix;
    }

    public String getStreetName() {
      return streetName;
    }

    public void setStreetName(String streetName) {
      this.streetName = streetName;
    }

    public String getStreetNumber() {
      return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
      this.streetNumber = streetNumber;
    }

    public String getStreetNumberSuffix() {
      return streetNumberSuffix;
    }

    public void setStreetNumberSuffix(String streetNumberSuffix) {
      this.streetNumberSuffix = streetNumberSuffix;
    }

    @Override
    public String toString() {
      return "Location{" +
          "streetName='" + streetName + '\'' +
          ", streetNumber='" + streetNumber + '\'' +
          ", streetNumberSuffix='" + streetNumberSuffix + '\'' +
          '}';
    }
  }

}
