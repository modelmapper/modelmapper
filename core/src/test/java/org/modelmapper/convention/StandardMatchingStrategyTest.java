package org.modelmapper.convention;

import org.testng.annotations.Test;

/**
 * Tests the {@link StandardMatchingStrategy} against various source and destination hierarchy
 * scenarios.
 * 
 * @author Jonathan Halterman
 */
@Test
public class StandardMatchingStrategyTest extends InexactMatchingStrategyTest {
  public StandardMatchingStrategyTest() {
    super(new StandardMatchingStrategy());
  }

  /**
   * <pre>
   * a/b/d <> a/b/c/d
   * </pre>
   */
  public void shouldNotMatchMissingSourceToken() {
    match("a", "b", "d").to("a", "b", "c", "d").assertNoMatch();
  }

  /**
   * <pre>
   * a/b/c <> aC
   * a/b/c <> a/C
   * </pre>
   */
  public void shouldNotMatchMissingDestinationToken() {
    match("a", "b", "c").to("aC").assertNoMatch();
    match("a", "b", "c").to("a", "c").assertNoMatch();
  }

  /**
   * <pre>
   * a/bc/d -> a/c/bd
   * order/cust/addr/value -> order/addr/cust/value
   * </pre>
   */
  public void shouldMatchUnorderedSourceTokens() {
    match("a", "bC", "d").to("a", "c", "bD").assertMatch();
    match("order", "cust", "addr", "value").to("order", "addr", "cust", "value").assertMatch();
  }
}
