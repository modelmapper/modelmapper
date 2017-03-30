package org.modelmapper.functional.deepmapping;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import org.modelmapper.AbstractTest;
import org.modelmapper.Asserts;
import org.modelmapper.ConfigurationException;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class DeepMappingTest1 extends AbstractTest {
  static class A1 {
    B1 b;

    public B1 getB() {
      return b;
    }
  }

  static class B1 {
    C1 c;

    public C1 getC() {
      return c;
    }
  }

  static class C1 {
    D1 d;

    public D1 getD() {
      return d;
    }
  }

  static class D1 {
    String value;

    public String getSource() {
      return value;
    }
  }

  static class A2 {
    B2 bb;

    public B2 getBb() {
      return bb;
    }
  }

  static class B2 {
    C2 cc;

    public C2 getCc() {
      return cc;
    }
  }

  static class C2 {
    D2 dd;

    public D2 getDd() {
      return dd;
    }
  }

  static class D2 {
    String dest;

    public void setDest(String dest) {
      this.dest = dest;
    }
  }

  public void shouldThrowOnMissingMutatorForAccessor() {
    modelMapper.getConfiguration().setFieldMatchingEnabled(false);
    try {
      modelMapper.addMappings(new PropertyMap<A1, A2>() {
        protected void configure() {
          map().getBb().getCc().getDd().setDest(source.getB().getC().getD().getSource());
        }
      });
    } catch (ConfigurationException e) {
      Asserts.assertContains(e.getMessage(), "1) No corresponding mutator was found");
      assertEquals(e.getErrorMessages().size(), 3);
      return;
    }

    fail();
  }

  public void testMapping() {
    modelMapper.addMappings(new PropertyMap<A1, A2>() {
      protected void configure() {
        map().getBb().getCc().getDd().setDest(source.getB().getC().getD().getSource());
      }
    });

    A1 a1 = new A1();
    a1.b = new B1();
    a1.b.c = new C1();
    a1.b.c.d = new D1();
    a1.b.c.d.value = "src";

    A2 a2 = modelMapper.map(a1, A2.class);

    modelMapper.validate();
    assertEquals(a2.bb.cc.dd.dest, "src");
  }
}
