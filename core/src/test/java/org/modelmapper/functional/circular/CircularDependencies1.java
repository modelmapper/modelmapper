package org.modelmapper.functional.circular;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * Tests the handling of circular references.
 * 
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class CircularDependencies1 extends AbstractTest {
  static class A {
    A a;
    B b;
    String value;
  }

  static class B {
    C c;
    String value;
  }

  static class C {
    A a;
    String value;
  }

  static class DA {
    DA a;
    DB b;
    String value;
  }

  static class DB {
    DC c;
    String value;
  }

  static class DC {
    DA a;
    String value;
  }

  public void shouldMap1LevelCircularReference() {
    A source = new A();
    source.value = "test";
    source.a = source; // Create circular reference

    DA dest = modelMapper.map(source, DA.class);
    assertEquals(dest.value, "test");
    assertEquals(dest.a, dest); // Assert circular reference
  }

  public void shouldMap2LevelCircularReference() {
    A source = new A();
    source.a = new A();
    source.value = "test1";
    source.a.value = "test2";
    source.a.a = source; // Create circular reference

    DA dest = modelMapper.map(source, DA.class);
    assertEquals(dest.value, "test1");
    assertEquals(dest.a.value, "test2");
    assertEquals(dest.a.a, dest); // Assert circular reference
  }

  public void shouldMapLengthyCircularReferenceWithHomoTypes() {
    A source = new A();
    source.value = "test";
    A parent = source;
    for (int i = 0; i < 5; i++) {
      parent.a = new A();
      parent.a.value = "test" + i;
      parent = parent.a;
    }

    parent.a = source; // Create circular reference

    DA dest = modelMapper.map(source, DA.class);
    assertEquals(dest.value, "test");

    DA destParent = dest;
    for (int i = 0; i < 5; i++) {
      DA child = destParent.a;
      assertEquals(child.value, "test" + i);
      destParent = child;
    }

    assertEquals(destParent.a, dest); // Assert circular reference
  }

  public void shouldMapLengthyCircularReferenceWithHeteroTypes() {
    A source = new A();
    source.value = "test";
    A parent = source;
    int maxDepth = 5;
    for (int i = 0; i < maxDepth; i++) {
      parent.b = new B();
      parent.b.value = "b" + i;
      parent.b.c = new C();
      parent.b.c.value = "c" + i;

      if (i == maxDepth - 1)
        parent.b.c.a = source; // Create circular reference
      else {
        parent.b.c.a = new A();
        parent.b.c.a.value = "a" + i;
        parent = parent.b.c.a;
      }
    }

    DA dest = modelMapper.map(source, DA.class);
    assertEquals(dest.value, "test");

    DA destParent = dest;
    for (int i = 0; i < maxDepth; i++) {
      assertNull(destParent.a);
      assertEquals(destParent.b.value, "b" + i);
      assertEquals(destParent.b.c.value, "c" + i);
      if (i < maxDepth - 1)
        assertEquals(destParent.b.c.a.value, "a" + i);
      destParent = destParent.b.c.a;
    }

    assertEquals(destParent, dest); // Assert circular reference
  }

  /**
   * Assert non-circular hierarchy of homogeneous references.
   */
  public void shouldMapHierarchyOfHomoReferences() {
    A source = new A();
    source.value = "test";
    A parent = source;
    for (int i = 0; i < 5; i++) {
      A child = new A();
      parent.a = child;
      child.value = "test" + i;
      parent = child;
    }

    DA dest = modelMapper.map(source, DA.class);
    assertEquals(dest.value, "test");

    DA destParent = dest;
    for (int i = 0; i < 5; i++) {
      DA child = destParent.a;
      assertEquals(child.value, "test" + i);
      destParent = child;
    }

    assertNull(destParent.a);
  }
}
