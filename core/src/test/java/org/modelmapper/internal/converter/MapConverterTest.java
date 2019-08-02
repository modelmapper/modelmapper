package org.modelmapper.internal.converter;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.modelmapper.ModelMapper;
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

  @SuppressWarnings("unchecked")
  static class SrcSortedMap {
    SortedMap<String, Integer> a = new TreeMap<String, Integer>();
    SortedMap<Integer, String> b = new TreeMap<Integer, String>();
    @SuppressWarnings("rawtypes")
    SortedMap rawmap = new TreeMap();
    {
      a.put("3", 3);
      a.put("1", 1);
      a.put("2", 2);
      b.put(6, "6");
      b.put(4, "4");
      b.put(5, "5");
      rawmap.put(9, "9");
      rawmap.put(7, "7");
      rawmap.put(8, "8");
    }
  }

  static class DestSortedMap {
    SortedMap<String, String> a;
    SortedMap<Integer, Integer> b;
    @SuppressWarnings("rawtypes")
    SortedMap rawmap;
  }

  @SuppressWarnings("unchecked")
  public void shouldConvertElementsFromSortedMap() {
    SortedMap<String, Integer> map = new TreeMap<String, Integer>();
    map.put("3", 3);
    map.put("1", 1);
    map.put("2", 2);

    Map<String, Integer> dest = modelMapper.map(map, SortedMap.class);

    assertEquals(dest, map);
    assertTrue(dest instanceof SortedMap);
  }

  public void shouldConvertElementsFromSortedMapModel() {
    SortedMap<String, String> expectedA = new TreeMap<String, String>();
    expectedA.put("1", "1");
    expectedA.put("2", "2");
    expectedA.put("3", "3");
    SortedMap<Integer, Integer> expectedB = new TreeMap<Integer, Integer>();
    expectedB.put(4, 4);
    expectedB.put(5, 5);
    expectedB.put(6, 6);
    SortedMap<Object, Object> expectedRaw = new TreeMap<Object, Object>();
    expectedRaw.put(7, "7");
    expectedRaw.put(8, "8");
    expectedRaw.put(9, "9");

    DestSortedMap d = modelMapper.map(new SrcSortedMap(), DestSortedMap.class);

    assertEquals(d.a, expectedA);
    assertEquals(d.b, expectedB);
    assertEquals(d.rawmap, expectedRaw);
    assertTrue(d.a instanceof SortedMap);
    assertTrue(d.b instanceof SortedMap);
    assertTrue(d.rawmap instanceof SortedMap);
  }

  public void shouldConvertWithGenericTypes() {
    Map<Integer, BigDecimal> numbers = Collections.singletonMap(1, BigDecimal.valueOf(2));

    Type mapType = new org.modelmapper.TypeToken<Map<Long, String>>() {}.getType();
    Map<Long, String> mixed =  new ModelMapper().map(numbers, mapType);

    assertFalse(mixed.isEmpty());
    assertTrue(mixed.size() == 1);
    assertTrue(mixed.keySet().iterator().next() instanceof Long);
    assertTrue(mixed.keySet().iterator().next() == 1l);
    assertTrue(mixed.values().iterator().next() instanceof String);
    assertTrue(mixed.values().iterator().next().equals("2"));
  }

  public void testMatches() {
    assertEquals(converter.match(HashMap.class, TreeMap.class), MatchResult.FULL);
    assertEquals(converter.match(Map.class, HashMap.class), MatchResult.FULL);

    // Negative
    assertEquals(converter.match(Map.class, ArrayList.class), MatchResult.NONE);
  }
}
