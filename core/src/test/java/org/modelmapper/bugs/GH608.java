package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractConverter;
import org.modelmapper.AbstractTest;
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
  private SourceGetter<Location> self = source -> source;

  @Test
  public void testOrder1() {
    modelMapper.getConfiguration().setAmbiguityIgnored(true);
    modelMapper.createTypeMap(Location.class, Address.class)
        .addMappings(mapping -> {
          mapping.map(Location::getStreetName, Address::setStreet);
          mapping.using(converter).map(self, Address::setHouseNumber);
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
        .addMappings(mapping -> {
          mapping.using(converter).map(self, Address::setHouseNumber);
          mapping.map(Location::getStreetName, Address::setStreet);
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
