package org.modelmapper.functional;

import static org.testng.Assert.assertEquals;

import java.io.Serializable;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class GenericMapping extends AbstractTest {
  static class EntityLong extends Entity<Long> {
  }

  static class EntityString extends Entity<String> {
  }

  static class Entity<T extends Serializable> {
    protected T id;
  }

  public void shouldMapToSameType() {
    EntityLong source = new EntityLong();
    source.id = Long.valueOf(5000);

    EntityLong dest = modelMapper.map(source, EntityLong.class);
    assertEquals(dest.id.longValue(), 5000);
  }

  public void shouldMapToDifferentType() {
    // Scenario 1
    EntityLong source1 = new EntityLong();
    source1.id = Long.valueOf(5000);

    EntityString dest1 = modelMapper.map(source1, EntityString.class);
    assertEquals(dest1.id, "5000");

    // Scenario 2
    EntityString source2 = new EntityString();
    source2.id = "5000";

    EntityLong dest2 = modelMapper.map(source2, EntityLong.class);
    assertEquals(dest2.id.longValue(), 5000);
  }

  public void shouldMapFromGenericType() {
    // Scenario 2
    Entity<Long> source1 = new Entity<Long>();
    source1.id = Long.valueOf(5000);

    EntityString dest1 = modelMapper.map(source1, EntityString.class);
    assertEquals(dest1.id, "5000");

    // Scenario 2
    Entity<String> source2 = new Entity<String>();
    source2.id = "5000";

    EntityLong dest2 = modelMapper.map(source2, EntityLong.class);
    assertEquals(dest2.id.longValue(), 5000L);
  }
}
