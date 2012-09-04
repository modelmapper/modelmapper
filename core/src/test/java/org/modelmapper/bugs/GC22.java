package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * http://code.google.com/p/modelmapper/issues/detail?id=22
 */
@Test
public class GC22 extends AbstractTest {
  static class PersonDTO {
    String addressCity;
    StateDTO addressState;

    public StateDTO getAddressState() {
      return addressState;
    }
  }

  static class StateDTO {
    public String getCode() {
      return code;
    }

    String code;
    String desc;
  }

  static class Person {
    Address address;

    public Address getAddress() {
      return address;
    }
  }

  static class Address {
    String city;
    String state;
    String asdfasdf;

    public void setState(String state) {
      this.state = state;
    }
  }

  public void test() {
    modelMapper.addMappings(new PropertyMap<PersonDTO, Person>() {
      @Override
      protected void configure() {
        map().getAddress().setState(source.getAddressState().getCode());
      }
    });

    PersonDTO dto = new PersonDTO();
    dto.addressCity = "San Francisco";
    dto.addressState = new StateDTO();
    dto.addressState.code = "CA";
    Person p = modelMapper.map(dto, Person.class);

    assertEquals(p.address.city, "San Francisco");
    assertEquals(p.address.state, "CA");
  }
}
