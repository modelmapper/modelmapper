package org.modelmapper.functional.lambda;

import org.modelmapper.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

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
    typeMap.addMapping(Src::getSrcText, Dest::setDestText);
    typeMap.addMapping(Src::getSrcNumber, Dest::setDestNumber);

    typeMap.validate();

    Dest dest = typeMap.map(new Src("foo", 3));
    assertEquals(dest.destText, "foo");
    assertEquals(dest.destNumber, 3);
  }

  public void shouldAddMappings() {
    TypeMap<Src, Dest> typeMap = modelMapper.createTypeMap(Src.class, Dest.class);
    typeMap.addMappings(mapping -> {
      mapping.map(Src::getSrcText, Dest::setDestText);
      mapping.map(Src::getSrcNumber, Dest::setDestNumber);
    });

    typeMap.validate();

    Dest dest = typeMap.map(new Src("foo", 3));
    assertEquals(dest.destText, "foo");
    assertEquals(dest.destNumber, 3);
  }

  public void shouldAddMappingWithConverter() {
    TypeMap<Src, Dest> typeMap = modelMapper.createTypeMap(Src.class, Dest.class);
    typeMap.addMappings(
        mapping -> {
          mapping.using((Converter<String, String>) context -> context.getSource().toUpperCase())
              .map(Src::getSrcText, Dest::setDestText);
          mapping.using((Converter<Integer, Integer>) context -> context.getSource() + 1)
              .map(Src::getSrcNumber, Dest::setDestNumber);
        });

    typeMap.validate();

    Dest dest = typeMap.map(new Src("foo", 3));
    assertEquals(dest.destText, "FOO");
    assertEquals(dest.destNumber, 4);
  }

  public void shouldAddMappingWithSkip() {
    TypeMap<Src, Dest> typeMap = modelMapper.createTypeMap(Src.class, Dest.class);
    typeMap.addMappings(
        mapping -> {
          mapping.skip(Dest::setDestText);
          mapping.map(Src::getSrcNumber, Dest::setDestNumber);
        });

    typeMap.validate();

    Dest dest = typeMap.map(new Src("foo", 3));
    assertNull(dest.destText);
    assertEquals(dest.destNumber, 3);
  }

  public void shouldAddMappingWithCondition() {
    TypeMap<Src, Dest> typeMap = modelMapper.createTypeMap(Src.class, Dest.class);
    typeMap.addMappings(
        mapping -> {
          mapping.when((Condition<String, String>) context -> context.getSource().equals("foo")).map(Src::getSrcText, Dest::setDestText);
          mapping.when((Condition<Integer, Integer>) context -> context.getSource() > 3).map(Src::getSrcNumber, Dest::setDestNumber);
        });

    typeMap.validate();

    Dest dest1 = typeMap.map(new Src("bar", 3));
    assertNull(dest1.destText);
    assertEquals(dest1.destNumber, 0);

    Dest dest2 = typeMap.map(new Src("foo", 4));
    assertEquals(dest2.destText, "foo");
    assertEquals(dest2.destNumber, 4);
  }
}
