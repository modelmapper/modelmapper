package org.modelmapper.functional.converter;

import org.modelmapper.AbstractTest;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author kurdyukovpv
 */
@Test(groups = "functional")
public class ConvertNestedObjectTest extends AbstractTest {

  static class AuthorDTO {
    String name;
    AddressDTO address;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public AddressDTO getAddress() {
      return address;
    }

    public void setAddressDTO(AddressDTO address) {
      this.address = address;
    }
  }

  static class LibraryDTO {
    String name;
    AddressDTO address;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public AddressDTO getAddress() {
      return address;
    }

    public void setAddress(AddressDTO address) {
      this.address = address;
    }
  }

  static class AddressDTO {
    String street;
    String lat;
    String lon;

    public String getStreet() {
      return street;
    }

    public void setStreet(String street) {
      this.street = street;
    }

    public String getLat() {
      return lat;
    }

    public void setLat(String lat) {
      this.lat = lat;
    }

    public String getLon() {
      return lon;
    }

    public void setLon(String lon) {
      this.lon = lon;
    }
  }

  static class WrapperDTO {
    AuthorDTO author;
    LibraryDTO library;

    public AuthorDTO getAuthor() {
      return author;
    }

    public void setAuthor(AuthorDTO author) {
      this.author = author;
    }

    public LibraryDTO getLibrary() {
      return library;
    }

    public void setLibrary(LibraryDTO library) {
      this.library = library;
    }
  }

  static class Author {
    String name;
    Address address;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Address getAddress() {
      return address;
    }

    public void setAddress(Address address) {
      this.address = address;
    }
  }

  static class Library {
    String name;
    Address address;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Address getAddress() {
      return address;
    }

    public void setAddress(Address address) {
      this.address = address;
    }
  }

  static class Address {
    String street;
    Coordinate coordinate;

    public String getStreet() {
      return street;
    }

    public void setStreet(String street) {
      this.street = street;
    }

    public Coordinate getCoordinate() {
      return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
      this.coordinate = coordinate;
    }
  }

  static class Coordinate {
    long x;
    long y;

    public Coordinate(long x, long y) {
      this.x = x;
      this.y = y;
    }

    public long getX() {
      return x;
    }

    public void setX(long x) {
      this.x = x;
    }

    public long getY() {
      return y;
    }

    public void setY(long y) {
      this.y = y;
    }
  }

  static class Wrapper {
    Author author;
    Library library;

    public Author getAuthor() {
      return author;
    }

    public void setAuthor(Author author) {
      this.author = author;
    }

    public Library getLibrary() {
      return library;
    }

    public void setLibrary(Library library) {
      this.library = library;
    }
  }


  private static Converter<AddressDTO, Coordinate> CONVERTER = new Converter<AddressDTO, Coordinate>() {
    public Coordinate convert(MappingContext<AddressDTO, Coordinate> context) {
      return new Coordinate(Long.valueOf(context.getSource().getLat()), Long.valueOf(context.getSource().getLon()));
    }
  };

  public void convertNested() {
    AddressDTO addressDTO1 = new AddressDTO();
    addressDTO1.setLat("1");
    addressDTO1.setLon("2");
    addressDTO1.setStreet("Main street");

    AddressDTO addressDTO2 = new AddressDTO();
    addressDTO2.setLat("3");
    addressDTO2.setLon("4");
    addressDTO2.setStreet("Some street");

    AuthorDTO authorDTO = new AuthorDTO();
    authorDTO.setName("King");
    authorDTO.setAddressDTO(addressDTO1);

    LibraryDTO libraryDTO = new LibraryDTO();
    libraryDTO.setName("Main");
    libraryDTO.setAddress(addressDTO2);

    WrapperDTO wrapperDTO = new WrapperDTO();
    wrapperDTO.setAuthor(authorDTO);
    wrapperDTO.setLibrary(libraryDTO);

    modelMapper.addMappings(new PropertyMap<AddressDTO, Address>() {
      @Override
      protected void configure() {
        using(CONVERTER).map(source, destination.getCoordinate());
      }
    });

    Wrapper wrapper = modelMapper.map(wrapperDTO, Wrapper.class);

    assertEquals(wrapper.getAuthor().getName(), "King");
    assertEquals(wrapper.getAuthor().getAddress().getCoordinate().getX(), 1L);

    assertEquals(wrapper.getLibrary().getAddress().getStreet(), "Some street");
    assertEquals(wrapper.getLibrary().getAddress().getCoordinate().getY(), 4L);

  }
}