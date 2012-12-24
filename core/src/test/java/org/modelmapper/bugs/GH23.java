package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.convention.MatchingStrategies;
import org.testng.annotations.Test;

/**
 * https://github.com/jhalterman/modelmapper/issues/23
 * 
 * @author Jonathan Halterman
 */
@Test
public class GH23 extends AbstractTest {
  public static class Source {
    String sampleFieldOne;
    Integer sampleField1;
    Integer samplefield1;
  }

  public static class Dest {
    String samplefieldone;
    Integer samplefield1;
  }

  public void testWithStandardMatchingStartegy() {
    mapAndAssert();
  }

  public void testWithLooseMatchingStrategy() {
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    mapAndAssert();
  }

  private void mapAndAssert() {
    Source source = new Source();
    source.sampleField1 = Integer.valueOf(5);
    source.samplefield1 = Integer.valueOf(8);
    source.sampleFieldOne = "test";

    Dest dest = modelMapper.map(source, Dest.class);
    assertEquals(dest.samplefield1, source.samplefield1);
    assertEquals(dest.samplefieldone, source.sampleFieldOne);

    Source source2 = modelMapper.map(dest, Source.class);
    assertEquals(source2.sampleField1, dest.samplefield1);
    assertEquals(source2.sampleFieldOne, dest.samplefieldone);
  }
}
