package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * https://github.com/jhalterman/modelmapper/pull/39
 */
@Test
public class GH39 extends AbstractTest {
  static class Source {
    String value;

    public Source() {
      getValue();
    }

    String getValue() {
      return value;
    }
  }

  static class Dest {
    String value;

    public Dest() {
      setValue(null);
    }

    void setValue(String value) {
      this.value = value;
    }
  }

  public void shouldIgnoreProxyInvocationsWhileInConstructor() {
    Source src = new Source();
    src.value = "5";

    modelMapper.addMappings(new PropertyMap<Source, Dest>() {
      @Override
      protected void configure() {
        map().setValue(source.getValue());
      }
    });

    Dest dest = modelMapper.map(src, Dest.class);

    assertEquals(dest.value, "5");
  }
}
