package org.modelmapper.bugs;

import org.modelmapper.AbstractTest;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class GH126 extends AbstractTest {
  public static interface HasField<T> {
    T getField();
  }

  public static class Entity implements HasField<Long> {

    private Long field;

    public Long getField() {
      return field;
    }

    public void setField(Long field) {
      this.field = field;
    }
  }

  public static class Dto implements HasField<Long> {
    private Long field;

    public Long getField() {
      return field;
    }

    public void setField(Long field) {
      this.field = field;
    }
  }

  public static class EntityToDtoMap extends PropertyMap<Entity, Dto> {
    @Override
    protected void configure() {
      map().setField(source.getField());
    }
  }

  @Test
  public void mapWithGenericField() {
    // given
    ModelMapper mapper = new ModelMapper();
    mapper.addMappings(new EntityToDtoMap());

    Entity entity = new Entity();

    // when
    Dto dto = mapper.map(entity, Dto.class);

    // then
    Assert.assertNotNull(dto);
    Assert.assertEquals(entity.getField(), dto.getField());
  }
}
