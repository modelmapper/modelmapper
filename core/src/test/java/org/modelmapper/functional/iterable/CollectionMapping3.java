package org.modelmapper.functional.iterable;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class CollectionMapping3 extends AbstractTest {
  static class SourceParent {
    List<SourceChild> children;

    List<SourceChild> getChildren() {
      return children;
    }
  }

  static class SourceChild {
    String name;

    SourceChild(String name) {
      this.name = name;
    }
  }

  static class DestParent1 {
    List<DestChild> children;

    void setChildrens(List<DestChild> children) {
      this.children = children;
    }
  }

  static class DestParent2 {
    List<DestChild> kids;

    void setKids(List<DestChild> kids) {
      this.kids = kids;
    }
  }

  static class DestChild {
    String name;

    DestChild() {
    }

    DestChild(String name) {
      this.name = name;
    }
  }

  public void shouldMap() {
    SourceParent source = new SourceParent();
    source.children = Arrays.asList(new SourceChild("abc"), new SourceChild("def"));

    DestParent1 dest = modelMapper.map(source, DestParent1.class);

    assertEquals(dest.children.get(0).name, "abc");
    assertEquals(dest.children.get(1).name, "def");
  }

  public void shouldMapWithPropertyMap() {
    SourceParent source = new SourceParent();
    source.children = Arrays.asList(new SourceChild("abc"), new SourceChild("def"));

    modelMapper.addMappings(new PropertyMap<SourceParent, DestParent2>() {
      protected void configure() {
        map(source.getChildren()).setKids(null);
      }
    });

    DestParent2 dest = modelMapper.map(source, DestParent2.class);
    assertEquals(dest.kids.get(0).name, "abc");
    assertEquals(dest.kids.get(1).name, "def");
  }
}
