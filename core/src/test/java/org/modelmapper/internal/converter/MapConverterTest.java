package org.modelmapper.internal.converter;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.modelmapper.spi.ConditionalConverter.MatchResult;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class MapConverterTest extends AbstractConverterTest {
  public MapConverterTest() {
    super(new MapConverter());
  }

  @SuppressWarnings("unchecked")
  static class S {
    Map<String, Integer> a = new HashMap<String, Integer>();
    Map<Integer, String> b = new HashMap<Integer, String>();
    @SuppressWarnings("rawtypes")
    Map rawmap = new HashMap();
    {
      a.put("1", 1);
      a.put("2", 2);
      a.put("3", 3);
      b.put(4, "4");
      b.put(5, "5");
      b.put(6, "6");
      rawmap.put(7, "7");
      rawmap.put(8, "8");
      rawmap.put(9, "9");
    }
  }

  static class D {
    Map<String, String> a;
    Map<Integer, Integer> b;
    @SuppressWarnings("rawtypes")
    Map rawmap;
  }

  @SuppressWarnings("unchecked")
  public void shouldConvertElements() {
    Map<String, Integer> map = new HashMap<String, Integer>();
    map.put("1", 1);
    map.put("2", 2);
    map.put("3", 3);
    Map<String, Integer> dest = modelMapper.map(map, Map.class);
    assertEquals(dest, map);
  }

  public void shouldConvertElementsFromModel() {
    Map<String, String> expectedA = new HashMap<String, String>();
    expectedA.put("1", "1");
    expectedA.put("2", "2");
    expectedA.put("3", "3");
    Map<Integer, Integer> expectedB = new HashMap<Integer, Integer>();
    expectedB.put(4, 4);
    expectedB.put(5, 5);
    expectedB.put(6, 6);
    Map<Object, Object> expectedRaw = new HashMap<Object, Object>();
    expectedRaw.put(7, "7");
    expectedRaw.put(8, "8");
    expectedRaw.put(9, "9");

    D d = modelMapper.map(new S(), D.class);
    assertEquals(d.a, expectedA);
    assertEquals(d.b, expectedB);
    assertEquals(d.rawmap, expectedRaw);
  }

  public void testMatches() {
    assertEquals(converter.match(HashMap.class, TreeMap.class), MatchResult.FULL);
    assertEquals(converter.match(Map.class, HashMap.class), MatchResult.FULL);

    // Negative
    assertEquals(converter.match(Map.class, ArrayList.class), MatchResult.NONE);
  }
}
