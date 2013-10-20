package org.modelmapper.functional;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.modelmapper.AbstractTest;
import org.modelmapper.Conditions;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 * @see {@link org.modelmapper.bugs.GH6} for additional null behavior tests
 */
@Test(groups = "functional")
public class NullBehavior extends AbstractTest {
  static class S1 {
    S2 sub;
    String nullString;
    Long longValue;

    public S2 getSub() {
      return sub;
    }
  }

  static class S2 {
    int something;

    int[] getItems() {
      return new int[] { 1, 2, 3 };
    }
  }

  static class D1 {
    D2 sub;
    int subSomething = 5;
    String nullString;
    Long longValue = Long.valueOf(4);

    public void setSub(D2 sub) {
      this.sub = sub;
    }
  }

  static class D2 {
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
    assertNull(result.sub);
    assertEquals(result.subSomething, 0);
    assertNull(result.nullString);
    assertNull(result.longValue);
  }

  public void shouldSkipNullsPerTypeMap() {
    modelMapper.createTypeMap(S1.class, D1.class).setPropertyCondition(Conditions.isNotNull());

    S1 source = new S1();
    D1 d = modelMapper.map(source, D1.class);
    assertEquals(d.longValue, Long.valueOf(4));
    assertEquals(d.subSomething, 5);
    assertNull(d.sub);
    assertNull(d.nullString);
  }

  public void shouldSkipNullsGlobally() {
    modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());

    S1 source = new S1();
    D1 d = modelMapper.map(source, D1.class);
    assertEquals(d.longValue, Long.valueOf(4));
    assertEquals(d.subSomething, 5);
    assertNull(d.sub);
    assertNull(d.nullString);
  }
}
