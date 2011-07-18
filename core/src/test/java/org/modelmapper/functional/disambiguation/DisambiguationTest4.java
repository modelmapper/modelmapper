package org.modelmapper.functional.disambiguation;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import org.modelmapper.AbstractTest;
import org.modelmapper.Asserts;
import org.modelmapper.ConfigurationException;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class DisambiguationTest4 extends AbstractTest {
  static class Source {
    String value1 = "abc";
    String value2 = "def";
    String AaBbCc;

    public String getValue2() {
      return value2;
    }
  }

  static class Dest {
    String value;
    String AaBb;

    public void setValue(String value) {
      this.value = value;
    }
  }

  static class Source1 {
    String AaBbCc;
    String AaBbAa;
  }

  static class Dest1 {
    String Aa;
  }

  public void shouldThrowOnMap() {
    try {
      modelMapper.map(new Source(), Dest.class);
    } catch (ConfigurationException e) {
      Asserts.assertContains(e.getMessage(), "setValue() matches multiple");
      return;
    }

    fail();
  }

  public void shouldOverrideAmbiguityWithExplicitMapping() {
    Dest d = modelMapper.addMappings(new PropertyMap<Source, Dest>() {
      protected void configure() {
        map().setValue(source.getValue2());
      }
    }).map(new Source());

    assertEquals(d.value, "def");
  }
}
