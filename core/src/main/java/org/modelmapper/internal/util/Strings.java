package org.modelmapper.internal.util;

import java.util.List;

import org.modelmapper.spi.PropertyInfo;

/**
 * @author Jonathan Halterman
 */
public final class Strings {
  private Strings() {
  }

  public static String join(List<? extends PropertyInfo> properties) {
    StringBuilder sb = new StringBuilder();
    String delim = "";
    for (PropertyInfo info : properties) {
      sb.append(delim).append(info.getName());
      delim = ".";
    }

    return sb.toString();
  }
}
