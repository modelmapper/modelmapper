package org.modelmapper.functional;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.Provider;
import org.modelmapper.config.Configuration.AccessLevel;
import org.testng.annotations.Test;

/**
 * Maps to an immutable destination.
 * 
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class Immutable extends AbstractTest {
  static class Source {
    String name;
    String address;
  }

  static class Destination1 {
    private final String name;
    private final String address;

    Destination1(String name, String address) {
      this.name = name;
      this.address = address;
    }
  }

  static class Destination2 {
    private final String name = null;
    private final String address = null;
  }

  public void shouldMapToImmutableViaProvider() {
    modelMapper.createTypeMap(Source.class, Destination1.class).setProvider(
        new Provider<Destination1>() {
          public Destination1 get(ProvisionRequest<Destination1> request) {
            Source s = Source.class.cast(request.getSource());
            return new Destination1(s.name, s.address);
          }
        });

    Source source = new Source();
    source.name = "Joe";
    source.address = "Main Street";

    Destination1 dest1 = modelMapper.map(source, Destination1.class);
    assertEquals(dest1.name, "Joe");
    assertEquals(dest1.address, "Main Street");
  }

  public void shouldMapToImmutableViaSecurityOverride() {
    modelMapper.getConfiguration()
        .setFieldMatchingEnabled(true)
        .setFieldAccessLevel(AccessLevel.PRIVATE);

    Source source = new Source();
    source.name = "Joe";
    source.address = "Main Street";

    Destination2 dest2 = modelMapper.map(source, Destination2.class);
    assertEquals(dest2.name, "Joe");
    assertEquals(dest2.address, "Main Street");
  }
}
