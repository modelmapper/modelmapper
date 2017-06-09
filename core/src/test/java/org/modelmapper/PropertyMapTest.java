package org.modelmapper;

import net.jodah.typetools.TypeResolver;
import org.testng.Assert;
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
  @Test
  public void loadShouldRequireTypeParameters() {
    PropertyMap propertyMap = new PropertyMap() {
      @Override
      protected void configure() {
      }
    };
    Assert.assertTrue(propertyMap.sourceType == TypeResolver.Unknown.class);
    Assert.assertTrue(propertyMap.destinationType == TypeResolver.Unknown.class);
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
