package org.modelmapper.functional.deepmapping;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
@SuppressWarnings("unused")
public class NestedMappingTest5  extends AbstractTest {
  private static class Address {
    int id;
    String street;
    String city;
  }

  private static class AddressDTO {
    String id;
    String city;
  }

  private static class Customer {
    int id;
    Address homeAddress;
    Address mailingAddress;
  }

  private static class Order {
    int id;
    Customer customer;
    Shipper shipper;
    Address companyAddress;
  }

  private static class OrderDTO1 {
    String id;
    int customerId;
    String homeStreet;
    String mailingCity;
  }

  private static class OrderDTO2 {
    String id;
    String shipperStreet;
    String mailingCity;
  }

  private static class OrderDTO3 {
    AddressDTO homeAddress;
  }

  private static class Shipper {
    Address address;
  }

  /**
   * Order/id to OrderDTO1/id Order/customer/id to OrderDTO1/customerId homeStreet is ambiguous
   * mailingCity is ambiguous
   */
  public void shouldMapOrderToOrderDTO1() {

  }
}
