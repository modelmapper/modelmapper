package org.modelmapper.convention;

import org.modelmapper.internal.MatchingStrategyTestSupport;
import org.testng.annotations.Test;

/**
 * Tests the {@link StrictMatchingStrategy} against various source and destination hierarchy
 * scenarios.
 * 
 * @author Jonathan Halterman
 */
@Test
public class StrictMatchingStrategyTest extends MatchingStrategyTestSupport {
  public StrictMatchingStrategyTest() {
    super(new StrictMatchingStrategy());
  }

  public void shouldMatchExactTokensWithDuplicates() {
    match("address").$("address").to("address").assertNoMatch();
    match("address").$("address").assertMatch();
  }

  public void shouldMatchExactTokens() {
    match("address").$("street").to("street").assertNoMatch();
    match("address").$("street").to("addressStreet").assertNoMatch();
    match("address", "street").to("address", "street").assertMatch();
    match("address", "streetName").to("address", "streetName").assertMatch();
    match("order", "cust", "addr", "value").to("order", "cust", "value").assertNoMatch();
    match("order", "cust", "addr", "value").to("order", "cust", "addr", "value").assertMatch();
  }
}
