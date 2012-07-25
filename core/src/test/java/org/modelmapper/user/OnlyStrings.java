package org.modelmapper.user;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.Conditions;
import org.testng.annotations.Test;

/**
 * A test that maps only string types. From: <a href=
 * "http://stackoverflow.com/questions/10862726/copy-only-string-properties-from-one-object-to-another"
 * >lnk</a>
 */
@Test
public class OnlyStrings extends AbstractTest {
  static class Source {
    int a;
    boolean b;
    String s1;
  }

  static class Destination {
    int a;
    boolean b;
    String s1;
  }

  public void shouldOnlyCopyStrings() {
    modelMapper.createTypeMap(Source.class, Destination.class).setPropertyCondition(
        Conditions.isType(String.class));

    Source source = new Source();
    source.a = 5;
    source.b = true;
    source.s1 = "test";
    Destination d = modelMapper.map(source, Destination.class);

    assertEquals(d.a, 0);
    assertEquals(d.b, false);
    assertEquals(source.s1, d.s1);
  }
}
