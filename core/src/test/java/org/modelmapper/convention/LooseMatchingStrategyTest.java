package org.modelmapper.convention;

import org.testng.annotations.Test;

/**
 * Tests the {@link LooseMatchingStrategy} against various source and destination hierarchy
 * scenarios.
 * 
 * @author Jonathan Halterman
 */
@Test
public class LooseMatchingStrategyTest extends InexactMatchingStrategyTest {
  public LooseMatchingStrategyTest() {
    super(new LooseMatchingStrategy());
  }

  /**
   * <pre>
   * Address address/String address -> address
   * </pre>
   */
  public void shouldMatchSameTokenAgainstMultipleSourceMembers() {
    match(Address.class, "address").$(String.class, "address").to("address").assertMatch();
  }

  /**
   * <pre>
   * Address address/String city -> city
   * Address address/String city -> address/city
   * </pre>
   */
  public void shouldMatchLastSourceMemberName() {
    match(Address.class, "address").$(String.class, "city").to("city").assertMatch();
    match(Address.class, "address").$(String.class, "city").to("address", "city").assertMatch();
  }

  /**
   * <pre>
   * Address address/String city <> address
   * </pre>
   */
  public void shouldNotMatchWithMismatchedLastToken() {
    match(Address.class, "address").$(String.class, "city").to("address").assertNoMatch();
  }
}
