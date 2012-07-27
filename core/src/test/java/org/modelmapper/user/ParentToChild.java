package org.modelmapper.user;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * From <a href=
 * "http://stackoverflow.com/questions/11605446/how-to-map-parent-field-to-child-object-field-using-dozer"
 * >lnk</a>
 */
@Test(enabled = false)
public class ParentToChild extends AbstractTest {
  static class A {
    int indicatorA;
    List<A> listA;

    List<A> getListA() {
      return listA;
    }
  }

  static class B {
    int indicatorB;
    List<B> listB;

    void setListB(List<B> listB) {
      this.listB = listB;
    }
  }

  public void test() {
    A parentA = new A();
    parentA.indicatorA = 10;
    parentA.listA = new ArrayList<A>();

    A child1A = new A();
    A child2A = new A();
    parentA.listA.add(child1A);
    parentA.listA.add(child2A);

    modelMapper.addMappings(new PropertyMap<A, B>() {
      @Override
      protected void configure() {
        map(source.getListA()).setListB(null);
      }
    });

    modelMapper.map(parentA, B.class);
  }
}
