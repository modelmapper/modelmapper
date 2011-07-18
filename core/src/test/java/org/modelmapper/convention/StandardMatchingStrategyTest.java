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
   * a/b/c <> ac
   * a/b/c <> a/c
   * </pre>
   */
  public void shouldNotMatchMissingDestinationToken() {
    match("a", "b", "c").to("aC").assertNoMatch();
    match("a", "b", "c").to("a", "c").assertNoMatch();
  }
}
