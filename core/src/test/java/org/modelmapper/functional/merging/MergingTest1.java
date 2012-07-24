package org.modelmapper.functional.merging;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * Tests the merging of mappings from one TypeMap into another.
 * 
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class MergingTest1 extends AbstractTest {
  static class Outer {
    Inner inner = new Inner();
  }

  static class Inner {
    String value;
  }

  static class DestOuter {
    DestInner inner = new DestInner();
  }

  static class DestInner {
    String value;

    public void setValue(String value) {
      this.value = value;
    }
  }

  public void shouldMergeMappings() {
    modelMapper.addMappings(new PropertyMap<Inner, DestInner>() {
      @Override
      protected void configure() {
        map().setValue("test");
      }
    });

    DestOuter o = modelMapper.map(new Outer(), DestOuter.class);
    assertEquals(o.inner.value, "test");
  }
}
