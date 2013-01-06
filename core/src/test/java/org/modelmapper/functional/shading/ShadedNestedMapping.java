package org.modelmapper.functional.shading;

import static org.testng.Assert.assertNull;

import org.modelmapper.AbstractTest;
import org.modelmapper.convention.MatchingStrategies;
import org.testng.annotations.Test;

@Test(groups = "functional")
public class ShadedNestedMapping extends AbstractTest {
  static class A {
    B b;
  }

  static class B {
    C c;
  }

  static class C {
    D d;
  }

  static class D {
    String value;
  }

  static class DestOne {
    DestTwo two;
  }

  static class DestTwo {
    DestThree three;
  }

  static class DestThree {
    String value;
  }

  public void shouldShadeNullValue() {
    A a = new A();
    a.b = new B();

    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    DestOne dest = modelMapper.map(a, DestOne.class);
    assertNull(dest.two);
  }
}
