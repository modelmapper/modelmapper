package org.modelmapper.internal.converter;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.Map;

import org.modelmapper.spi.ConditionalConverter.MatchResult;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class EnumConverterTest extends AbstractConverterTest {
  public EnumConverterTest() {
    super(new EnumConverter(), Dest.class);
  }

  enum Source {
    a, b, c
  }

  enum Dest {
    a, B, c
  }

  static class DTO {
    String dest;
  }

  public void testConvert() {
    assertEquals(convert(Source.a), Dest.a);
    assertNull(convert(Source.b));
    assertEquals(convert(Source.c), Dest.c);
  }

  public void testMatches() {
    assertEquals(converter.match(Source.class, Dest.class), MatchResult.FULL);

    // Negative
    assertEquals(converter.match(Source.class, Map.class), MatchResult.NONE);
    assertEquals(converter.match(Map.class, Dest.class), MatchResult.NONE);
  }

  public void shouldConvertFromStringToEnum() {
    assertEquals(convert("a"), Dest.a);
  }
}
