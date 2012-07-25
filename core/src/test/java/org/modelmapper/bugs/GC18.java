package org.modelmapper.bugs;

import static org.testng.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * http://code.google.com/p/modelmapper/issues/detail?id=18
 * 
 * Disabled for now since this does not appear to be a bug.
 */
@Test(enabled = false)
public class GC18 extends AbstractTest {
  static class A {
    List<SubA> subs;

    public List<SubA> getSubs() {
      return subs;
    }
  }

  static class SubA {
    String text;
    String name;

    public String getText() {
      return text;
    }

    public String getName() {
      return name;
    }
  }

  static class B {
    List<SubB> subz;

    public void setSubz(List<SubB> subz) {
      this.subz = subz;
    }
  }

  static class SubB {
    SubSubB sub;

    public SubSubB getSub() {
      return sub;
    }

    public void setSub(SubSubB sub) {
      this.sub = sub;
    }
  }

  static class SubSubB {
    String otherText;
    String otherName;

    public void setOtherName(String otherName) {
      this.otherName = otherName;
    }

    public void setOtherText(String otherText) {
      this.otherText = otherText;
    }
  }

  public void test() {
    modelMapper.addMappings(new PropertyMap<SubA, SubB>() {
      @Override
      protected void configure() {
        map().getSub().setOtherName(source.getName());
        map().getSub().setOtherText(source.getText());
      }
    });

    modelMapper.addMappings(new PropertyMap<A, B>() {
      @Override
      protected void configure() {
        map(source.getSubs()).setSubz(null);
      }
    });

    SubA subA1 = new SubA();
    subA1.name = "name1";
    subA1.text = "text1";
    SubB subB1 = modelMapper.map(subA1, SubB.class);
    assertEquals(subA1.name, subB1.sub.otherName);
    assertEquals(subA1.text, subB1.sub.otherText);

    SubA subA2 = new SubA();
    subA2.name = "name2";
    subA2.text = "text2";
    SubB subB2 = modelMapper.map(subA2, SubB.class);
    assertEquals(subA2.name, subB2.sub.otherName);
    assertEquals(subA2.text, subB2.sub.otherText);

    A a = new A();
    a.subs = Arrays.asList(subA1, subA2);

    B b = modelMapper.map(a, B.class);
    assertFalse(b.subz.get(0).equals(b.subz.get(1)));
  }
}
