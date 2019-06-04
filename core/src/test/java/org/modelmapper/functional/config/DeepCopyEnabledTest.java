package org.modelmapper.functional.config;

import org.modelmapper.AbstractTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;

@Test
public class DeepCopyEnabledTest extends AbstractTest {
  static class Source {
    Property property;

    public Source(Property property) {
      this.property = property;
    }
  }
  static class Destination {
    Property property;
  }


  static class Property {
    String value;

    public Property() {
    }

    public Property(String value) {
      this.value = value;
    }
  }

  public void shouldDeepCopy() {
    modelMapper.getConfiguration().setDeepCopyEnabled(true);
    modelMapper.getConfiguration().setDeepCopyEnabled(false);
    modelMapper.getConfiguration().setDeepCopyEnabled(true);

    Source source = new Source(new Property("text"));
    Destination destination = modelMapper.map(source, Destination.class);

    assertNotSame(destination.property, source.property);
    assertEquals(destination.property.value, source.property.value);
  }

  public void shouldNotDeepCopyByDefault() {
    Source source = new Source(new Property("text"));
    Destination destination = modelMapper.map(source, Destination.class);

    assertSame(destination.property, source.property);
  }

  public void shouldNotDeepCopy() {
    modelMapper.getConfiguration().setDeepCopyEnabled(false);
    modelMapper.getConfiguration().setDeepCopyEnabled(true);
    modelMapper.getConfiguration().setDeepCopyEnabled(false);

    Source source = new Source(new Property("text"));
    Destination destination = modelMapper.map(source, Destination.class);

    assertSame(destination.property, source.property);
  }
}
