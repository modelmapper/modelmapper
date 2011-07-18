package org.modelmapper.internal;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author Jonathan Halterman
 */
@Test
public class PropertyInfoRegistryTest extends AbstractTest {
  static class OtherEntity extends StringEntity {
  }

  static class StringEntity extends Entity<String> {
  }

  static class LongEntity extends Entity<Long> {
  }

  static class Entity<T extends Serializable> {
    public T getId() {
      return null;
    }
  }

  public void shouldResolveSeparatePropertyInfoForDifferentInitialTypes() throws Exception {
    Method getId = Entity.class.getMethod("getId");
    Accessor longGetId = PropertyInfoRegistry.accessorFor(LongEntity.class, getId,
        modelMapper.getConfiguration(), "getId");
    Accessor stringGetId = PropertyInfoRegistry.accessorFor(StringEntity.class, getId,
        modelMapper.getConfiguration(), "getId");
    Accessor otherGetId = PropertyInfoRegistry.accessorFor(OtherEntity.class, getId,
        modelMapper.getConfiguration(), "getId");

    assertTrue(longGetId != stringGetId);
    assertTrue(longGetId != otherGetId);
    assertTrue(stringGetId != otherGetId);
  }

  public void shouldResolveSamePropertyInfo() throws Exception {
    Method getId = Entity.class.getMethod("getId");
    Accessor longGetId1 = PropertyInfoRegistry.accessorFor(LongEntity.class, getId,
        modelMapper.getConfiguration(), "getId");
    Accessor longGetId2 = PropertyInfoRegistry.accessorFor(LongEntity.class, getId,
        modelMapper.getConfiguration(), "getId");

    assertTrue(longGetId1 == longGetId2);
  }
}
