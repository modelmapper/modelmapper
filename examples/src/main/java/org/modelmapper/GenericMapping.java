package org.modelmapper;

import static org.testng.Assert.assertEquals;

import java.io.Serializable;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class GenericMapping {
  public static class EntityLong extends Entity<Long> {
  }

  public static class EntityString extends Entity<String> {
  }

  public static class Entity<T extends Serializable> {
    protected T id;

    public T getId() {
      return id;
    }

    public void setId(T id) {
      this.id = id;
    }
  }

  public static class Source {
    protected Serializable id;

    public Serializable getId() {
      return id;
    }

    public void setId(Serializable id) {
      this.id = id;
    }
  }

  public static class Dest {
    protected Long id;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }
  }

//  public void shouldMapToSameType() {
//    EntityLong source = new EntityLong();
//    source.setId(Long.valueOf(5000));
//
//    EntityLong dest = modelMapper.map(source, EntityLong.class);
//    assertEquals(5000, dest.getId().longValue());
//  }

//  public void shouldMapToDifferentType() {
//    // Scenario 1
//    EntityLong source1 = new EntityLong();
//    source1.setId(Long.valueOf(5000));
//
//    EntityString dest1 = modelMapper.map(source1, EntityString.class);
//    assertEquals("5000", dest1.getId());
//
//    // Scenario 2
//    EntityString source2 = new EntityString();
//    source2.setId("5000");
//
//    EntityLong dest2 = modelMapper.map(source1, EntityLong.class);
//    assertEquals(5000, dest2.getId().longValue());
//  }

  /**
   * This scenario is mappable since the component type Serializable happens to be convertable to
   * the destination component type String.
   */
  public void shouldMapFromAssignableGenericType() {
    Entity<Long> source1 = new Entity<Long>();
    source1.setId(Long.valueOf(5000));

    Mapper mapper = new DozerBeanMapper();
    EntityString dest1 = mapper.map(source1, EntityString.class);
    assertEquals("5000", dest1.getId());
  }

  /**
   * This scenario is not mappable since the component type Serializable is not convertable to the
   * destination component type String.
   * 
   * TODO make this scenario work
   */
  public void shouldNotMapFromUnassignableGenericType() {
    Entity<String> source2 = new Entity<String>();
    source2.setId("abc");

    Mapper mapper = new DozerBeanMapper();
    EntityLong dest2 = mapper.map(source2, EntityLong.class);
    dest2 = mapper.map(source2, EntityLong.class);
    assertEquals(5000, dest2.getId().longValue());
  }
}
