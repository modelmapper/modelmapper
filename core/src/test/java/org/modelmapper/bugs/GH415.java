package org.modelmapper.bugs;

import org.modelmapper.AbstractTest;
import org.modelmapper.TypeToken;
import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.spi.SourceGetter;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test
public class GH415 extends AbstractTest {
  interface AInterface {

  }

  static class A<T extends AInterface> {
    private int a;
    private T pojo;

    public int getA() {
      return a;
    }

    public void setA(int a) {
      this.a = a;
    }

    public T getPojo() {
      return pojo;
    }

    public void setPojo(T pojo) {
      this.pojo = pojo;
    }
  }

  static class AProperties implements AInterface {
    String s;
  }

  interface BInterface {

  }

  static class B<T extends BInterface> {
    private int a;
    private T pojo;

    public int getA() {
      return a;
    }

    public void setA(int a) {
      this.a = a;
    }

    public T getPojo() {
      return pojo;
    }

    public void setPojo(T pojo) {
      this.pojo = pojo;
    }
  }

  static class BProperties implements BInterface {
    String s;
  }

  public void shouldMap() {
    AProperties aProperties = new AProperties();
    aProperties.s = "foo";
    A<AProperties> src = new A<AProperties>();
    src.a = 1;
    src.pojo = aProperties;

    modelMapper.typeMap(AProperties.class, BProperties.class)
        .include(BInterface.class);
    modelMapper.typeMap(A.class, B.class)
        .addMapping(new SourceGetter<A>() {
          @Override
          public Object get(A source) {
            return source.getPojo();
          }
        }, new DestinationSetter<B, BInterface>() {
          @Override
          @SuppressWarnings("unchecked")
          public void accept(B destination, BInterface value) {
            destination.setPojo(value);
          }
        });
    B<BProperties> destination = modelMapper.map(src,
        new TypeToken<B<BProperties>>(){}.getType());
    assertEquals(destination.a, 1);
    assertEquals(destination.pojo.s, "foo");
  }
}
