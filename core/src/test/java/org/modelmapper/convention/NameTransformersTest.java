package org.modelmapper.convention;

import static org.testng.Assert.assertEquals;

import org.modelmapper.spi.NameableType;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class NameTransformersTest {
  public void testJavaBeansAccessor() {
    assertEquals(NameTransformers.JAVABEANS_ACCESSOR.transform("getAge", NameableType.METHOD),
        "age");
    assertEquals(NameTransformers.JAVABEANS_ACCESSOR.transform("age", NameableType.METHOD), "age");
    assertEquals(NameTransformers.JAVABEANS_ACCESSOR.transform("GetAge", NameableType.METHOD),
        "GetAge");
    assertEquals(NameTransformers.JAVABEANS_ACCESSOR.transform("isOpen", NameableType.METHOD),
        "open");
    assertEquals(NameTransformers.JAVABEANS_ACCESSOR.transform("isopen", NameableType.METHOD),
        "open");
  }

  public void testJavaBeansMutator() {
    assertEquals(NameTransformers.JAVABEANS_MUTATOR.transform("setAge", NameableType.METHOD), "age");
    assertEquals(NameTransformers.JAVABEANS_MUTATOR.transform("age", NameableType.METHOD), "age");
    assertEquals(NameTransformers.JAVABEANS_MUTATOR.transform("SetAge", NameableType.METHOD),
        "SetAge");
  }
}
