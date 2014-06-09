package org.modelmapper.functional.converter;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class ConverterTest1 extends AbstractTest {
  static class Source {
    String value;
  }

  static class Dest {
    String value;

    void setValue(String value) {
      this.value = value;
    }
  }

  public void shouldUseTypeMapPropertyConverter() {
    modelMapper.createTypeMap(Source.class, Dest.class).setPropertyConverter(
        new Converter<Object, Object>() {
          public Object convert(MappingContext<Object, Object> context) {
            return "test";
          }
        });

    Source source = new Source();
    source.value = "dummy";
    Dest dest = modelMapper.map(source, Dest.class);
    assertEquals(dest.value, "test");
  }

  public void shouldUseTypeMapConverter() {
    modelMapper.createTypeMap(Source.class, Dest.class).setConverter(new Converter<Source, Dest>() {
      public Dest convert(MappingContext<Source, Dest> context) {
        Dest dest = new Dest();
        dest.value = "test";
        return dest;
      }
    });

    Dest dest = modelMapper.map(new Source(), Dest.class);
    assertEquals(dest.value, "test");
  }

  public void propertyMapConverterShouldOverrideTypeMapPropertyConverter() {
    modelMapper.createTypeMap(Source.class, Dest.class)
        .setPropertyConverter(new Converter<Object, Object>() {
          public Object convert(MappingContext<Object, Object> context) {
            return "test";
          }
        })
        .addMappings(new PropertyMap<Source, Dest>() {
          @Override
          protected void configure() {
            using(new Converter<Object, Object>() {
              public Object convert(MappingContext<Object, Object> context) {
                return "abc";
              }
            }).map().setValue(null);
          }
        });

    Dest dest = modelMapper.map(new Source(), Dest.class);
    assertEquals(dest.value, "abc");
  }

  public void shouldUseConverterWhenMappingDestinationFieldWithoutSource() {
    modelMapper.addMappings(new PropertyMap<Source, Dest>() {
      @Override
      protected void configure() {
        using(new Converter<Object, Object>() {
          public Object convert(MappingContext<Object, Object> context) {
            return "abc";
          }
        }).map(destination.value);
      }
    });

    Dest dest = modelMapper.map(new Source(), Dest.class);
    assertEquals(dest.value, "abc");
  }

  public void shouldUseConverterWhenMappingFields() {
    modelMapper.addMappings(new PropertyMap<Source, Dest>() {
      @Override
      protected void configure() {
        using(new Converter<Object, Object>() {
          public Object convert(MappingContext<Object, Object> context) {
            return context.getSource() + "abc";
          }
        }).map(source.value, destination.value);
      }
    });

    Source source = new Source();
    source.value = "test";
    Dest dest = modelMapper.map(source, Dest.class);
    assertEquals(dest.value, "testabc");
  }
}
