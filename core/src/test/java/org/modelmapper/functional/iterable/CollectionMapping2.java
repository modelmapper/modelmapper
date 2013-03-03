package org.modelmapper.functional.iterable;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class CollectionMapping2 extends AbstractTest {
  static class SList {
    List<Integer> name;
  }

  static class DArray {
    String[] name;
  }

  static class DList {
    List<String> name;
  }

  static class DCollection {
    Collection<String> name;
  }

  static class DSet {
    Set<String> name;
  }

  public void shouldMapListToArray() {
    DList list = new DList();
    list.name = Arrays.asList("a", "b", "c");
    DArray d = modelMapper.map(list, DArray.class);

    modelMapper.validate();
    assertEquals(Arrays.asList(d.name), list.name);
  }

  public void shouldMapArrayToList() {
    DArray array = new DArray();
    array.name = new String[] { "a", "b", "c" };
    DList d = modelMapper.map(array, DList.class);

    modelMapper.validate();
    assertEquals(d.name, Arrays.asList(array.name));
  }

  public void shouldMapListToList() {
    DList list = new DList();
    list.name = Arrays.asList("a", "b", "c");
    DList d = modelMapper.map(list, DList.class);

    modelMapper.validate();
    assertEquals(d.name, list.name);
  }

  public void shouldMapListToListOfDifferentTypes() {
    SList list = new SList();
    list.name = Arrays.asList(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3));
    DList d = modelMapper.map(list, DList.class);

    modelMapper.validate();
    assertEquals(d.name, Arrays.asList("1", "2", "3"));
  }
}
