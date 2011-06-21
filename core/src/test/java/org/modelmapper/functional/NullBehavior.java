package org.modelmapper.functional;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.modelmapper.AbstractTest;
import org.modelmapper.TypeMap;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
@SuppressWarnings("unused")
public class NullBehavior extends AbstractTest {
  private static class S1 {
    S2 sub;
    String nullString;
  }

  private static class S2 {
    int something;

    int[] getItems() {
      return new int[] { 1, 2, 3 };
    }
  }

  private static class D1 {
    D2 sub;
    int subSomething;
    String nullString;
  }

  private static class D2 {
    int[] items;
  }

  /**
   * <pre>
   * sub/items -> sub/items
   * sub/something -> subSomething
   * nullString -> nullString
   * </pre>
   */
  public void shouldValidateTypeMap() {
    modelMapper.getTypeMap(S1.class, D1.class);
    modelMapper.validate();
  }

  public void shouldMapModelWithNullItems() {
    D1 result = modelMapper.map(new S1(), D1.class);
    assertNull(result.sub.items);
    assertEquals(result.subSomething, 0);
    assertNull(result.nullString);
  }
}
