package org.modelmapper.bugs;

import static org.testng.Assert.assertNotNull;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

@Test
public class GH521 extends AbstractTest {

  public void shouldMap() {
    B dest = modelMapper.map(new A(), B.class);
    assertNotNull(dest.x.y);
  }

  private static class A {
    String value = "a";
    AA x = new AA();
  }

  private static class AA {
    String value = "aa";
    AAA y = new AAA();
  }

  private static class AAA {
    String value = null;
  }

  private static class B {
    String value;
    BB x;
  }

  private static class BB {
    String value;
    BBB y;
  }

  private static class BBB {
    String value;
  }
}
