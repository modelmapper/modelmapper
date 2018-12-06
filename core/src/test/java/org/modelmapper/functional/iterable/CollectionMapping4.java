package org.modelmapper.functional.iterable;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.assertEquals;

/**
 * @author Adrian Pauli
 */
@Test(groups = "functional")
public class CollectionMapping4 extends AbstractTest {

  static class SArray {
    String[] name;
  }

  static class SList {
    List<String> name;
  }

  static class SSet {
    Set<String> name;
  }

  public void shouldRemoveFromExistentArray() {
    SArray source = new SArray();
    source.name = new String[]{"a", "b"};

    SArray destination = new SArray();
    destination.name = new String[]{"a", "b", "c"};

    modelMapper.map(source, destination);

    modelMapper.validate();
    assertEquals(destination.name.length, 2);
    assertEquals(destination.name, source.name);
  }

  public void shouldAddToExistentArray() {
    SArray source = new SArray();
    source.name = new String[]{"a", "b", "c"};

    SArray destination = new SArray();
    destination.name = new String[]{"a", "b"};


    modelMapper.map(source, destination);

    modelMapper.validate();
    assertEquals(destination.name.length, 3);
    assertEquals(destination.name, source.name);
  }

  public void shouldRemoveFromExistentList() {
    SList source = new SList();
    source.name = Arrays.asList("a", "b");

    SList destination = new SList();
    destination.name = Arrays.asList("a", "b", "c");
    modelMapper.map(source, destination);

    modelMapper.validate();
    assertEquals(destination.name.size(), 2);
    assertEquals(destination.name, source.name);
  }

  public void shouldAddToExistentList() {
    SList source = new SList();
    source.name = Arrays.asList("a", "b", "c");
    modelMapper.map(source, source);

    SList destination = new SList();
    destination.name = Arrays.asList("a", "b");
    modelMapper.map(source, destination);

    modelMapper.validate();
    assertEquals(destination.name.size(), 3);
    assertEquals(destination.name, source.name);
  }

  public void shouldRemoveFromExistentSet() {
    SSet source = new SSet();
    source.name = new HashSet<String>();
    source.name.add("1");
    source.name.add("2");

    SSet destination = new SSet();
    destination.name = new HashSet<String>();
    destination.name.add("1");
    destination.name.add("2");
    destination.name.add("3");
    modelMapper.map(source, destination);

    modelMapper.validate();
    assertEquals(destination.name.size(), 2);
    assertEquals(destination.name, source.name);
  }

  public void shouldAddToExistentSet() {
    SSet source = new SSet();
    source.name = new HashSet<String>();
    source.name.add("1");
    source.name.add("2");
    source.name.add("3");

    SSet destination = new SSet();
    destination.name = new HashSet<String>();
    destination.name.add("1");
    destination.name.add("2");
    modelMapper.map(source, destination);

    modelMapper.validate();
    assertEquals(destination.name.size(), 3);
    assertEquals(destination.name, source.name);
  }
}
