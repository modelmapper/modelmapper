package org.modelmapper.internal.converter;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.modelmapper.internal.converter.EnumConverter;
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
    assertTrue(converter.supports(Source.class, Dest.class));
    assertFalse(converter.supports(Source.class, Map.class));
    assertFalse(converter.supports(Map.class, Dest.class));
  }
}
