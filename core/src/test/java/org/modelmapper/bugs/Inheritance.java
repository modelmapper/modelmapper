package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

@Test
public class Inheritance extends AbstractTest {
  static class SuperEntity {
    String name; // assume getters and setters
  }

  static class SubEntity extends SuperEntity {
    boolean flag; // assume getters and setters
  }

  static class OwnerEntity {
    SubEntity ref; // assume getters and setters
  }

  static class SuperDto {
    String name; // assume getters and setters
  }

  static class SubDto extends SuperDto {
    boolean flag; // assume getters and setters
  }

  static class OwnerDto {
    SubDto ref; // assume getters and setters
  }

  public void test() {
    SubEntity subEntity = new SubEntity();
    subEntity.name = "name";
    subEntity.flag = true;
    OwnerEntity entity = new OwnerEntity();
    entity.ref = subEntity;

    OwnerDto dto = modelMapper.map(entity, OwnerDto.class);
    assertNotNull(dto.ref);
    assertEquals("name", dto.ref.name);
    assertEquals(SubDto.class, dto.ref.getClass());
    assertEquals(true, ((SubDto) dto.ref).flag);
  }
}
