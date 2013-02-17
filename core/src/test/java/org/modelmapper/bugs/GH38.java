package org.modelmapper.bugs;

import org.mockito.Mockito;
import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * https://github.com/jhalterman/modelmapper/issues/38
 */
@Test
public class GH38 extends AbstractTest {
  public static class A {
    private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  public static class B {
    private String nameProp;

    public String getNameProp() {
      return nameProp;
    }

    public void setNameProp(String nameProp) {
      this.nameProp = nameProp;
    }
  }

  public void test() {
    modelMapper.addMappings(new PropertyMap<A, B>() {
      @Override
      protected void configure() {
        map().setNameProp(source.getName());
      }
    });

    A a = Mockito.mock(A.class);
    Mockito.when(a.getName()).thenReturn("hello");
    B b = modelMapper.map(a, B.class);

    Assert.assertEquals("hello", b.getNameProp());
  }
}
