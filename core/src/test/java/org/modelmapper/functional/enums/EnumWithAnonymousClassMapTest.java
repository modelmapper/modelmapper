package org.modelmapper.functional.enums;

import org.modelmapper.AbstractTest;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test(groups = "functional")
public class EnumWithAnonymousClassMapTest extends AbstractTest {

  enum Source {
    a,
    b,
    c {
      @Override
      public boolean isUnique() {
        return true;
      }
    };

    public boolean isUnique() {
      return false;
    }
  }

  enum Dest {
    ordinary, unique
  }

  public void testConvertEnumWithAnonymousClass() {
    modelMapper.addConverter(new Converter<Source, Dest>() {
      @Override
      public Dest convert(MappingContext<Source, Dest> context) {
        return context.getSource().isUnique() ? Dest.unique : Dest.ordinary;
      }
    });

    assertEquals(modelMapper.map(Source.a, Dest.class), Dest.ordinary);
    assertEquals(modelMapper.map(Source.c, Dest.class), Dest.unique);
  }

}
