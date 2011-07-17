package org.modelmapper.functional;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.convention.MatchingStrategies;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Tests that nested mappings of unequal depth hierarchies on the source and destination sides map
 * as expected. Requires the loose matching strategy.
 * 
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class UnequalHierarchies extends AbstractTest {
  private static class SourceA {
    SourceB b = new SourceB();
  }

  private static class SourceB {
    SourceC c = new SourceC();
  }

  private static class SourceC {
    String value = "source";
  }

  private static class Dest1 {
    String bCValue = "dest";
  }

  private static class Dest2A {
    Dest2B b = new Dest2B();
  }

  private static class Dest2B {
    String cValue = "dest";
  }

  private static class Dest3A {
    Dest3B bC = new Dest3B();
  }

  private static class Dest3B {
    String value = "dest";
  }

  @Override
  @BeforeTest
  protected void initContext() {
    super.initContext();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
  }

  /**
   * <pre>
   *  b/c/value -> bCValue
   *  b/c/value -> b/cValue
   *  b/c/value -> bC/value
   *  c/value -> cValue
   * </pre>
   */
  public void shouldMap() {
    Dest1 dest1 = modelMapper.map(new SourceA(), Dest1.class);
    assertEquals(dest1.bCValue, "source");

    Dest2A dest2a = modelMapper.map(new SourceA(), Dest2A.class);
    assertEquals(dest2a.b.cValue, "source");

    Dest3A dest3a = modelMapper.map(new SourceA(), Dest3A.class);
    assertEquals(dest3a.bC.value, "source");

    Dest2B dest2b = modelMapper.map(new SourceB(), Dest2B.class);
    assertEquals(dest2b.cValue, "source");

    modelMapper.validate();
  }

  /**
   * <pre>
   * bCValue -> b/c/value
   * b/cValue -> b/c/value
   * bC/value -> b/c/value
   * cValue -> c/value
   * </pre>
   */
  public void shouldMapInverse() {
    SourceA sourceA = modelMapper.map(new Dest1(), SourceA.class);
    assertEquals(sourceA.b.c.value, "dest");

    sourceA = modelMapper.map(new Dest2A(), SourceA.class);
    assertEquals(sourceA.b.c.value, "dest");

    sourceA = modelMapper.map(new Dest3A(), SourceA.class);
    assertEquals(sourceA.b.c.value, "dest");

    SourceB sourceB = modelMapper.map(new Dest2B(), SourceB.class);
    assertEquals(sourceB.c.value, "dest");

    modelMapper.validate();
  }
}
