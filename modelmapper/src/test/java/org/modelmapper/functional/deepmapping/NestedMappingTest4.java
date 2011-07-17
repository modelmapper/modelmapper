package org.modelmapper.functional.deepmapping;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import org.modelmapper.AbstractTest;
import org.modelmapper.Asserts;
import org.modelmapper.ConfigurationException;
import org.modelmapper.convention.MatchingStrategies;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class NestedMappingTest4 extends AbstractTest {
  private static class Address {
    String street;
    String city;
  }

  private static class User1 {
    Address address;
  }

  private static class User2 {
    Address billingAddress;
    Address mailingAddress;
  }

  private static class UserDTO1 {
    String street;
    String city;
  }

  private static class UserDTO2 {
    String billingStreet;
    String billingCity;
    String mailingStreet;
    String mailingCity;
  }

  /**
   * Address address/String street -> street Address address/String city -> city
   */
  public void shouldMapUser1ToUserDTO1() {
    User1 user = new User1();
    user.address = new Address();
    user.address.street = "1234 main street";
    user.address.city = "boston";

    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    UserDTO1 dto = modelMapper.map(user, UserDTO1.class);
    modelMapper.validate();

    assertEquals(dto.street, user.address.street);
    assertEquals(dto.city, user.address.city);
  }

  public void shouldMapUser2ToUserDTO2() {
    User2 user = new User2();
    user.billingAddress = new Address();
    user.billingAddress.street = "1234 main street";
    user.billingAddress.city = "boston";
    user.mailingAddress = new Address();
    user.mailingAddress.street = "4543 12th street";
    user.mailingAddress.city = "new york";

    UserDTO2 dto = modelMapper.map(user, UserDTO2.class);
    modelMapper.validate();

    assertEquals(dto.mailingStreet, user.mailingAddress.street);
    assertEquals(dto.mailingCity, user.mailingAddress.city);
    assertEquals(dto.billingStreet, user.billingAddress.street);
    assertEquals(dto.billingCity, user.billingAddress.city);
  }

  /**
   * Maps User1/address/street from UserDTO1/street <br>
   * Maps User1/address/city from UserDTO1/city <br>
   */
  public void shouldMapUserDTO1ToUser1() {
    UserDTO1 dto = new UserDTO1();
    dto.street = "1234 main street";
    dto.city = "boston";

    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    User1 user = modelMapper.map(dto, User1.class);
    modelMapper.validate();

    assertEquals(user.address.street, dto.street);
    assertEquals(user.address.city, dto.city);
  }

  /**
   * <pre>
   * String billingStreet -> billingAddress/street
   * String billingCity -> billingAddress/city
   * String mailingStreet -> mailingAddress/street
   * String mailingCity -> mailingAddress/city
   * </pre>
   * 
   * Requires disambiguation.
   */
  public void shouldMapUserDTO2ToUser2() {
    UserDTO2 dto = new UserDTO2();
    dto.mailingStreet = "1234 main street";
    dto.mailingCity = "boston";
    dto.billingStreet = "4543 12th street";
    dto.billingCity = "new york";

    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    User2 user = modelMapper.map(dto, User2.class);
    modelMapper.validate();

    assertEquals(user.mailingAddress.street, dto.mailingStreet);
    assertEquals(user.mailingAddress.city, dto.mailingCity);
    assertEquals(user.billingAddress.street, dto.billingStreet);
    assertEquals(user.billingAddress.city, dto.billingCity);
  }

  public void shouldNotMapUser2ToUserDTO1() {
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    
    try {
      modelMapper.map(new User2(), UserDTO1.class);
      modelMapper.validate();
    } catch (ConfigurationException e) {
      assertEquals(e.getErrorMessages().size(), 2);
      Asserts.assertContains(e.getMessage(), "street matches multiple");
      Asserts.assertContains(e.getMessage(), "city matches multiple");
      return;
    }

    fail();
  }

  public void shouldNotMapUserDTO1ToUser2() {
    UserDTO1 dto = new UserDTO1();
    dto.city = "cityname";
    dto.street = "1234 main street";

    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    User2 user = modelMapper.map(dto, User2.class);
    modelMapper.validate();

    assertEquals(dto.city, user.billingAddress.city);
    assertEquals(dto.street, user.billingAddress.street);
    assertEquals(dto.city, user.mailingAddress.city);
    assertEquals(dto.street, user.mailingAddress.street);
  }
}
