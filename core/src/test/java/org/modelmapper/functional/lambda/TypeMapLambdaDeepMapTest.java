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
import org.modelmapper.ExpressionMap;
import org.modelmapper.ModelMapper;
import org.modelmapper.Provider;
import org.modelmapper.TypeMap;
import org.modelmapper.builder.ConfigurableMapExpression;
import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.SourceGetter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class TypeMapLambdaDeepMapTest extends AbstractTest {
  static class ParentSrc {
    Src src;

    public ParentSrc(Src src) {
      this.src = src;
    }

    public Src getSrc() {
      return src;
    }

    public void setSrc(Src src) {
      this.src = src;
    }
  }

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

  static class ParentDest {
    Dest dest;

    public Dest getDest() {
      return dest;
    }

    public void setDest(Dest dest) {
      this.dest = dest;
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

  public void shouldAddMappingDeeply() {
    TypeMap<ParentSrc, ParentDest> typeMap = modelMapper.createTypeMap(ParentSrc.class, ParentDest.class);
    typeMap.addMapping(parentSrcGetter(), parentDestSetter());

    typeMap.validate();
    assertEquals(typeMap.map(new ParentSrc(new Src("bar"))).getDest().destText, "bar");
  }

  public void shouldAddMappingWithProvider() {
    final Dest dest = new Dest();

    TypeMap<ParentSrc, ParentDest> typeMap = modelMapper.createTypeMap(ParentSrc.class, ParentDest.class);
    typeMap.addMappings(
        new ExpressionMap<ParentSrc, ParentDest>() {
          public void configure(ConfigurableMapExpression<ParentSrc, ParentDest> mapping) {
            mapping.with(new Provider<Dest>() {
              public Dest get(ProvisionRequest<Dest> request) {
                return dest;
              }
            }).map(new SourceGetter<ParentSrc>() {
              public Object get(ParentSrc source) {
                return source.getSrc();
              }
            }, new DestinationSetter<ParentDest, Dest>() {
              public void accept(ParentDest destination, Dest value) {
                destination.setDest(value);
              }
            });
          }
        });

    typeMap.validate();
    assertSame(typeMap.map(new ParentSrc(new Src("foo"))).dest, dest);
  }

  private static SourceGetter<ParentSrc> parentSrcGetter() {
    return new SourceGetter<ParentSrc>() {
      public Object get(ParentSrc source) {
        return source.getSrc().getSrcText();
      }
    };
  }

  private static SourceGetter<Src> srcGetter() {
    return new SourceGetter<Src>() {
      public Object get(Src source) {
        return source.getSrcText();
      }
    };
  }

  private static DestinationSetter<ParentDest, String> parentDestSetter() {
    return new DestinationSetter<ParentDest, String>() {
      public void accept(ParentDest destination, String value) {
        destination.getDest().setDestText(value);
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
