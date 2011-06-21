package org.modelmapper.functional;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * Tests that nested mappings of equal depth hierarchies on the source and destination sides map as
 * expected.
 * 
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class EqualHierarchies extends AbstractTest {
  private static class SourceA {
    SourceB b = new SourceB();
  }

  private static class SourceB {
    String value = "source";
  }

  private static class DestA {
    DestB b = new DestB();
  }

  private static class DestB {
    String value = "dest";
  }

  /**
   * <pre>
   * value -> value
   * </pre>
   */
  public void shouldMapInnerClasses() {
    DestB dest = modelMapper.map(new SourceB(), DestB.class);
    modelMapper.validate();
    assertEquals(dest.value, "source");
  }

  /**
   * <pre>
   * value -> value
   * </pre>
   */
  public void shouldMapInverseInnerClasses() {
    SourceB source = modelMapper.map(new DestB(), SourceB.class);
    modelMapper.validate();
    assertEquals(source.value, "dest");
  }

  /**
   * <pre>
   * b/value -> b/value
   * </pre>
   */
  public void shouldMapOuterClasses() {
    DestA dest = modelMapper.map(new SourceA(), DestA.class);
    modelMapper.validate();
    assertEquals(dest.b.value, "source");
  }

  /**
   * <pre>
   * b/value -> b/value
   * </pre>
   */
  public void shouldMapInverseOuterClasses() {
    SourceA source = modelMapper.map(new DestA(), SourceA.class);
    modelMapper.validate();
    assertEquals(source.b.value, "dest");
  }
}
