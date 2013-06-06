package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.testng.annotations.Test;

/**
 * http://code.google.com/p/modelmapper/issues/detail?id=10
 */
@Test
public class GC10 {
  static class FromOuter {
    FromInner inner;

    public FromOuter() {
    }

    public FromOuter(Integer one, Integer two, Integer three) {
      inner = new FromInner(one, two, three);
    }

    FromInner getInner() {
      return inner;
    }
  }

  static class FromInner {
    Integer one;
    Integer two;
    Integer three;

    public FromInner() {
    }

    public FromInner(Integer one, Integer two, Integer three) {
      this.one = one;
      this.two = two;
      this.three = three;
    }

    public Integer getOne() {
      return one;
    }

    public void setOne(Integer one) {
      this.one = one;
    }

    public Integer getTwo() {
      return two;
    }

    public void setTwo(Integer two) {
      this.two = two;
    }

    public Integer getThree() {
      return three;
    }

    public void setThree(Integer three) {
      this.three = three;
    }
  }

  static class ToOuter {
    ToInner inner;

    public ToInner getInner() {
      return inner;
    }

    public void setInner(ToInner inner) {
      this.inner = inner;
    }
  }

  static class ToInner {
    String a;
    String b;
    String c;

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

    public String getC() {
      return c;
    }

    public void setC(String c) {
      this.c = c;
    }
  }

  public void shouldSupportMultipleSourceMappings() {
    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    mapper.getConfiguration().setAmbiguityIgnored(true);

    mapper.addMappings(new PropertyMap<FromOuter, ToOuter>() {
      protected void configure() {
        map(source.getInner().getOne()).getInner().setA(null);
        map(source.getInner().getTwo()).getInner().setB(null);
        map(source.getInner().getThree()).getInner().setC(null);
      }
    });

    FromOuter from = new FromOuter(1, 2, 3);
    ToOuter to = mapper.map(from, ToOuter.class);

    assertEquals(to.inner.a, "1");
    assertEquals(to.inner.b, "2");
    assertEquals(to.inner.c, "3");
  }
}
