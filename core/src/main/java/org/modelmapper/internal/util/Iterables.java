package org.modelmapper.internal.util;

import java.util.Collection;

/**
 * @author Jonathan Halterman
 */
public final class Iterables {
  private Iterables() {
  }

  public static boolean isIterable(Class<?> type) {
    return type.isArray() || Collection.class.isAssignableFrom(type);
  }
}
