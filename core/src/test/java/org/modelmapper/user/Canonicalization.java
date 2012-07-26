package org.modelmapper.user;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

/**
 * From http://stackoverflow.com/questions/11097748/canonicalizing-java-bean-property-names
 */
@Test(enabled = false)
public class Canonicalization extends AbstractTest {
  public class Foo {
    String id;

    String getReferenceID() {
      return id;
    }
  }

  public class Bar {
    String id;

    void setRefID(String id) {
      this.id = id;
    }
  }

  public void test() {
    modelMapper.createTypeMap(Foo.class, Bar.class).setPropertyConverter(
        new Converter<Object, Object>() {
          public Object convert(MappingContext<Object, Object> context) {
            return null;
          }
        });

    Foo foo = new Foo();
    foo.id = "test";
    Bar bar = modelMapper.map(foo, Bar.class);
    assertEquals(foo.id, bar.id);
  }
}
