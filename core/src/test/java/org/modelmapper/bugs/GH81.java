package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import net.jodah.typetools.TypeResolver;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.bugs.GH81.FooPrime.BarPrime;
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
    assertEquals(TypeResolver.resolveRawArguments(List.class, BarPrime.class)[0], Integer.class);
  }
}
