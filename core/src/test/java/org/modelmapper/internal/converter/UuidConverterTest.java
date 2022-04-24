package org.modelmapper.internal.converter;

import static org.testng.Assert.assertEquals;

import java.util.UUID;
import org.modelmapper.spi.ConditionalConverter.MatchResult;
import org.testng.annotations.Test;

@Test
public class UuidConverterTest extends AbstractConverterTest {
  UuidConverterTest() {
    super(new UuidConverter());
  }

  public void testConvert() {
    UUID uuid = UUID.randomUUID();
    assertEquals(convert(uuid.toString()), uuid);
    assertEquals(convert(uuid.toString().toCharArray()), uuid);
  }

  public void testSupported() {
    assertEquals(converter.match(char[].class, UUID.class), MatchResult.FULL);
    assertEquals(converter.match(String.class, UUID.class), MatchResult.FULL);
    assertEquals(converter.match(byte[].class, UUID.class), MatchResult.PARTIAL);
    assertEquals(converter.match(String.class, String.class), MatchResult.NONE);
  }
}
