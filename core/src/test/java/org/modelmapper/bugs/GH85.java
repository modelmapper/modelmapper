package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * Add support for fluent setters on destinations.
 */
@Test
public class GH85 extends AbstractTest {
  static class Source {
    private String foo;

    String getFoo() {
      return foo;
    }
  }

  static class Dest {
    private String foo;

    Dest setFoo(String foo) {
      this.foo = foo;
      return this;
    }
  }

  public void shouldMapToDestinationWithFluentSetters() {
    Source source = new Source();
    source.foo = "test";
    Dest dest = modelMapper.map(source, Dest.class);
    assertEquals(dest.foo, source.foo);
  }
}
