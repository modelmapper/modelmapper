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
public class CollectionMapping1 extends AbstractTest {
  static class DArray {
    String[] name;

    public void setName(String[] name) {
      this.name = name;
    }
  }

  static class DList {
    List<String> name;

    public void setName(List<String> name) {
      this.name = name;
    }
  }

  static class DCollection {
    Collection<String> name;

    public void setName(Collection<String> name) {
      this.name = name;
    }
  }

  static class DSet {
    Set<String> name;

    public void setName(Set<String> name) {
      this.name = name;
    }
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
}
