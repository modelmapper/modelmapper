package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.Provider;
import org.testng.annotations.Test;

@Test
public class GH74 extends AbstractTest {
  static class Foo {
    Bar bar;
  }

  static class Bar {
    String value;
  }

  static class FooDTO {
    BarDTO bar;
  }

  static class BarDTO {
    String value;
  }

  public void test() {
    modelMapper.getConfiguration().setProvider(new Provider<Object>() {
      public Object get(ProvisionRequest<Object> request) {
        return null;
      }
    });

    Foo foo = new Foo();
    foo.bar = new Bar();
    foo.bar.value = "test";
    FooDTO dto = modelMapper.map(foo, FooDTO.class);
    assertEquals(dto.bar.value, foo.bar.value);
  }
}
