package org.modelmapper.functional.inherit;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

@Test
public class MultipleInterfacesTest extends AbstractTest {

  interface InterfaceA {
    String getA();

    void setA(String a);
  }

  interface InterfaceB {
    String getB();

    void setB(String b);
  }

  interface InterfaceC {
    String getC();

    void setC(String c);
  }

  interface InterfaceD {
    String getD();

    void setD(String d);
  }

  static class Source implements InterfaceA, InterfaceB {
    String a;
    String b;

    public Source(String a, String b) {
      this.a = a;
      this.b = b;
    }

    public String getA() {
      return a;
    }

    public void setA(String a) {
      this.a = a;
    }

    public String getB() {
      return b;
    }

    public void setB(String b) {
      this.b = b;
    }
  }

  static class Destination implements InterfaceC, InterfaceD {
    String c;
    String d;

    public String getC() {
      return c;
    }

    public void setC(String c) {
      this.c = c;
    }

    public String getD() {
      return d;
    }

    public void setD(String d) {
      this.d = d;
    }
  }

  public void shouldIncludeBaseTypeMaps() {
    modelMapper.addMappings(new PropertyMap<InterfaceA, InterfaceC>() {
      @Override
      protected void configure() {
        map().setC(source.getA());
      }
    });
    modelMapper.addMappings(new PropertyMap<InterfaceB, InterfaceD>() {
      @Override
      protected void configure() {
        map().setD(source.getB());
      }
    });

    modelMapper.createTypeMap(Source.class, Destination.class)
        .includeBase(InterfaceA.class, InterfaceC.class)
        .includeBase(InterfaceB.class, InterfaceD.class);

    Destination destination = modelMapper.map(new Source("foo", "bar"), Destination.class);
    assertEquals(destination.getC(), "foo");
    assertEquals(destination.getD(), "bar");
  }
}
