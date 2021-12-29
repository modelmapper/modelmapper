package org.modelmapper.functional.lambda;

import org.modelmapper.AbstractTest;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test
public class TypeMapLambdaFromConstantTest extends AbstractTest {
  static class Src {
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
        .addMappings(mapping -> mapping.map(source -> "Andy Lin", Dest::setName));

    typeMap.validate();
    assertEquals(typeMap.map(new Src()).name, "Andy Lin");
  }
}
