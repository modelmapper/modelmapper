package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

@Test
public class GH109 extends AbstractTest {
  public static class Source {
    Integer value;

    public Integer getFoo() {
      return value;
    }
  }

  public static class Destination {
    int value;

    public void setBar(int value) {
      this.value = value;
    }
  }

  @Test
  public void testModelMapperIntegerToInt() throws Exception {
    modelMapper.addMappings(new PropertyMap<Source, Destination>() {
      @Override
      protected void configure() {
        map().setBar(source.getFoo());
      }
    });

    Source s = new Source();
    s.value = Integer.valueOf(42);
    Destination dest = modelMapper.map(s, Destination.class);
    assertEquals(42, dest.value);
  }
}
