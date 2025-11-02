package org.modelmapper.functional.constructor;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.spi.ConstructorInjector;
import org.testng.annotations.Test;

@Test
public class ConstructorTest extends AbstractTest {
  static class Source {
    String foo;
    Integer bar;
  }

  static class Destination {
    private final String foo;
    private final Integer bar;

    public Destination(final String foo, final Integer bar) {
      this.foo = foo;
      this.bar = bar;
    }
  }

  public void shouldMap() {
    modelMapper.getConfiguration().addConstructorInjector(
        ConstructorInjector.forClass(Destination.class, "foo", "bar"));

    Source source = new Source();
    source.foo = "foo";
    source.bar = 42;

    Destination destination = modelMapper.map(source, Destination.class);
    assertEquals(destination.foo, "foo");
    assertEquals(destination.bar, Integer.valueOf(42));
  }
}
