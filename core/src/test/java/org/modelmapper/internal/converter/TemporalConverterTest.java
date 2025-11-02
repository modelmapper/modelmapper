package org.modelmapper.internal.converter;

import static org.testng.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import org.modelmapper.spi.ConditionalConverter.MatchResult;
import org.testng.annotations.Test;

@Test
public class TemporalConverterTest extends AbstractConverterTest {
  TemporalConverterTest() {
    super(new TemporalConverter());
  }

  public void testConvert() {
    LocalDate localDate = LocalDate.now();
    LocalDateTime localDateTime = LocalDateTime.now();
    OffsetDateTime offsetDateTime = OffsetDateTime.now();
    ZonedDateTime zonedDateTime = ZonedDateTime.now();
    assertEquals(convert(localDate), localDate);
    assertEquals(convert(localDateTime), localDateTime);
    assertEquals(convert(offsetDateTime), offsetDateTime);
    assertEquals(convert(zonedDateTime), zonedDateTime);
  }

  public void testSupported() {
    assertEquals(converter.match(LocalDateTime.class, LocalDateTime.class), MatchResult.FULL);
    assertEquals(converter.match(OffsetDateTime.class, OffsetDateTime.class), MatchResult.FULL);
    assertEquals(converter.match(ZonedDateTime.class, ZonedDateTime.class), MatchResult.FULL);
    assertEquals(converter.match(LocalDate.class, LocalDateTime.class), MatchResult.NONE);
  }
}
