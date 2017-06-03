package org.modelmapper.functional.inherit;

import org.modelmapper.AbstractProvider;
import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.modelmapper.PropertyMapProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

@Test
public class ExplicitMapInheritTest extends AbstractTest {

  interface SrcInterface {
    String getSrc();
  }

  static class SrcA implements SrcInterface {
    String src;

    public SrcA(String src) {
      this.src = src;
    }

    public String getSrc() {
      return src;
    }
  }

  static class SrcB implements SrcInterface {
    String src;

    public SrcB(String src) {
      this.src = src;
    }

    public String getSrc() {
      return src;
    }
  }

  static class SrcC extends SrcB {
    public SrcC(String src) {
      super(src);
    }
  }

  interface DestInterface {
    void setDest(String dest);
  }

  static class DestA implements DestInterface {
    String dest;

    public void setDest(String dest) {
      this.dest = dest;
    }
  }

  static class DestB implements DestInterface {
    String dest;

    public void setDest(String dest) {
      this.dest = dest;
    }
  }

  static class DestC extends DestB {
  }

  static class MappingProvider implements PropertyMapProvider<SrcInterface, DestInterface> {
    public <S extends SrcInterface, D extends DestInterface> PropertyMap<S, D> provide(Class<S> sourceType, Class<D> destinationType) {
      return new PropertyMap<S, D>(sourceType, destinationType) {
        @Override
        protected void configure() {
          map().setDest(source.getSrc());
        }
      };
    }
  }

  public void shouldMappingClassSuccess() {
    PropertyMapProvider<SrcInterface, DestInterface> provider = new MappingProvider();

    modelMapper.addMappings(provider.provide(SrcA.class, DestA.class));
    modelMapper.addMappings(provider.provide(SrcB.class, DestB.class));
    modelMapper.addMappings(provider.provide(SrcC.class, DestC.class));

    assertEquals(modelMapper.map(new SrcA("foo"), DestA.class).dest, "foo");
    assertEquals(modelMapper.map(new SrcB("foo"), DestB.class).dest, "foo");
    assertEquals(modelMapper.map(new SrcC("foo"), DestC.class).dest, "foo");

    assertNull(modelMapper.map(new SrcA("foo"), DestB.class).dest);
    assertNull(modelMapper.map(new SrcA("foo"), DestC.class).dest);
    assertNull(modelMapper.map(new SrcC("foo"), DestB.class).dest);
  }

  public void shouldMappingInterfaceSuccess() {
    PropertyMapProvider<SrcInterface, DestInterface> provider = new MappingProvider();

    modelMapper.addMappings(provider.provide(SrcA.class, DestInterface.class)).setProvider(new AbstractProvider<DestInterface>() {
      protected DestInterface get() {
        return new DestA();
      }
    });

    DestInterface dest = modelMapper.map(new SrcA("foo"), DestInterface.class);
    assertTrue(dest instanceof DestA);
    assertEquals(((DestA) dest).dest, "foo");
  }
}
