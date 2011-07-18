/**
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.modelmapper;

import static org.testng.Assert.assertTrue;

import java.util.Collection;
import java.util.Set;

public final class Asserts {
  private Asserts() {
  }

  public static void assertContains(String text, String... substrings) {
    int startingFrom = 0;
    for (String substring : substrings) {
      int index = text.indexOf(substring, startingFrom);
      assertTrue(index >= startingFrom,
          String.format("Expected \"%s\" to contain substring \"%s\"", text, substring));
      startingFrom = index + substring.length();
    }

    String lastSubstring = substrings[substrings.length - 1];
    assertTrue(text.indexOf(lastSubstring, startingFrom) == -1, String.format(
        "Expected \"%s\" to contain substring \"%s\" only once),", text, lastSubstring));
  }

  /**
   * Compares a collection to a set, disregarding sort order.
   */
  public static void assertEquals(Collection<?> a, Set<?> b) {
    for (Object object : a)
      if (!b.contains(object))
        throw new AssertionError();
  }
}
