package org.modelmapper.functional.deepmapping;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class DeepMappingTest2 extends AbstractTest {
  public static class Source {
    SourceInner value = new SourceInner();
  }

  public static class SourceInner {
    String valueInner = "abc";
  }

  public static class Dest {
    DestInner value;
  }

  public static class DestInner {
    String valueInner;
  }

  public void shouldMap() {
    Dest dest = modelMapper.map(new Source(), Dest.class);
    assertEquals(dest.value.valueInner, "abc");
  }
}
