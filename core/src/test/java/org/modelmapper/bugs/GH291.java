package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

@Test
public class GH291 extends AbstractTest {
  static class Source {
    private int value;

    public Source(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }

    public void setValue(int value) {
      this.value = value;
    }
  }

  static class Destination {
    private KInteger value;

    public KInteger getValue() {
      return value;
    }

    public void setValue(KInteger value) {
      this.value = value;
    }
  }

  static class KInteger {
    private int value;

    public KInteger(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }

    public void setValue(int value) {
      this.value = value;
    }
  }

  public void shouldHandlePrimitiveConverter() {
    modelMapper.addConverter(new Converter<Integer, KInteger>() {
      public KInteger convert(MappingContext<Integer, KInteger> context) {
        return new KInteger(context.getSource() / 1000);
      }
    });

    Destination dest = modelMapper.map(new Source(123000), Destination.class);
    assertEquals(dest.value.value, 123);
  }
}
