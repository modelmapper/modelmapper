package org.modelmapper.functional.disambiguation;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * Tests the scenario where a name match is made but the source type is not verifiable by the
 * matching converter.
 * 
 * @author Jonathan Halterman
 */
@Test
public class SameNames extends AbstractTest {
  static class Date {
    int month = 12;
    int date = 5;
    int year = 2005;
  }

  static class A {
    Date date = new Date();
  }

  static class B {
    int month;
    int date = 7;
    int year;
  }

  public void shouldMapWithUnverifiedSource() {
    A a = new A();
    B b = modelMapper.map(a, B.class);
    assertEquals(b.date, 5);
  }

  public void shouldMapWithUnverifiedDest() {
    B b = new B();
    A a = modelMapper.map(b, A.class);
    assertEquals(a.date.date, 7);
  }
}
