package org.modelmapper.convention;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class NameTokenizersTest {
  public void testCamelCaseTokenizer() {
    assertEquals(NameTokenizers.CAMEL_CASE.tokenize("abcDefG", null), new String[] { "abc", "Def",
        "G" });
    assertEquals(NameTokenizers.CAMEL_CASE.tokenize("AbcDef", null), new String[] { "Abc", "Def" });
    assertEquals(NameTokenizers.CAMEL_CASE.tokenize("ABcDEfGh", null), new String[] { "A", "Bc",
        "D", "Ef", "Gh" });
    assertEquals(NameTokenizers.CAMEL_CASE.tokenize("aBCD", null), new String[] { "a", "BCD" });
    assertEquals(NameTokenizers.CAMEL_CASE.tokenize("AaBbCcDd", null), new String[] { "Aa", "Bb",
        "Cc", "Dd" });
    assertEquals(NameTokenizers.CAMEL_CASE.tokenize("", null), new String[] { "" });
  }
}
