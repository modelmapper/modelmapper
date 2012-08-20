package org.modelmapper.functional.circular;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * Tests the handling of circular references.
 * 
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class CircularDependencies4 extends AbstractTest {
  static class VeryCircular {
    VeryCircular very;
    String value;
  }

  static class DVeryCircular {
    DVeryCircular very;
    String value;
  }
}
