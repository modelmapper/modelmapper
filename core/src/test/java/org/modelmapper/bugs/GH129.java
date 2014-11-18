package org.modelmapper.bugs;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

@Test
public class GH129 extends AbstractTest {
  static class Source {
    String v1;
    String v2;
    String v3;
    String v4;
    String v5;
    String v6;
  }

  static class Dest {
    char v1;
    Character v2;
    boolean v3;
    Boolean v4;
    int v5;
    Integer v6;
  }

  public void shouldConvertEmptyStringToDefaultValue() {
    Source source = new Source() {
      {
        v1 = v2 = v3 = v4 = v5 = v6 = "";
      }
    };

    modelMapper.map(source, Dest.class);
  }
}
