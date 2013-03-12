package org.modelmapper.functional.converter;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class PreAndPostConverterTest extends AbstractTest {
  static class Source {
    String id;
    String addr;
  }

  static class Dest {
    String id;
    String address;
  }

  public void shouldPreConvertert() {
    modelMapper.createTypeMap(Source.class, Dest.class).setPreConverter(
        new Converter<Source, Dest>() {
          public Dest convert(MappingContext<Source, Dest> context) {
            context.getDestination().address = context.getSource().addr;
            return context.getDestination();
          }
        });

    Source source = new Source();
    source.id = "123";
    source.addr = "test";

    Dest dest = modelMapper.map(source, Dest.class);

    assertEquals(dest.id, source.id);
    assertEquals(dest.address, source.addr);
  }

  public void shouldPostConvertert() {
    modelMapper.createTypeMap(Source.class, Dest.class).setPostConverter(
        new Converter<Source, Dest>() {
          public Dest convert(MappingContext<Source, Dest> context) {
            context.getDestination().address = context.getSource().id + context.getSource().addr;
            return context.getDestination();
          }
        });

    Source source = new Source();
    source.id = "123";
    source.addr = "test";

    Dest dest = modelMapper.map(source, Dest.class);

    assertEquals(dest.id, source.id);
    assertEquals(dest.address, source.id + source.addr);
  }
}
