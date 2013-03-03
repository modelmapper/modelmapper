package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * https://github.com/jhalterman/modelmapper/issues/37
 */
@Test
public class GH37 extends AbstractTest {
  static class Source {
    String one;
    String two;
    String three;
    String four;

    public String getOne() {
      return one;
    }

    public void setOne(String one) {
      this.one = one;
    }

    public String getTwo() {
      return two;
    }

    public void setTwo(String two) {
      this.two = two;
    }

    public String getThree() {
      return three;
    }

    public void setThree(String three) {
      this.three = three;
    }

    public String getFour() {
      return four;
    }

    public void setFour(String four) {
      this.four = four;
    }

  }

  static class Destination {
    String one;
    String two;
    String v3;
    String v4;

    public String getOne() {
      return one;
    }

    public void setOne(String one) {
      this.one = one;
    }

    public String getTwo() {
      return two;
    }

    public void setTwo(String two) {
      this.two = two;
    }

    public String getV3() {
      return v3;
    }

    public void setV3(String v3) {
      this.v3 = v3;
    }

    public String getV4() {
      return v4;
    }

    public void setV4(String v4) {
      this.v4 = v4;
    }
  }

  public void test() {
    modelMapper.addMappings(new PropertyMap<Source, Destination>() {
      protected void configure() {
        skip().setOne(null);
        map().setV3(source.getThree());
        map(source.getTwo()).setTwo(null);
        map(source.getFour()).setV4(null);
      }
    });

    Source source = new Source();
    source.setOne("1");
    source.setTwo("2");
    source.setThree("3");
    source.setFour("4");

    Destination dest = modelMapper.map(source, Destination.class);

    assertEquals(dest.one, null);
    assertEquals(dest.two, source.two);
    assertEquals(dest.v3, source.three);
    assertEquals(dest.v4, source.four);
  }
}
