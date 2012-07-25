package org.modelmapper.functional.typeresolution;

import static org.testng.Assert.assertEquals;

import java.io.Serializable;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class GenericMapping extends AbstractTest {
  static class Entity<T extends Serializable> {
    protected T id;
  }

  static class EntityInt extends Entity<Integer> {
  }

  static class EntityLong extends Entity<Long> {
  }

  static class EntityString extends Entity<String> {
  }

  public void shouldMapFromGenericType() {
    Entity<Long> source1 = new Entity<Long>();
    source1.id = Long.valueOf(5000);

    EntityString dest1 = modelMapper.map(source1, EntityString.class);
    assertEquals(dest1.id, "5000");
  }

  public void shouldMapFromGenericTypeToGenericType() {
    Entity<Long> source1 = new Entity<Long>();
    source1.id = Long.valueOf(5000);

    EntityInt dest1 = modelMapper.map(source1, EntityInt.class);
    assertEquals(dest1.id.intValue(), 5000);
  }

  public void shouldMapToGenericType() {
    Entity<String> source2 = new Entity<String>();
    source2.id = "5000";

    EntityLong dest2 = modelMapper.map(source2, EntityLong.class);
    assertEquals(dest2.id.longValue(), 5000L);
  }

  public void shouldMapToSameType() {
    EntityLong source = new EntityLong();
    source.id = Long.valueOf(5000);

    EntityLong dest = modelMapper.map(source, EntityLong.class);
    assertEquals(dest.id.longValue(), 5000);
  }
}
