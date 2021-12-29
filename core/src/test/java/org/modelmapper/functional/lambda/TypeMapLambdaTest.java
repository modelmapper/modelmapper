package org.modelmapper.functional.lambda;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import org.modelmapper.AbstractTest;
import org.modelmapper.Asserts;
import org.modelmapper.Conditions;
import org.modelmapper.ConfigurationException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.spi.MappingContext;
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
    typeMap.addMapping(Src::getSrcText, Dest::setDestText);

    typeMap.validate();
    assertEquals(typeMap.map(new Src("foo")).destText, "foo");
  }

  public void shouldAddMappingWithConverter() {
    TypeMap<Src, Dest> typeMap = modelMapper.createTypeMap(Src.class, Dest.class);
    typeMap.addMappings(mapping -> mapping.using((MappingContext<String, String> context) -> context.getSource().toUpperCase())
        .map(Src::getSrcText, Dest::setDestText));

    typeMap.validate();
    assertEquals(typeMap.map(new Src("foo")).destText, "FOO");
  }

  public void shouldAddMappingWithSkip() {
    TypeMap<Src, Dest> typeMap = modelMapper.createTypeMap(Src.class, Dest.class);
    typeMap.addMappings(mapping -> mapping.skip(Dest::setDestText));

    typeMap.validate();
    assertNull(typeMap.map(new Src("foo")).destText);
  }

  public void shouldAddMappingWithConditionalSkip() {
    TypeMap<Src, Dest> typeMap = modelMapper.createTypeMap(Src.class, Dest.class);
    typeMap.addMappings(mapping -> mapping.when(Conditions.isNull()).skip(Src::getSrcText, Dest::setDestText));
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
      typeMap.addMappings(mapping -> mapping.when(Conditions.isNull()).skip(Dest::setDestText));
      fail();
    } catch (ConfigurationException e) {
      Asserts.assertContains(e.getMessage(),
          "Source properties must be provided when conditional skip, please use when().skip(sourceGetter, destinationSetter) instead");
    }
  }

  public void shouldAddMappingWithCondition() {
    TypeMap<Src, Dest> typeMap = modelMapper.createTypeMap(Src.class, Dest.class);
    typeMap.addMappings(mapping -> mapping.when((MappingContext<String, String> context) -> context.getSource().equals("foo"))
        .map(Src::getSrcText, Dest::setDestText));

    typeMap.validate();
    assertNull(typeMap.map(new Src("bar")).destText);
    assertEquals(typeMap.map(new Src("foo")).destText, "foo");
  }

  public void shouldFailedWithEmptyDestinationSetter() {
    TypeMap<Src, Dest> typeMap = modelMapper.createTypeMap(Src.class, Dest.class);

    try {
      typeMap.addMapping(Src::getSrcText, (destination, value) -> {});
      fail();
    } catch (ConfigurationException e) {
      Asserts.assertContains(e.getMessage(), "Illegal DestinationSetter defined");
    }
  }
}
