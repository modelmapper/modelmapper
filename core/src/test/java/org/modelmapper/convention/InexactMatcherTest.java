package org.modelmapper.convention;

import static org.modelmapper.convention.InexactMatcher.matchTokens;
import static org.testng.Assert.assertEquals;

import org.modelmapper.spi.Tokens;
import org.testng.annotations.Test;

@Test
public class InexactMatcherTest {
  public void shouldMatchTokens() {
    // Positive
    assertEquals(
        matchTokens(Tokens.of("customer", "fiRst", "naMe"),
            Tokens.of("cusTomerfirstname"), 0), 1);
    assertEquals(
        matchTokens(Tokens.of("customErFirstnAme"),
            Tokens.of("customer", "fiRst", "naMe"),0), 3);
    assertEquals(
        matchTokens(Tokens.of("cu", "stomerf", "irstna", "me"),
            Tokens.of("customer", "fiRst", "naMe"), 0), 3);
    assertEquals(
        matchTokens(Tokens.of("customer", "fiRst", "naMe"),
            Tokens.of("cu", "stomerf", "irstna", "me"), 0), 4);
    assertEquals(matchTokens(Tokens.of("oo", "aa"), Tokens.of("aa", "a"), 0), 1);
    assertEquals(matchTokens(Tokens.of("aabb", "cc"), Tokens.of("aa", "bbcc"), 0), 2);
    assertEquals(
        matchTokens(Tokens.of("z", "ab", "cd", "e"), Tokens.of("abc", "de"), 0), 2);
    assertEquals(
        matchTokens(Tokens.of("oo", "aa", "bb", "cc"), Tokens.of("aa", "bb"), 0), 1);

    // Later starting index
    assertEquals(
        matchTokens(Tokens.of("customer", "first", "name"), Tokens.of("asdf",
            "firstName"), 1), 1);
    assertEquals(
        matchTokens(Tokens.of("customer", "firstNaMe"), Tokens.of("asdf", "first",
            "name"), 1), 2);

    // Negative
    assertEquals(
        matchTokens(Tokens.of("customer", "fiRst", "naMe"),
            Tokens.of("cusTomFirstname"), 0), 0);
    assertEquals(matchTokens(Tokens.of("oo", "a", "aaa"), Tokens.of("aa", "a"), 0), 2);
    assertEquals(matchTokens(Tokens.of("aabbc", "cc"), Tokens.of("aa", "bb", "cc"), 0),
        0);
    assertEquals(
        matchTokens(Tokens.of("z", "ab", "ccd", "e"), Tokens.of("abc", "de"), 0), 0);

    // Later starting index
    assertEquals(
        matchTokens(Tokens.of("name", "customer", "first"), Tokens.of("asdf",
            "firstName"), 1), 0);
    assertEquals(
        matchTokens(Tokens.of("my", "firstNaMe"), Tokens.of("my", "asdf", "first"), 2),
        0);
    assertEquals(matchTokens(Tokens.of("a", "a", "a"), Tokens.of("b"), 0), 0);

    assertEquals(matchTokens(Tokens.of("goo", "go"), Tokens.of("goo", "go"), 0), 1);
    assertEquals(matchTokens(Tokens.of("goo", "go"), Tokens.of("goo", "go"), 1), 1);

    assertEquals(matchTokens(Tokens.of("go", "goo"), Tokens.of("go", "goo"), 0), 1);
    assertEquals(matchTokens(Tokens.of("go", "goo"), Tokens.of("go", "goo"), 1), 1);
  }
}
