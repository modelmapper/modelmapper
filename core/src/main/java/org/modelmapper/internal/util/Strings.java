/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.modelmapper.internal.util;

import java.util.List;

import org.modelmapper.spi.PropertyInfo;

/**
 * @author Jonathan Halterman
 */
public final class Strings {
  private Strings() {
  }

  /**
   * Returns the joined {@code properties} with a <code>.</code> delimiter, including a trailing
   * delimiter.
   */
  public static String join(List<? extends PropertyInfo> properties) {
    StringBuilder sb = new StringBuilder();
    for (PropertyInfo info : properties)
      sb.append(info.getName()).append('.');
    return sb.toString();
  }

  /**
   * Returns a String containing the members of the {@code properties} joined on a <code>/</code>
   * delimiter.
   */
  public static String joinMembers(List<? extends PropertyInfo> properties) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < properties.size(); i++) {
      PropertyInfo info = properties.get(i);
      if (i > 0)
        builder.append("/");
      builder.append(Types.toString(info.getMember()));
    }

    return builder.toString();
  }

  public static String joinWithFirstType(List<? extends PropertyInfo> properties) {
    StringBuilder sb = new StringBuilder();
    String delim = "";
    for (PropertyInfo info : properties) {
      sb.append(delim).append(delim.equals("") ? info : info.getName());
      delim = ".";
    }

    return sb.toString();
  }

  /**
   * Utility method to take a string and convert it to normal Java variable name capitalization.
   * This normally means converting the first character from upper case to lower case, but in the
   * (unusual) special case when there is more than one character and both the first and second
   * characters are upper case, we leave it alone.
   * <p>
   * Thus "FooBah" becomes "fooBah" and "X" becomes "x", but "URL" stays as "URL".
   * 
   * @param name The string to be decapitalized.
   * @return The decapitalized version of the string.
   */
  public static String decapitalize(String name) {
    if (name == null || name.length() == 0)
      return name;
    if (name.length() > 1 && Character.isUpperCase(name.charAt(1))
        && Character.isUpperCase(name.charAt(0)))
      return name;
    char chars[] = name.toCharArray();
    chars[0] = Character.toLowerCase(chars[0]);
    return String.valueOf(chars);
  }
}
