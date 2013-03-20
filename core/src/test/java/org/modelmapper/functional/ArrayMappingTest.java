package org.modelmapper.functional;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class ArrayMappingTest extends AbstractTest {
  static class Source {
    int[] array;
  }

  static class Dest {
    String[] array;
  }

  public void shouldMapNullArrayWithDefaultValues() {
    modelMapper.map(new Source(), Dest.class);
  }
}
