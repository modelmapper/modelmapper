package org.modelmapper.functional.circular;

import static org.testng.Assert.*;

import org.modelmapper.AbstractTest;
import org.modelmapper.ConfigurationException;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * Tests the handling of circular references.
 * 
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
@SuppressWarnings("unused")
public class CircularDependencies4 extends AbstractTest {
  private static class A {
    B value = new B();
  }

  private static class B {
    A value;
  }

  public void shouldNotThrowOnMatchingCircularReference() {
    B b2 = modelMapper.map(new A(), B.class);
    assertNull(b2.value);
  }
}
