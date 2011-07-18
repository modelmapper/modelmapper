package org.modelmapper.functional.deepmapping;

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
public class NestedMappingTest7 extends AbstractTest {
  private static class S1 {
    S2 sub = new S2();
  }

  private static class S2 {
    int something = 2;
    int somethingelse = 5;
    S3 subsub = new S3();

    int[] getItems() {
      return new int[] { 1, 2, 3 };
    }
  }

  private static class S3 {
    String one = "1";
    String two = "2";
  }

  private static class D1 {
    D2 sub;
    int subSomething;
  }

  private static class D2 {
    int[] items;
    int somethingelse;
    D3 subsub;
  }

  private static class D3 {
    String one;
    String two;
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
    assertEquals(result.sub.items, new int[] { 1, 2, 3 });
    assertEquals(result.sub.somethingelse, 5);
    assertEquals(result.sub.subsub.one, "1");
    assertEquals(result.sub.subsub.two, "2");
    assertEquals(result.subSomething, 2);
  }
}
