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
    a,
    b,
    c,
    d {
      @Override
      public boolean isD() {
        return true;
      }
    };

    public boolean isD() {
      return false;
    }
  }

  enum Dest {
    a, B, c, d
  }

  static class DTO {
    String dest;
  }

  public void testConvert() {
    assertEquals(convert(Source.a), Dest.a);
    assertNull(convert(Source.b));
    assertEquals(convert(Source.c), Dest.c);
    assertEquals(convert(Source.d), Dest.d);
  }

  public void testMatches() {
    assertEquals(converter.match(Source.class, Dest.class), MatchResult.FULL);
    assertEquals(converter.match(Source.d.getClass(), Dest.d.getClass()), MatchResult.FULL);

    // Negative
    assertEquals(converter.match(Source.class, Map.class), MatchResult.NONE);
    assertEquals(converter.match(Map.class, Dest.class), MatchResult.NONE);
  }

  public void shouldConvertFromStringToEnum() {
    assertEquals(convert("a"), Dest.a);
  }
}
