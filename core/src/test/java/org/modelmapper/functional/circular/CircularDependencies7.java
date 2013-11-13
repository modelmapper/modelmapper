package org.modelmapper.functional.circular;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class CircularDependencies7 extends AbstractTest {
  static class A {
    A a;
  }

  static class DA {
    DA a;
  }

  public void shouldMap() {
    A a = new A();
    a.a = a;
    DA da = modelMapper.map(a, DA.class);

    assertEquals(da.a, da);
  }
}
