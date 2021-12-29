package org.modelmapper.functional.lambda;

import org.modelmapper.AbstractTest;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.spi.MappingContext;
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
        .addMappings(mapping -> mapping
            .using((MappingContext<Src, String> context) -> String.format("%s %s",
                context.getSource().getFirstName(),
                context.getSource().getLastName()))
            .map(source -> source, Dest::setName));

    typeMap.validate();
    assertEquals(typeMap.map(new Src("Andy", "Lin")).name, "Andy Lin");
  }
}
