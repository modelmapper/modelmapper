package org.modelmapper.functional.enums;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

@Test(groups = "functional")
public class EnumConverter extends AbstractTest {
  public enum Source {
    A, B, C
  }

  public enum Dest {
    One, Two, Three
  }

  public void test() {
    modelMapper.createTypeMap(Source.class, Dest.class).setConverter(new Converter<Source, Dest>() {
      public Dest convert(MappingContext<Source, Dest> context) {
        switch (context.getSource()) {
          case A:
            return Dest.One;
          case B:
            return Dest.Two;
          case C:
            return Dest.Three;
          default:
            return null;
        }
      }
    });

    assertEquals(modelMapper.map(Source.A, Dest.class), Dest.One);
    assertEquals(modelMapper.map(Source.B, Dest.class), Dest.Two);
    assertEquals(modelMapper.map(Source.C, Dest.class), Dest.Three);
  }
}
