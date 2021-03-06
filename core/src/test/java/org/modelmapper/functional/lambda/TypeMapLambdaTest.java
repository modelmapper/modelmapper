package org.modelmapper.functional.lambda;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import org.modelmapper.AbstractTest;
import org.modelmapper.Asserts;
import org.modelmapper.Condition;
import org.modelmapper.Conditions;
import org.modelmapper.ConfigurationException;
import org.modelmapper.Converter;
import org.modelmapper.ExpressionMap;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.builder.ConfigurableConditionExpression;
import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.SourceGetter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
@SuppressWarnings("unused")
public class TypeMapLambdaTest extends AbstractTest {
  static class Src {
    String srcText;

    Src(String srcText) {
      this.srcText = srcText;
    }

    String getSrcText() {
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

    void setDestText(String destText) {
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
          public void configure(ConfigurableConditionExpression<Src, Dest> mapping) {
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
          public void configure(ConfigurableConditionExpression<Src, Dest> mapping) {
            mapping.skip(destSetter());
          }
        });

    typeMap.validate();
    assertNull(typeMap.map(new Src("foo")).destText);
  }

  public void shouldAddMappingWithConditionalSkip() {
    TypeMap<Src, Dest> typeMap = modelMapper.createTypeMap(Src.class, Dest.class);
    typeMap.addMappings(
        new ExpressionMap<Src, Dest>() {
          public void configure(ConfigurableConditionExpression<Src, Dest> mapping) {
            mapping.when(Conditions.isNull()).skip(srcGetter(), destSetter());
          }
        });
    typeMap.validate();

    Dest dest = new Dest();
    typeMap.map(new Src("foo"), dest);
    assertEquals(dest.destText, "foo");
    typeMap.map(new Src(null), dest);
    assertEquals(dest.destText, "foo");
  }

  public void shouldFailedWhenConditionalSkipWithoutSourceGetter() {
    TypeMap<Src, Dest> typeMap = modelMapper.createTypeMap(Src.class, Dest.class);

    try {
      typeMap.addMappings(
          new ExpressionMap<Src, Dest>() {
            public void configure(ConfigurableConditionExpression<Src, Dest> mapping) {
              mapping.when(Conditions.isNull()).skip(destSetter());
            }
          });
      fail();
    } catch (ConfigurationException e) {
      Asserts.assertContains(e.getMessage(),
          "Source properties must be provided when conditional skip, please use when().skip(sourceGetter, destinationSetter) instead");
    }
  }

  public void shouldAddMappingWithCondition() {
    TypeMap<Src, Dest> typeMap = modelMapper.createTypeMap(Src.class, Dest.class);
    typeMap.addMappings(
        new ExpressionMap<Src, Dest>() {
          public void configure(ConfigurableConditionExpression<Src, Dest> mapping) {
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
