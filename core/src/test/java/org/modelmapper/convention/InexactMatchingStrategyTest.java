package org.modelmapper.convention;

import org.modelmapper.internal.MatchingStrategyTestSupport;
import org.modelmapper.spi.MatchingStrategy;
import org.testng.annotations.Test;

/**
 * Common tests for inexact matching strategies.
 * 
 * @author Jonathan Halterman
 */
@Test
public class InexactMatchingStrategyTest extends MatchingStrategyTestSupport {
  static class Address {
  }

  static class Artist {
  }

  static class Customer {
  }

  static class Name {
  }

  public InexactMatchingStrategyTest() {
    super(new StandardMatchingStrategy());
  }

  protected InexactMatchingStrategyTest(MatchingStrategy matchingStrategy) {
    super(matchingStrategy);
  }

  /**
   * <pre>
   * Address home -> addressHome
   * </pre>
   */
  public void shouldMatchClass() {
    match(Address.class, "home").to("addressHome").assertMatch();
  }

  /**
   * <pre>
   * Address/Integer id -> addressId
   * </pre>
   */
  public void shouldMatchDeclaringClass() {
    match(Address.class, Integer.TYPE, "id").to("addressId").assertMatch();
  }

  /**
   * <pre>
   * String name -> name
   * Artist artist/String name -> artist/name
   * </pre>
   */
  public void shouldMatchEqualHierarchies() {
    match(String.class, "name").to("name").assertMatch();
    match(Artist.class, "artist").$(String.class, "name").to("artist", "name").assertMatch();
  }

  /**
   * <pre>
   * String artistName -> artist/name
   * String customerAddressStreet -> customer/address/street
   * Address customerAddress/String street -> customer/address/street
   * </pre>
   */
  public void shouldMatchGreaterDestinationHierarchy() {
    match(String.class, "artistName").to("artist", "name").assertMatch();
    match(String.class, "customerAddressStreet").to("customer", "address", "street").assertMatch();
    match(Address.class, "customerAddress").$(String.class, "street")
        .to("customer", "address", "street")
        .assertMatch();
  }

  /**
   * <pre>
   * Artist artist/String name -> artistName
   * Address homeAddress/String street -> homeStreet
   * Customer customer/Address address/String street -> customerAddressStreet
   * Customer customer/Address address/String street -> customerAddress/street
   * </pre>
   */
  public void shouldMatchGreaterSourceHierarchy() {
    match(Artist.class, "artist").$(String.class, "name").to("artistName").assertMatch();
    match(Address.class, "homeAddress").$(String.class, "street").to("homeStreet").assertMatch();
    match(Customer.class, "customer").$(Address.class, "address")
        .$(String.class, "street")
        .to("customerAddressStreet")
        .assertMatch();
    match(Customer.class, "customer").$(Address.class, "address")
        .$(String.class, "street")
        .to("customerAddress", "street")
        .assertMatch();
  }

  /**
   * <pre>
   * String addressStreet -> streetAddress;
   * Address address/Street street -> streetAddress
   * Customer customer/Name name/String first -> customerFirstName
   * Customer customer/Name name/String first -> customer/firstName
   * </pre>
   */
  public void shouldMatchInverted() {
    match(String.class, "addressStreet").to("streetAddress").assertMatch();
    match(Address.class, "address").$(String.class, "street").to("streetAddress").assertMatch();
    match(Customer.class, "customer").$(Name.class, "name")
        .$(String.class, "first")
        .to("customerFirstName")
        .assertMatch();
    match(Customer.class, "customer").$(Name.class, "name")
        .$(String.class, "first")
        .to("customer", "firstName")
        .assertMatch();
  }

  /**
   * <pre>
   * Address home -> homeAddress
   * Address home/String street -> homeAddressStreet
   * Address home/String street -> homeStreetAddress
   * Address home/String street -> homeAddress/street
   * </pre>
   */
  public void shouldMatchInvertedClass() {
    match(Address.class, "home").$(String.class, "street").to("homeAddressStreet");
    match(Address.class, "home").$(String.class, "street").to("homeStreetAddress");
    match(Address.class, "home").$(String.class, "street")
        .to("homeAddress", "street")
        .assertMatch();
  }

  /**
   * <pre>
   * Address/String home -> homeAddress
   * Address/String home -> home/address
   * </pre>
   */
  public void shouldMatchInvertedDeclaringClass() {
    match(Address.class, String.class, "home").to("homeAddress").assertMatch();
    match(Address.class, String.class, "home").to("home", "address").assertMatch();
  }

  /**
   * <pre>
   * Address address -> addressAddress
   * Address address -> address/address
   * Address address/String address -> address
   * Address address/String address -> addressAddress
   * Address address/Object address/String foo -> addressFoo
   * </pre>
   */
  public void shouldMatchSameTokenTwice() {
    match(Address.class, "address").to("addressAddress").assertMatch();
    match(Address.class, "address").to("address", "address").assertMatch();
    match(Address.class, "address").$(String.class, "address").to("address").assertMatch();
    match(Object.class, "address").$(String.class, "address").to("address").assertMatch();
    match(Address.class, "address").$(String.class, "address").to("addressAddress").assertMatch();
    match(Address.class, "address").$(Object.class, "address")
        .$(String.class, "foo")
        .to("addressFoo")
        .assertMatch();
  }

  /**
   * <pre>
   * a/c -> 
   * </pre>
   */
  public void shouldMatchSplitTokens() {
    match(Address.class, "address").to("addressAddress").assertMatch();
  }

  /**
   * <pre>
   * Address address matches address
   * </pre>
   */
  public void shouldMatchWithSameNameAndClassName() {
    match(Address.class, "address").to("address").assertMatch();
  }

  /**
   * <pre>
   * Address value <> address
   * </pre>
   */
  public void shouldNotMatchClassAlone() {
    match(String.class, "value").to("address").assertNoMatch();
  }

  /**
   * <pre>
   * abc -> a/B/C
   * aa/bb/cc -> aabb
   * street/address -> streetaddress
   * streetaddress -> street/address
   * </pre>
   */
  public void shouldMatchCombinedTokens() {
    match("aabbcc").to("aaBbCc").assertMatch();
    match("ooAaBbCc").to("aabb").assertMatch();
    match("streetAddress").to("streetaddress").assertMatch();
    match("streetaddress").to("streetAddress").assertMatch();
  }
}
