package org.modelmapper.functional.inherit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.modelmapper.*;
import org.testng.annotations.Test;

@Test
public class TypeMapIncludeTest extends AbstractTest {

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
    String getDest();
    void setDest(String dest);
  }

  static class DestA implements DestInterface {
    String dest;

    public String getDest() {
      return dest;
    }

    public void setDest(String dest) {
      this.dest = dest;
    }
  }

  static class DestB implements DestInterface {
    String dest;

    public String getDest() {
      return dest;
    }

    public void setDest(String dest) {
      this.dest = dest;
    }
  }

  static class DestC extends DestB {
  }

  static class BasePropertyMap extends PropertyMap<SrcInterface, DestInterface> {
    @Override
    protected void configure() {
      map().setDest(source.getSrc());
    }
  }

  public void shouldMappingClassSuccess() {
    TypeMap<SrcInterface, DestInterface> baseTypeMap = modelMapper.addMappings(new BasePropertyMap());

    baseTypeMap
        .include(SrcA.class, DestA.class)
        .include(SrcB.class, DestB.class)
        .include(SrcC.class, DestC.class);

    assertEquals(modelMapper.map(new SrcA("foo"), DestA.class).dest, "foo");
    assertEquals(modelMapper.map(new SrcB("foo"), DestB.class).dest, "foo");
    assertEquals(modelMapper.map(new SrcC("foo"), DestC.class).dest, "foo");

    assertNull(modelMapper.map(new SrcA("foo"), DestB.class).dest);
    assertNull(modelMapper.map(new SrcA("foo"), DestC.class).dest);
    assertNull(modelMapper.map(new SrcC("foo"), DestB.class).dest);
  }

  public void shouldNotMappingUndefinedTypeMap() {
    TypeMap<SrcInterface, DestInterface> baseTypeMap = modelMapper.addMappings(new BasePropertyMap());

    baseTypeMap
        .include(SrcA.class, DestA.class)
        .include(SrcB.class, DestB.class)
        .include(SrcC.class, DestC.class);

    assertNull(modelMapper.map(new SrcA("foo"), DestB.class).dest);
    assertNull(modelMapper.map(new SrcA("foo"), DestC.class).dest);
    assertNull(modelMapper.map(new SrcC("foo"), DestB.class).dest);
  }

  public void shouldMappingInterfaceWithProviderSuccess() {
    TypeMap<SrcInterface, DestInterface> baseTypeMap = modelMapper.addMappings(new BasePropertyMap());

    baseTypeMap
        .include(SrcA.class, DestInterface.class)
        .include(SrcB.class, DestInterface.class)
        .include(SrcC.class, DestInterface.class);

    modelMapper.getTypeMap(SrcA.class, DestInterface.class).setProvider(new Provider<DestInterface>() {
      public DestInterface get(ProvisionRequest<DestInterface> request) {
        return new DestA();
      }
    });

    modelMapper.getTypeMap(SrcB.class, DestInterface.class).setProvider(new Provider<DestInterface>() {
      public DestInterface get(ProvisionRequest<DestInterface> request) {
        return new DestB();
      }
    });

    modelMapper.getTypeMap(SrcC.class, DestInterface.class).setProvider(new Provider<DestInterface>() {
      public DestInterface get(ProvisionRequest<DestInterface> request) {
        return new DestC();
      }
    });

    DestInterface destA = modelMapper.map(new SrcA("foo"), DestInterface.class);
    assertTrue(destA instanceof DestA);
    assertEquals(destA.getDest(), "foo");

    DestInterface destB = modelMapper.map(new SrcB("foo"), DestInterface.class);
    assertTrue(destB instanceof DestB);
    assertEquals(destB.getDest(), "foo");

    DestInterface destC = modelMapper.map(new SrcC("foo"), DestInterface.class);
    assertTrue(destC instanceof DestC);
    assertEquals(destC.getDest(), "foo");
  }

  public void shouldFailedDuplicateInclude() {
    TypeMap<SrcInterface, DestInterface> baseTypeMap = modelMapper.addMappings(new BasePropertyMap());

    baseTypeMap.include(SrcA.class, DestA.class);
    try {
      baseTypeMap.include(SrcA.class, DestA.class);
    } catch (IllegalArgumentException e) {
      Asserts.assertContains(e.getMessage(), "TypeMap exists in the store");
    }
  }

  public void shouldMapDependsOnDestType() {
    TypeMap<SrcInterface, DestInterface> baseTypeMap = modelMapper.addMappings(new BasePropertyMap());

    baseTypeMap
        .include(SrcA.class, DestA.class)
        .include(SrcA.class, DestC.class)
        .include(SrcA.class, DestInterface.class);

    modelMapper.getTypeMap(SrcA.class, DestInterface.class).setProvider(new AbstractProvider<DestInterface>() {
      @Override
      protected DestInterface get() {
        return new DestA();
      }
    });

    assertEquals(modelMapper.map(new SrcA("foo"), DestA.class).dest, "foo");
    assertEquals(modelMapper.map(new SrcA("foo"), DestC.class).dest, "foo");
    assertTrue(modelMapper.map(new SrcA("foo"), DestInterface.class) instanceof DestA);

    assertNull(modelMapper.map(new SrcA("foo"), DestB.class).dest);
  }
}