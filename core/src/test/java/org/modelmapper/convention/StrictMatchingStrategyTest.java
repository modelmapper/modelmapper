package org.modelmapper.convention;

import org.modelmapper.internal.MatchingStrategyTestSupport;
import org.testng.annotations.Test;

/**
 * Tests the {@link StandardMatchingStrategy} against various source and destination hierarchy
 * scenarios.
 * 
 * @author Jonathan Halterman
 */
@Test
public class StrictMatchingStrategyTest extends MatchingStrategyTestSupport {
  public StrictMatchingStrategyTest() {
    super(new StrictMatchingStrategy());
  }

}
