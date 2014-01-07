package org.modelmapper.functional.converter;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class ExistingDestinationTest extends AbstractTest {
  static class Source {
    Foo foo;
  }

  static class Foo {
    String value;
  }

  static class Dest {
    String value;
  }

  public void shouldConvertWithExistingDestination() {
    modelMapper.addConverter(new Converter<Source, Dest>() {
      public Dest convert(MappingContext<Source, Dest> context) {
        Dest dest = new Dest();
        context.getMappingEngine().map(context.create(context.getSource().foo, dest));
        return dest;
      }
    });

    Source source = new Source();
    source.foo = new Foo();
    source.foo.value = "test";
    Dest dest = modelMapper.map(source, Dest.class);
    assertEquals(dest.value, "test");
  }
}
