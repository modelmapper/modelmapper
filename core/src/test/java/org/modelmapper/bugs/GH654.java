package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.modelmapper.AbstractTest;
import org.modelmapper.ConfigurationException;
import org.modelmapper.spi.ErrorMessage;
import org.testng.annotations.Test;

@Test
public class GH654 extends AbstractTest {

  static class A {
    private String field1;
    private String field2;
    private B b;

    public String getField1() {
      return field1;
    }

    public void setField1(String field1) {
      this.field1 = field1;
    }

    public String getField2() {
      return field2;
    }

    public void setField2(String field2) {
      this.field2 = field2;
    }

    public B getB() {
      return b;
    }

    public void setB(B b) {
      this.b = b;
    }
  }

  static class B {
    private String field1;
    private String field2;

    public String getField1() {
      return field1;
    }

    public void setField1(String field1) {
      this.field1 = field1;
    }

    public String getField2() {
      return field2;
    }

    public void setField2(String field2) {
      this.field2 = field2;
    }
  }

  private static class ADto {
    private String field1;
    private String field2;
    private BDto b;

    public String getField1() {
      return field1;
    }

    public void setField1(String field1) {
      this.field1 = field1;
    }

    public String getField2() {
      return field2;
    }

    public void setField2(String field2) {
      this.field2 = field2;
    }

    public BDto getB() {
      return b;
    }

    public void setB(BDto b) {
      this.b = b;
    }
  }

  static class BDto {
    private String field1;
    private String field2;

    public String getField1() {
      return field1;
    }

    public void setField1(String field1) {
      this.field1 = field1;
    }

    public String getField2() {
      return field2;
    }

    public void setField2(String field2) {
      this.field2 = field2;
    }
  }

  public void testSkipConflict() {
    try {
      modelMapper.typeMap(A.class, ADto.class)
          .addMappings(mapper -> mapper.skip(ADto::setB));
      fail();
    } catch (ConfigurationException e) {
      assertEquals(e.getErrorMessages().size(), 1);
      assertEquals(e.getErrorMessages().iterator().next().getMessage(),
          "Not able to skip b., because there are already nested properties are mapped: [b.field1. b.field2.]. "
              + "Do you skip the property after the implicit mappings mapped? "
              + "We recommended you to create an empty type map, and followed by addMappings and implicitMappings calls");
    }
  }
}
