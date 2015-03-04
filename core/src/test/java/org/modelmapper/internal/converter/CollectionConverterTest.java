package org.modelmapper.internal.converter;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.modelmapper.Asserts;
import org.modelmapper.spi.ConditionalConverter.MatchResult;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class CollectionConverterTest extends AbstractConverterTest {
  public CollectionConverterTest() {
    super(new CollectionConverter());
  }

  static class S {
 //   List<Integer> a = Arrays.asList(1, 2, 3);
 //   int[] b = new int[] { 4, 5, 6 };
    @SuppressWarnings("rawtypes")
    List rawlist = Arrays.asList(7, 8, 9);
  }

  static class D {
 //   List<String> a;
 //   Collection<String> b;
    @SuppressWarnings("rawtypes")
    List rawlist;
  }

  @SuppressWarnings("unchecked")
  public void shouldConvertElementsFromArray() {
    int[] source = new int[] { 1, 2, 3 };
    List<Object> dest = modelMapper.map(source, List.class);
    assertEquals(dest, Arrays.asList(1, 2, 3));
  }

  @SuppressWarnings("unchecked")
  public void shouldConvertElementsFromList() {
    List<Integer> list = Arrays.asList(1, 2, 3);
    List<Object> dest = modelMapper.map(list, List.class);
    assertEquals(dest, list);
  }

  public void shouldConvertElementsFromModel() {
    D d = modelMapper.map(new S(), D.class);
  //  assertEquals(d.a, Arrays.asList("1", "2", "3"));
   // assertEquals(d.b, Arrays.asList("4", "5", "6"));
    assertEquals(d.rawlist, Arrays.asList(7, 8, 9));
  }

  public void shouldConvertListToList() {
    List<String> source = Arrays.asList("a", "b", "c");
    assertEquals(convert(source, ArrayList.class), source);
  }

  public void shouldConvertArrayToList() {
    String[] source = new String[] { "a", "b", "c" };
    assertEquals(convert(source, ArrayList.class), Arrays.asList(source));
  }

  @SuppressWarnings("unchecked")
  public void shouldConvertPrimitiveArrayToCollection() {
    int[] source = new int[] { 1, 2, 3 };
    Collection<Integer> dest = (Collection<Integer>) convert(source, Collection.class);
    assertEquals(dest.toArray(), source);
  }

  @SuppressWarnings("unchecked")
  public void shouldConvertListToSet() {
    List<String> source = Arrays.asList("a", "b", "c");
    Set<String> dest = (Set<String>) convert(source, Set.class);
    assertTrue(dest instanceof Set);
    Asserts.assertEquals(source, dest);
  }

  public void shouldConvertArrayToCollection() {
    Collection<String> source = Arrays.asList("a", "b", "c");
    assertEquals(convert(source, Collection.class), source);
  }

  @SuppressWarnings("unchecked")
  public void shouldConvertListToHashSet() {
    List<String> source = Arrays.asList("a", "b", "c");
    Set<String> dest = (Set<String>) convert(source, Set.class);
    assertTrue(dest instanceof HashSet);
    Asserts.assertEquals(source, dest);
  }

  @SuppressWarnings("unchecked")
  public void shouldConvertListToSortedSet() {
    List<String> source = Arrays.asList("a", "b", "c");
    SortedSet<String> dest = (SortedSet<String>) convert(source, SortedSet.class);
    assertTrue(dest instanceof SortedSet);
    Asserts.assertEquals(source, dest);
  }

  public void testMatches() {
    assertEquals(converter.match(ArrayList.class, List.class), MatchResult.FULL);
    assertEquals(converter.match(Object[].class, Set.class), MatchResult.FULL);

    // Negative
    assertEquals(converter.match(Map.class, ArrayList.class), MatchResult.NONE);
  }
}
