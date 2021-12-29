package org.modelmapper.functional.lambda;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class TypeMapLambdaTypeConvertTest extends AbstractTest {
  static class StringWrap {
    private String value;

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }

  static class IntegerWrap {
    private int value;

    public int getValue() {
      return value;
    }

    public void setValue(int value) {
      this.value = value;
    }
  }

  @BeforeMethod
  public void setUp() {
    modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setImplicitMappingEnabled(false);
  }

  public void shouldAddMappingStringToInteger() {
    TypeMap<StringWrap, IntegerWrap> typeMap = modelMapper.createTypeMap(StringWrap.class, IntegerWrap.class);

    typeMap.addMapping(StringWrap::getValue, IntegerWrap::setValue);

    StringWrap src = new StringWrap();
    src.setValue("3");
    assertEquals(modelMapper.map(src, IntegerWrap.class).value, 3);
  }

  public void shouldAddMappingIntegerToString() {
    TypeMap<IntegerWrap, StringWrap> typeMap = modelMapper.createTypeMap(IntegerWrap.class, StringWrap.class);
    typeMap.addMapping(IntegerWrap::getValue, StringWrap::setValue);

    IntegerWrap src = new IntegerWrap();
    src.setValue(3);
    assertEquals(modelMapper.map(src, StringWrap.class).value, "3");
  }
}
