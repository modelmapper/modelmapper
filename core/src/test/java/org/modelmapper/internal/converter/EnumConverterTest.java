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

  public void testConvert() {
    assertEquals(convert(Source.a), Dest.a);
    assertNull(convert(Source.b));
    assertEquals(convert(Source.c), Dest.c);
  }

  public void testMatches() {
    assertEquals(converter.match(Source.class, Dest.class), MatchResult.SOURCE_AND_DEST);
    
    // Negative
    assertEquals(converter.match(Source.class, Map.class), MatchResult.NO_MATCH);
    assertEquals(converter.match(Map.class, Dest.class), MatchResult.NO_MATCH);
  }
}
