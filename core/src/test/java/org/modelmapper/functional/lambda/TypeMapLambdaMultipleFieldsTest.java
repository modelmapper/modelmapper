package org.modelmapper.functional.lambda;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.modelmapper.AbstractTest;
import org.modelmapper.Condition;
import org.modelmapper.Converter;
import org.modelmapper.ExpressionMap;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.builder.ConfigurableMapExpression;
import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.SourceGetter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class TypeMapLambdaMultipleFieldsTest extends AbstractTest {
  static class Src {
    private String srcText;
    private int srcNumber;

    public Src(String srcText, int srcNumber) {
      this.srcText = srcText;
      this.srcNumber = srcNumber;
    }

    public String getSrcText() {
      return srcText;
    }

    public void setSrcText(String srcText) {
      this.srcText = srcText;
    }

    public int getSrcNumber() {
      return srcNumber;
    }

    public void setSrcNumber(int srcNumber) {
      this.srcNumber = srcNumber;
    }
  }

  static class Dest {
    private String destText;
    private int destNumber;

    public String getDestText() {
      return destText;
    }

    public void setDestText(String destText) {
      this.destText = destText;
    }

    public int getDestNumber() {
      return destNumber;
    }

    public void setDestNumber(int destNumber) {
      this.destNumber = destNumber;
    }
  }

  @BeforeMethod
  public void setUp() {
    modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setImplicitMappingEnabled(false);
  }

  public void shouldAddMapping() {
    TypeMap<Src, Dest> typeMap = modelMapper.createTypeMap(Src.class, Dest.class);
    typeMap.addMapping(srcTextGetter(), destTextSetter());
    typeMap.addMapping(srcNumberGetter(), destNumberSetter());

    typeMap.validate();

    Dest dest = typeMap.map(new Src("foo", 3));
    assertEquals(dest.destText, "foo");
    assertEquals(dest.destNumber, 3);
  }

  public void shouldAddMappings() {
    TypeMap<Src, Dest> typeMap = modelMapper.createTypeMap(Src.class, Dest.class);
    typeMap.addMappings(new ExpressionMap<Src, Dest>() {
      public void configure(ConfigurableMapExpression<Src, Dest> mapping) {
        mapping.map(srcTextGetter(), destTextSetter());
        mapping.map(srcNumberGetter(), destNumberSetter());
      }
    });

    typeMap.validate();

    Dest dest = typeMap.map(new Src("foo", 3));
    assertEquals(dest.destText, "foo");
    assertEquals(dest.destNumber, 3);
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
            }).map(srcTextGetter(), destTextSetter());
            mapping.using(new Converter<Integer, Integer>() {
              public Integer convert(MappingContext<Integer, Integer> context) {
                return context.getSource() + 1;
              }
            }).map(srcNumberGetter(), destNumberSetter());
          }
        });

    typeMap.validate();

    Dest dest = typeMap.map(new Src("foo", 3));
    assertEquals(dest.destText, "FOO");
    assertEquals(dest.destNumber, 4);
  }

  public void shouldAddMappingWithSkip() {
    TypeMap<Src, Dest> typeMap = modelMapper.createTypeMap(Src.class, Dest.class);
    typeMap.addMappings(
        new ExpressionMap<Src, Dest>() {
          public void configure(ConfigurableMapExpression<Src, Dest> mapping) {
            mapping.skip(destTextSetter());
            mapping.map(srcNumberGetter(), destNumberSetter());
          }
        });

    typeMap.validate();

    Dest dest = typeMap.map(new Src("foo", 3));
    assertNull(dest.destText);
    assertEquals(dest.destNumber, 3);
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
            }).map(srcTextGetter(), destTextSetter());
            mapping.when(new Condition<Integer, Integer>() {
              public boolean applies(MappingContext<Integer, Integer> context) {
                return context.getSource() > 3;
              }
            }).map(srcNumberGetter(), destNumberSetter());
          }
        });

    typeMap.validate();

    Dest dest1 = typeMap.map(new Src("bar", 3));
    assertNull(dest1.destText);
    assertEquals(dest1.destNumber, 0);

    Dest dest2 = typeMap.map(new Src("foo", 4));
    assertEquals(dest2.destText, "foo");
    assertEquals(dest2.destNumber, 4);
  }


  private static SourceGetter<Src> srcTextGetter() {
    return new SourceGetter<Src>() {
      public Object get(Src source) {
        return source.getSrcText();
      }
    };
  }


  private static SourceGetter<Src> srcNumberGetter() {
    return new SourceGetter<Src>() {
      public Object get(Src source) {
        return source.getSrcNumber();
      }
    };
  }

  private static DestinationSetter<Dest, String> destTextSetter() {
    return new DestinationSetter<Dest, String>() {
      public void accept(Dest destination, String value) {
        destination.setDestText(value);
      }
    };
  }

  private static DestinationSetter<Dest, Integer> destNumberSetter() {
    return new DestinationSetter<Dest, Integer>() {
      public void accept(Dest destination, Integer value) {
        destination.setDestNumber(value);
      }
    };
  }
}
