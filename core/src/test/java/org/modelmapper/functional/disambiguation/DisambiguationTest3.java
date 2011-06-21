package org.modelmapper.functional.disambiguation;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * Tests that disambiguation is performed as expected by the TypeMapFactory.
 * 
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class DisambiguationTest3 extends AbstractTest {
  private static class Entity {
    A Aa = new A();
  }

  private static class A {
    B Bb = new B();
  }

  private static class B {
    C Cc = new C();
    C CcDd = new C();
  }

  private static class C {
    String Ee;
  }

  private static class DTO {
    String AaBbCcEe = "v1";
    String AaBbCcDdEe = "v2";
  }

  /**
   * <pre>
   * A Aa/B Bb/C Cc/String Ee -> AaBbCcEe
   * A Aa/B Bb/C CcDd/String Ee -> AaBbCcDdEe
   * </pre>
   * 
   * Disambiguates.
   */
  public void shouldMapEntityToDTO() {
    Entity entity = new Entity();
    entity.Aa.Bb.Cc.Ee = "e1";
    entity.Aa.Bb.CcDd.Ee = "e2";
    DTO dto = modelMapper.map(entity, DTO.class);

    modelMapper.validate();
    assertEquals(dto.AaBbCcEe, "e1");
    assertEquals(dto.AaBbCcDdEe, "e2");
  }

  /**
   * <pre>
   * AaBbCcEe -> Aa/Bb/Cc/Ee
   * AaBbCcDdEe -> Aa/Bb/CcDd/Ee
   * </pre>
   */
  public void shouldMapDTOToEntity() {
    Entity entity = modelMapper.map(new DTO(), Entity.class);

    modelMapper.validate();
    assertEquals(entity.Aa.Bb.Cc.Ee, "v1");
    assertEquals(entity.Aa.Bb.CcDd.Ee, "v2");
  }
}
