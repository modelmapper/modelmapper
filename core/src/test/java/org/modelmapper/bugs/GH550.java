package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

@Test
public class GH550 extends AbstractTest {

  public void shouldSupportHashMapSubClass() {
    SourceHashMap attributes = new SourceHashMap();
    attributes.put("foo", "bar");
    Destination destination = modelMapper.map(
        new Source(attributes), Destination.class);
    assertEquals(destination.attributes.get("foo"), "bar");
  }

  private static class Source {
    SourceHashMap attributes;

    public Source(SourceHashMap attributes) {
      this.attributes = attributes;
    }

    public SourceHashMap getAttributes() {
      return attributes;
    }
  }

  private static class SourceHashMap extends HashMap<String, Object> {
  }

  private static class Destination {
    DestinationHashMap attributes;

    public void setAttributes(DestinationHashMap attributes) {
      this.attributes = attributes;
    }
  }

  private static class DestinationHashMap extends HashMap<String, Object> {
  }
}
