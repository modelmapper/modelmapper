package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * http://code.google.com/p/modelmapper/issues/detail?id=12
 */
@Test
public class GC12 extends AbstractTest {
  static class Source {
    List<SourceChild> children;
  }

  static class SourceChild {
    Integer value;

    Integer getValue() {
      return value;
    }
  }

  static class Dest {
    List<DestChild> children;
  }

  static class DestChild {
    String value;

    void setValue(String value) {
      this.value = value;
    }
  }

  public void test() {
    modelMapper.addMappings(new PropertyMap<SourceChild, DestChild>() {
      protected void configure() {
        map(source.getValue()).setValue(null);
      }
    });

    Source source = new Source();
    SourceChild sourceChild = new SourceChild();
    sourceChild.value = 555;
    source.children = Arrays.asList(sourceChild);

    Dest d = modelMapper.map(source, Dest.class);

    assertEquals(d.children.get(0).value, "555");
  }
}
