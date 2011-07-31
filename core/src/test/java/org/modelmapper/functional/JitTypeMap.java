package org.modelmapper.functional;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * Tests a scenario where a JIT typemap is created to map values from two mismatched types.
 */
@Test(groups = "functional")
public class JitTypeMap extends AbstractTest {
  static class S1 {
    S2 child;

    S2 getChild() {
      return child;
    }
  }

  static class S2 {
    String value1;
    String value2;
  }

  static class D1 {
    D2 sub;

    void setSub(D2 sub) {
      this.sub = sub;
    }
  }

  static class D2 {
    String value1;
    String value2;
  }

  public void shouldMap() {
    S1 source = new S1();
    source.child = new S2();
    source.child.value1 = "test1";
    source.child.value2 = "test2";

    modelMapper.addMappings(new PropertyMap<S1, D1>() {
      protected void configure() {
        map(source.getChild()).setSub(null);
      }
    });

    D1 dest = modelMapper.map(source, D1.class);
    assertEquals(dest.sub.value1, "test1");
    assertEquals(dest.sub.value2, "test2");
  }
}
