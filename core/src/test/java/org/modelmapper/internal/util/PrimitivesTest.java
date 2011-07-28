package org.modelmapper.internal.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class PrimitivesTest {
  public void testDefaultValue() {
    assertEquals(Primitives.defaultValue(Byte.TYPE), (byte) 0);
    assertEquals(Primitives.defaultValue(Short.TYPE), (short) 0);
    assertEquals(Primitives.defaultValue(Integer.TYPE), 0);
    assertEquals(Primitives.defaultValue(Long.TYPE), 0L);
    assertEquals(Primitives.defaultValue(Float.TYPE), 0.0f);
    assertEquals(Primitives.defaultValue(Double.TYPE), 0.0d);
    assertEquals(Primitives.defaultValue(Character.TYPE), '\u0000');
    assertEquals(Primitives.defaultValue(Boolean.TYPE), false);
    
    assertNull(Primitives.defaultValue(List.class));
  }
  
  public void testIsPrimitive() {
    assertTrue(Primitives.isPrimitive(Boolean.TYPE));
    assertTrue(Primitives.isPrimitive(Boolean.class));
    assertTrue(Primitives.isPrimitive(boolean.class));
    
    assertFalse(Primitives.isPrimitive(List.class));
  }
}
