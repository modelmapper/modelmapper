package org.modelmapper.functional;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class EnumConversion extends AbstractTest {
  static class Source {
    String color;
  }

  static class Dest {
    Color color;
  }

  enum Color {
    Red, Blue
  }

  public void shouldConvertFromStringToEnum() {
    Source src = new Source();
    src.color = "Blue";
    assertEquals(modelMapper.map(src, Dest.class).color, Color.Blue);
  }

  public void shouldConvertFromEnumToString() {
    Dest dest = new Dest();
    dest.color = Color.Blue;
    assertEquals(modelMapper.map(dest, Source.class).color, "Blue");
  }
}
