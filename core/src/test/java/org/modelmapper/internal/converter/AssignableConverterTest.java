package org.modelmapper.internal.converter;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.spi.ConditionalConverter.MatchResult;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class AssignableConverterTest extends AbstractConverterTest {
  public AssignableConverterTest() {
    super(new AssignableConverter());
  }

  public void testConvert() {
    assertEquals(convert("123"), "123");
  }

  public void testMatches() {
    assertEquals(converter.match(ArrayList.class, List.class), MatchResult.FULL);
    assertEquals(converter.match(LinkedHashMap.class, Map.class), MatchResult.FULL);

    // Negative
    assertEquals(converter.match(List.class, ArrayList.class), MatchResult.NONE);
  }
}
