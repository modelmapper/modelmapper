package org.modelmapper.functional.lambda;

import org.modelmapper.AbstractTest;
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

import static org.testng.Assert.assertEquals;

@Test
public class TypeMapLambdaFromSourceTest extends AbstractTest {
  static class Src {
    String firstName;
    String lastName;

    Src(String firstName, String lastName) {
      this.firstName = firstName;
      this.lastName = lastName;
    }

    String getFirstName() {
      return firstName;
    }

    String getLastName() {
      return lastName;
    }
  }

  static class Dest {
    String name;

    String getName() {
      return name;
    }

    void setName(String name) {
      this.name = name;
    }
  }

  @BeforeMethod
  public void setUp() {
    modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setImplicitMappingEnabled(false);
  }

  public void shouldMappingSource() {
    TypeMap<Src, Dest> typeMap = modelMapper.typeMap(Src.class, Dest.class)
        .addMappings(new ExpressionMap<Src, Dest>() {
          @Override
          public void configure(ConfigurableConditionExpression<Src, Dest> mapping) {
            mapping.using(new Converter<Src, String>() {
              @Override
              public String convert(MappingContext<Src, String> context) {
                return String.format("%s %s", context.getSource().getFirstName(),
                    context.getSource().getLastName());
              }
            }).map(srcGetter(), destSetter());
          }
        });

    typeMap.validate();
    assertEquals(typeMap.map(new Src("Andy", "Lin")).name, "Andy Lin");
  }

  private static SourceGetter<Src> srcGetter() {
    return new SourceGetter<Src>() {
      public Object get(Src source) {
        return source;
      }
    };
  }

  private static DestinationSetter<Dest, String> destSetter() {
    return new DestinationSetter<Dest, String>() {
      public void accept(Dest destination, String value) {
        destination.setName(value);
      }
    };
  }
}
