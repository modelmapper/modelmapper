package org.modelmapper.convention;

import static org.modelmapper.convention.InexactMatcher.matchTokens;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

@Test
public class InexactMatcherTest {
  public void shouldMatchTokens() {
    // Positive
    assertEquals(
        matchTokens(new String[] { "customer", "fiRst", "naMe" },
            new String[] { "cusTomerfirstname" }, 0), 1);
    assertEquals(
        matchTokens(new String[] { "customErFirstnAme" }, new String[] { "customer", "fiRst",
            "naMe" }, 0), 3);
    assertEquals(
        matchTokens(new String[] { "cu", "stomerf", "irstna", "me" }, new String[] { "customer",
            "fiRst", "naMe" }, 0), 3);
    assertEquals(
        matchTokens(new String[] { "customer", "fiRst", "naMe" }, new String[] { "cu", "stomerf",
            "irstna", "me" }, 0), 4);
    assertEquals(matchTokens(new String[] { "oo", "aa" }, new String[] { "aa", "a" }, 0), 1);
    assertEquals(matchTokens(new String[] { "aabb", "cc" }, new String[] { "aa", "bbcc" }, 0), 2);
    assertEquals(
        matchTokens(new String[] { "z", "ab", "cd", "e" }, new String[] { "abc", "de" }, 0), 2);
    assertEquals(
        matchTokens(new String[] { "oo", "aa", "bb", "cc" }, new String[] { "aa", "bb" }, 0), 1);

    // Later starting index
    assertEquals(
        matchTokens(new String[] { "customer", "first", "name" }, new String[] { "asdf",
            "firstName" }, 1), 1);
    assertEquals(
        matchTokens(new String[] { "customer", "firstNaMe" }, new String[] { "asdf", "first",
            "name" }, 1), 2);

    // Negative
    assertEquals(
        matchTokens(new String[] { "customer", "fiRst", "naMe" },
            new String[] { "cusTomFirstname" }, 0), 0);
    assertEquals(matchTokens(new String[] { "oo", "a", "aaa" }, new String[] { "aa", "a" }, 0), 2);
    assertEquals(matchTokens(new String[] { "aabbc", "cc" }, new String[] { "aa", "bb", "cc" }, 0),
        0);
    assertEquals(
        matchTokens(new String[] { "z", "ab", "ccd", "e" }, new String[] { "abc", "de" }, 0), 0);

    // Later starting index
    assertEquals(
        matchTokens(new String[] { "name", "customer", "first" }, new String[] { "asdf",
            "firstName" }, 1), 0);
    assertEquals(
        matchTokens(new String[] { "my", "firstNaMe" }, new String[] { "my", "asdf", "first" }, 2),
        0);
    assertEquals(matchTokens(new String[] { "a", "a", "a" }, new String[] { "b" }, 0), 0);

    assertEquals(matchTokens(new String[] { "goo", "go" }, new String[] { "goo", "go" }, 0), 1);
    assertEquals(matchTokens(new String[] { "goo", "go" }, new String[] { "goo", "go" }, 1), 1);

    assertEquals(matchTokens(new String[] { "go", "goo" }, new String[] { "go", "goo" }, 0), 1);
    assertEquals(matchTokens(new String[] { "go", "goo" }, new String[] { "go", "goo" }, 1), 1);
  }
}
