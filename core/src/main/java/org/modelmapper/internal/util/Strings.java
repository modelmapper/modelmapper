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
   * Compares the characters from the first {@code comparisonSize} elements from {@code a}, against
   * all of the characters in {@code b} to see if they are equal when ignoring case.
   */
  public static boolean contentEqualsIgnoreCase(int comparisonSize, CharSequence[] a, CharSequence b) {
    int n = 0;
    for (int i = 0; i < comparisonSize; i++)
      n += a[i].length();

    if (n == b.length()) {
      for (int i = 0, aIndex = 0, aCharIndex = 0; i < n; i++) {
        char c1 = a[aIndex].charAt(aCharIndex);
        char c2 = b.charAt(i);
        if (Character.toUpperCase(c1) != Character.toUpperCase(c2)
            || Character.toLowerCase(c1) != Character.toLowerCase(c2))
          return false;

        if (aCharIndex == a[aIndex].length() - 1) {
          aIndex++;
          aCharIndex = 0;
        } else
          aCharIndex++;
      }

      return true;
    }

    return false;
  }
}
