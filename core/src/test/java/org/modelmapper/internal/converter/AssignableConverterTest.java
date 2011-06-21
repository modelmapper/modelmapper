package org.modelmapper.internal.converter;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.internal.converter.AssignableConverter;
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
    assertTrue(converter.supports(ArrayList.class, List.class));
    assertTrue(converter.supports(LinkedHashMap.class, Map.class));

    // Negative
    assertFalse(converter.supports(List.class, ArrayList.class));
  }
}
