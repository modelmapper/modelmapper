package org.modelmapper.functional.skip;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.modelmapper.AbstractTest;
import org.modelmapper.Conditions;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

@Test
public class SkipMappingTest1 extends AbstractTest {
  static class Source {
    String name = "test";
    String foo = "foo";
  }

  static class Dest {
    String name;
    String bar;
  }

  public void shouldSkipMapping() {
    modelMapper.addMappings(new PropertyMap<Source, Dest>() {
      @Override
      protected void configure() {
        skip(destination.name);
      }
    });

    Dest dest = modelMapper.map(new Source(), Dest.class);
    assertNull(dest.name);
  }

  public void shouldConditionallySkipMapping() {
    modelMapper.addMappings(new PropertyMap<Source, Dest>() {
      @Override
      protected void configure() {
        when(Conditions.isNotNull()).skip(source.foo, destination.bar);
      }
    });

    Dest dest = modelMapper.map(new Source(), Dest.class);
    assertNotNull(dest.name);
    assertNull(dest.bar);
  }
}
