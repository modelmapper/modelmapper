package org.modelmapper.functional.lambda;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.fail;

import org.modelmapper.AbstractTest;
import org.modelmapper.Asserts;
import org.modelmapper.Condition;
import org.modelmapper.ConfigurationException;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.Provider;
import org.modelmapper.TypeMap;
import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.SourceGetter;
import org.modelmapper.ExpressionMap;
import org.modelmapper.builder.ConfigurableMapExpression;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class TypeMapLambdaTest extends AbstractTest {
  static class Src {
    String srcText;

    public Src(String srcText) {
      this.srcText = srcText;
    }

    public String getSrcText() {
      return srcText;
    }

    public void setSrcText(String srcText) {
      this.srcText = srcText;
    }
  }

  static class Dest {
    String destText;

    public String getDestText() {
      return destText;
    }

    public void setDestText(String destText) {
      this.destText = destText;
    }
  }

  @BeforeMethod
  public void setUp() {
    modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setImplicitMappingEnabled(false);
  }

  public void shouldAddMapping() {
    TypeMap<Src, Dest> typeMap = modelMapper.createTypeMap(Src.class, Dest.class);
    typeMap.addMapping(srcGetter(), destSetter());

    typeMap.validate();
    assertEquals(typeMap.map(new Src("foo")).destText, "foo");
  }

  public void shouldAddMappingWithConverter() {
    TypeMap<Src, Dest> typeMap = modelMapper.createTypeMap(Src.class, Dest.class);
    typeMap.addMappings(
        new ExpressionMap<Src, Dest>() {
          public void configure(ConfigurableMapExpression<Src, Dest> mapping) {
            mapping.using(new Converter<String, String>() {
              public String convert(MappingContext<String, String> context) {
                return context.getSource().toUpperCase();
              }
            }).map(srcGetter(), destSetter());
          }
        });

    typeMap.validate();
    assertEquals(typeMap.map(new Src("foo")).destText, "FOO");
  }

  public void shouldAddMappingWithSkip() {
    TypeMap<Src, Dest> typeMap = modelMapper.createTypeMap(Src.class, Dest.class);
    typeMap.addMappings(
        new ExpressionMap<Src, Dest>() {
          public void configure(ConfigurableMapExpression<Src, Dest> mapping) {
            mapping.skip(destSetter());
          }
        });

    typeMap.validate();
    assertNull(typeMap.map(new Src("foo")).destText);
  }

  public void shouldAddMappingWithCondition() {
    TypeMap<Src, Dest> typeMap = modelMapper.createTypeMap(Src.class, Dest.class);
    typeMap.addMappings(
        new ExpressionMap<Src, Dest>() {
          public void configure(ConfigurableMapExpression<Src, Dest> mapping) {
            mapping.when(new Condition<String, String>() {
              public boolean applies(MappingContext<String, String> context) {
                return context.getSource().equals("foo");
              }
            }).map(srcGetter(), destSetter());
          }
        });

    typeMap.validate();
    assertNull(typeMap.map(new Src("bar")).destText);
    assertEquals(typeMap.map(new Src("foo")).destText, "foo");
  }

  public void shouldFailedWithEmptySourceGetter() {
    TypeMap<Src, Dest> typeMap = modelMapper.createTypeMap(Src.class, Dest.class);

    try {
      typeMap.addMapping(new SourceGetter<Src>() {
        public Object get(Src source) {
          return source;
        }
      }, destSetter());
      fail();
    } catch (ConfigurationException e) {
      Asserts.assertContains(e.getMessage(), "Illegal SourceGetter defined");
    }
  }

  public void shouldFailedWithEmptyDestinationSetter() {
    TypeMap<Src, Dest> typeMap = modelMapper.createTypeMap(Src.class, Dest.class);

    try {
      typeMap.addMapping(srcGetter(), new DestinationSetter<Dest, String>() {
        public void accept(Dest destination, String value) {
        }
      });
      fail();
    } catch (ConfigurationException e) {
      Asserts.assertContains(e.getMessage(), "Illegal DestinationSetter defined");
    }
  }

  public void shouldFailedWithWrongSourceGetter() {
    TypeMap<Src, Dest> typeMap = modelMapper.createTypeMap(Src.class, Dest.class);

    try {
      typeMap.addMapping(new SourceGetter<Src>() {
        public Object get(Src source) {
          return source.toString();
        }
      }, destSetter());
      fail();
    } catch (ConfigurationException e) {
      Asserts.assertContains(e.getMessage(), "Illegal SourceGetter defined");
    }
  }


  private static SourceGetter<Src> srcGetter() {
    return new SourceGetter<Src>() {
      public Object get(Src source) {
        return source.getSrcText();
      }
    };
  }

  private static DestinationSetter<Dest, String> destSetter() {
    return new DestinationSetter<Dest, String>() {
      public void accept(Dest destination, String value) {
        destination.setDestText(value);
      }
    };
  }
}
