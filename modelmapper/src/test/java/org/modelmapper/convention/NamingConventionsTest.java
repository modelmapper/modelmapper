package org.modelmapper.convention;

import static org.testng.Assert.*;

import org.modelmapper.spi.PropertyType;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class NamingConventionsTest {
  public void testJavaBeansAccessor() {
    assertTrue(NamingConventions.JAVABEANS_ACCESSOR.applies("getAge", PropertyType.METHOD));
    assertTrue(NamingConventions.JAVABEANS_ACCESSOR.applies("isOpen", PropertyType.METHOD));
    assertTrue(NamingConventions.JAVABEANS_ACCESSOR.applies("isopen", PropertyType.METHOD));

    assertFalse(NamingConventions.JAVABEANS_ACCESSOR.applies("IsOpen", PropertyType.METHOD));
    assertFalse(NamingConventions.JAVABEANS_ACCESSOR.applies("age", PropertyType.METHOD));
    assertFalse(NamingConventions.JAVABEANS_ACCESSOR.applies("GetAge", PropertyType.METHOD));
  }

  public void testJavaBeansMutator() {
    assertTrue(NamingConventions.JAVABEANS_MUTATOR.applies("setAge", PropertyType.METHOD));

    assertFalse(NamingConventions.JAVABEANS_MUTATOR.applies("SetAge", PropertyType.METHOD));
    assertFalse(NamingConventions.JAVABEANS_MUTATOR.applies("age", PropertyType.METHOD));
  }
}
