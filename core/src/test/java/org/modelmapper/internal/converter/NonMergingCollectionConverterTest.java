package org.modelmapper.internal.converter;

import org.modelmapper.spi.ConditionalConverter.MatchResult;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.*;

/**
 * @author Jonathan Halterman
 */
@Test
public class NonMergingCollectionConverterTest extends AbstractConverterTest {
  public NonMergingCollectionConverterTest() {
    super(new NonMergingCollectionConverter());
  }

  static class S {
 //   List<Integer> a = Arrays.asList(1, 2, 3);
 //   int[] b = new int[] { 4, 5, 6 };
    @SuppressWarnings({ "rawtypes", "unused" })
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
    assertEquals(dest, Arrays.asList(1, 2, 3));
  }

  @SuppressWarnings("unchecked")
  public void shouldConvertListToSet() {
    List<String> source = Arrays.asList("a", "b", "c");
    Set<String> dest = (Set<String>) convert(source, Set.class);
    assertNotNull(dest);
    assertEquals(dest.size(), 3);
    assertTrue(dest.containsAll(source));
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
    assertEquals(dest.size(), 3);
    assertTrue(dest.containsAll(source));
  }

  @SuppressWarnings("unchecked")
  public void shouldConvertListToSortedSet() {
    List<String> source = Arrays.asList("a", "b", "c");
    SortedSet<String> dest = (SortedSet<String>) convert(source, SortedSet.class);
    assertNotNull(dest);
    assertEquals(source, dest);
  }

  @SuppressWarnings("unchecked")
  public void shouldConvertListToListOverwrite() {
    List<String> source = Arrays.asList("a", "b", "c");
    List<String> destination = Arrays.asList("d", "e", "f");
    Class<?> destinationType = List.class;
    assertEquals(convert(source, destination, (Class<Object>) destinationType), source);
  }

  @SuppressWarnings("unchecked")
  public void shouldConvertListToListOverwriteExistNotMerge() {
    List<String> source = Arrays.asList("a", "b", "c");
    List<String> destination = Arrays.asList("d", "e", "f", "g");
    Class<?> destinationType = List.class;
    assertEquals(convert(source, destination, destinationType),
        Arrays.asList("a", "b", "c"));
  }

  @SuppressWarnings("unchecked")
  public void shouldConvertListToListOverwriteExpand() {
    List<String> source = Arrays.asList("a", "b", "c");
    List<String> destination = Arrays.asList("d", "e");
    Class<?> destinationType = List.class;
    assertEquals(convert(source, destination, destinationType),
        Arrays.asList("a", "b", "c"));
  }

  static class SrcSortedSet {
    SortedSet<Integer> genericSet = new TreeSet<Integer>(Arrays.asList(3, 1, 2));
    @SuppressWarnings({ "rawtypes", "unchecked" })
    SortedSet rawset = new TreeSet(Arrays.asList(9, 7, 8));
  }

  static class DestSortedSet {
    SortedSet<Integer> genericSet;
    @SuppressWarnings("rawtypes")
    SortedSet rawset;
  }

  @SuppressWarnings("unchecked")
  public void shouldConvertElementsFromSortedSet() {
    SortedSet<Integer> s = new TreeSet<Integer>(Arrays.asList(3, 1, 2));
    SortedSet<Object> d = modelMapper.map(s, SortedSet.class);
    assertEquals(d, s);
    assertTrue(d instanceof SortedSet);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void shouldConvertElementsFromSortedSetModel() {
    DestSortedSet d = modelMapper.map(new SrcSortedSet(), DestSortedSet.class);
    assertEquals(d.genericSet, new TreeSet<Integer>(Arrays.asList(1, 2, 3)));
    assertEquals(d.rawset, new TreeSet(Arrays.asList(7, 8, 9)));
    assertTrue(d.genericSet instanceof SortedSet);
    assertTrue(d.rawset instanceof SortedSet);
  }

  public void testMatches() {
    assertEquals(converter.match(ArrayList.class, List.class), MatchResult.FULL);
    assertEquals(converter.match(Object[].class, Set.class), MatchResult.FULL);

    // Negative
    assertEquals(converter.match(Map.class, ArrayList.class), MatchResult.NONE);
  }
}
