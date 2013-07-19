package org.modelmapper.functional.config;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

@Test
public class ImplicitMappingDisabled extends AbstractTest {
  static class Src {
    public String name;
  }

  static class Dst {
    public String name;
  }

  public void shouldSkipImplicitMappingWhenDisabled() {
    Src src = new Src();
    src.name = "foo";

    modelMapper.getConfiguration().setImplicitMappingEnabled(false);

    Dst dst = modelMapper.map(src, Dst.class);
    assertNull(dst.name);
  }
}
