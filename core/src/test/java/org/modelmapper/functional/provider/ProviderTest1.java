package org.modelmapper.functional.provider;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.modelmapper.AbstractTest;
import org.modelmapper.Provider;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class ProviderTest1 extends AbstractTest {
  static class Source {
    Prop prop;

    Prop getProp() {
      return prop;
    }
  }

  static class Prop {
    String value;
  }

  static class Dest {
    DestProp prop;

    void setProp(DestProp prop) {
      this.prop = prop;
    }
  }

  static class DestProp {
    String value;

    void setValue(String value) {
      this.value = value;
    }
  }

  public void shouldUseTypeMapConverter() {
    final Dest dest = new Dest();

    modelMapper.createTypeMap(Source.class, Dest.class).setProvider(new Provider<Dest>() {
      public Dest get(ProvisionRequest<Dest> request) {
        return dest;
      }
    });

    Dest dd = modelMapper.map(new Source(), Dest.class);
    assertTrue(dest == dd);
  }

  public void shouldUseTypeMapPropertyProvider() {
    final String value = new String("test");

    modelMapper.createTypeMap(Source.class, Dest.class).setPropertyProvider(new Provider<String>() {
      public String get(ProvisionRequest<String> request) {
        return value;
      }
    });

    Source source = new Source();
    source.prop = new Prop();
    source.prop.value = "abc";
    Dest dest = modelMapper.map(source, Dest.class);

    // The provided value is used rather than the source value
    assertEquals(dest.prop.value, value);
  }
}
