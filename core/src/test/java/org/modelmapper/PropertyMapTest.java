package org.modelmapper;

import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class PropertyMapTest {
  static class BadMemberMap extends PropertyMap<Object, Object> {
    protected void configure() {
    }

    void test() {
      map();
    }
  }

  @SuppressWarnings("rawtypes")
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void loadShouldRequireTypeParameters() {
    new PropertyMap() {
      @Override
      protected void configure() {
      }
    };
  }

  public void shouldInstantiate() {
    new PropertyMap<Object, Object>() {
      @Override
      protected void configure() {
      }
    };
  }

  @Test(expectedExceptions = IllegalStateException.class)
  public void shouldThrowWhenMapOutsideOfDefine() {
    new BadMemberMap().test();
  }
}
