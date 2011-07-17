package org.modelmapper.functional.disambiguation;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.convention.MatchingStrategies;
import org.testng.annotations.Test;

/**
 * Tests that disambiguation is performed as expected by the TypeMapFactory.
 * 
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class DisambiguationTest2 extends AbstractTest {
  static class S1 {
    String string = "a";
    String secondString = "b";
  }

  static class S2 {
    String secondString = "b";
    String string = "a";
  }

  static class D1 {
    String string;
    String secondString;
  }

  static class D2 {
    String secondString;
    String string;
  }

  /**
   * Tests with members in various order.
   */
  public void shouldDisambiguate() {
    D1 d1 = modelMapper.map(new S1(), D1.class);
    modelMapper.validate();
    assertEquals(d1.string, "a");
    assertEquals(d1.secondString, "b");

    d1 = modelMapper.map(new S2(), D1.class);
    modelMapper.validate();
    assertEquals(d1.string, "a");
    assertEquals(d1.secondString, "b");

    D2 d2 = modelMapper.map(new S1(), D2.class);
    modelMapper.validate();
    assertEquals(d2.string, "a");
    assertEquals(d2.secondString, "b");

    d2 = modelMapper.map(new S2(), D2.class);
    modelMapper.validate();
    assertEquals(d2.string, "a");
    assertEquals(d2.secondString, "b");
  }

  public void shouldDisambiguateWithLooseMatchingStrategy() {
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    shouldDisambiguate();
  }
}
