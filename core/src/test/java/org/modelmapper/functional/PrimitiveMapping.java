package org.modelmapper.functional;

import static org.testng.Assert.assertEquals;

import java.util.Date;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class PrimitiveMapping extends AbstractTest {
  static class A {
    int i;
    boolean b;
    long l;
    char c;
    long d;
  }

  static class B {
    Integer i;
    Boolean b;
    Long l;
    Character c;
    Date d;
  }

  public void shouldMapPrimitivesToPrimitives() {
    A a = new A();
    a.i = 5;
    a.b = true;
    a.l = 10;
    a.c = 'c';
    a.d = new Date().getTime();
    A d = modelMapper.map(a, A.class);

    modelMapper.validate();
    assertEquals(a.i, d.i);
    assertEquals(a.b, d.b);
    assertEquals(a.l, d.l);
    assertEquals(a.c, d.c);
    assertEquals(a.d, d.d);
  }

  public void shouldMapWrappersToPrimitives() {
    B b = new B();
    b.i = 5;
    b.b = true;
    b.l = 10L;
    b.c = 'c';
    b.d = new Date();
    A a = modelMapper.map(b, A.class);

    modelMapper.validate();
    assertEquals(b.i, (Object) a.i);
    assertEquals(b.b, (Object) a.b);
    assertEquals(b.l, (Object) a.l);
    assertEquals(b.c, (Object) a.c);
    assertEquals(b.d.getTime(), (Object) a.d);
  }

  public void shouldMapPrimitivesToWrapper() {
    A a = new A();
    a.i = 5;
    a.b = true;
    a.l = 10;
    a.c = 'c';
    a.d = new Date().getTime();
    B b = modelMapper.map(a, B.class);

    modelMapper.validate();
    assertEquals((Object) a.i, b.i);
    assertEquals((Object) a.b, b.b);
    assertEquals((Object) a.l, b.l);
    assertEquals((Object) a.c, b.c);
    assertEquals((Object) a.d, b.d.getTime());
  }
}
