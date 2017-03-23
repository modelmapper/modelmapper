package org.modelmapper.functional.inherit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.modelmapper.Provider;
import org.testng.annotations.Test;

@Test
public class TypeMapIncludeBaseTest extends AbstractTest {

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

  public void shouldMappingClassSuccess() {
    modelMapper.addMappings(new PropertyMap<SrcInterface, DestInterface>() {
          @Override
          protected void configure() {
            map().setDest(source.getSrc());
          }
        });

    modelMapper.createTypeMap(SrcA.class, DestA.class).includeBase(SrcInterface.class, DestInterface.class);
    modelMapper.createTypeMap(SrcB.class, DestB.class).includeBase(SrcInterface.class, DestInterface.class);
    modelMapper.createTypeMap(SrcC.class, DestC.class).includeBase(SrcInterface.class, DestInterface.class);

    assertEquals(modelMapper.map(new SrcA("foo"), DestA.class).dest, "foo");
    assertEquals(modelMapper.map(new SrcB("foo"), DestB.class).dest, "foo");
    assertEquals(modelMapper.map(new SrcC("foo"), DestC.class).dest, "foo");

    assertNull(modelMapper.map(new SrcA("foo"), DestB.class).dest);
    assertNull(modelMapper.map(new SrcA("foo"), DestC.class).dest);
    assertNull(modelMapper.map(new SrcC("foo"), DestB.class).dest);
  }

  public void shouldMappingInterfaceSuccess() {
    modelMapper.addMappings(new PropertyMap<SrcInterface, DestInterface>() {
          @Override
          protected void configure() {
            map().setDest(source.getSrc());
          }
        });

    modelMapper.createTypeMap(SrcA.class, DestInterface.class)
        .includeBase(SrcInterface.class, DestInterface.class)
        .setProvider(new Provider<DestInterface>() {
          public DestInterface get(ProvisionRequest<DestInterface> request) {
            return new DestA();
          }
    });

    DestInterface dest = modelMapper.map(new SrcA("foo"), DestInterface.class);
    assertTrue(dest instanceof DestA);
    assertEquals(((DestA) dest).dest, "foo");
  }
}