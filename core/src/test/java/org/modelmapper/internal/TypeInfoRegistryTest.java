package org.modelmapper.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class TypeInfoRegistryTest {
  public void shouldHashCorrectly() {
    InheritingConfiguration config1 = new InheritingConfiguration();
    InheritingConfiguration config2 = new InheritingConfiguration();
    TypeInfo<Integer> typeInfo1 = TypeInfoRegistry.typeInfoFor(Integer.class, config1);
    TypeInfo<Integer> typeInfo2 = TypeInfoRegistry.typeInfoFor(Integer.class, config2);
    assertEquals(typeInfo1, typeInfo2);

    config1.enableFieldMatching(true);
    typeInfo1 = TypeInfoRegistry.typeInfoFor(Integer.class, config1);
    assertTrue(typeInfo1 != typeInfo2);
  }
}
