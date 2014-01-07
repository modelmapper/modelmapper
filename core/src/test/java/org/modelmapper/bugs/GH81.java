package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.bugs.GH81.FooPrime.BarPrime;
import org.modelmapper.internal.util.TypeResolver;
import org.testng.annotations.Test;

/**
 * From https://github.com/jhalterman/modelmapper/issues/81
 */
@Test
public class GH81 {
  public static class Foo<T extends Number> {
    @SuppressWarnings("serial")
    public class Bar extends ArrayList<T> {
    }
  }

  public static class FooPrime extends Foo<Integer> {
    @SuppressWarnings("serial")
    public class BarPrime extends Bar {
    }
  }

  @Test
  public void shouldResolveArgumentsOfMemberType() {
    assertEquals(TypeResolver.resolveArguments(BarPrime.class, List.class)[0], Integer.class);
  }
}
