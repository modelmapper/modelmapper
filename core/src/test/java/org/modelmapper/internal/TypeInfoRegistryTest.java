package org.modelmapper.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import org.modelmapper.config.Configuration.AccessLevel;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class TypeInfoRegistryTest {
  public void shouldHashCorrectly1() {
    InheritingConfiguration config1 = new InheritingConfiguration();
    InheritingConfiguration config2 = new InheritingConfiguration();
    TypeInfo<Integer> typeInfo1 = TypeInfoRegistry.typeInfoFor(Integer.class, config1);
    TypeInfo<Integer> typeInfo2 = TypeInfoRegistry.typeInfoFor(Integer.class, config2);
    assertEquals(typeInfo1, typeInfo2);

    config1.setFieldMatchingEnabled(true);
    typeInfo1 = TypeInfoRegistry.typeInfoFor(Integer.class, config1);
    assertTrue(typeInfo1 != typeInfo2);
  }

  public void shouldHashCorrectly2() {
    Class<?> type = Byte.class;
    InheritingConfiguration conf1 = new InheritingConfiguration() {
      @Override
      public int hashCode() {
        return 0;
      }
    };
    InheritingConfiguration conf2 = new InheritingConfiguration() {
      @Override
      public int hashCode() {
        return 0;
      }
    };

    conf1.setMethodAccessLevel(AccessLevel.PRIVATE);
    conf2.setMethodAccessLevel(AccessLevel.PUBLIC);
    assertNotEquals(conf1, conf2);
    assertNotEquals(TypeInfoRegistry.typeInfoFor(type, conf1),
        TypeInfoRegistry.typeInfoFor(type, conf2));
  }
}
