package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import org.modelmapper.ModelMapper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class GH234 {
  static class A {
    private String testGooGo;

    public String getTestGooGo() {
      return testGooGo;
    }

    public void setTestGooGo(String testGooGo) {
      this.testGooGo = testGooGo;
    }
  }

  static class B {
    private String testGooGo;

    public String getTestGooGo() {
      return testGooGo;
    }

    public void setTestGooGo(String testGooGo) {
      this.testGooGo = testGooGo;
    }
  }

  private ModelMapper modelMapper;

  @BeforeMethod
  public void setUp() {
    modelMapper = new ModelMapper();
  }

  public void shouldMap() {
    A a = new A();
    a.setTestGooGo("foo");

    assertEquals(modelMapper.map(a, B.class).getTestGooGo(), "foo");
  }
}
