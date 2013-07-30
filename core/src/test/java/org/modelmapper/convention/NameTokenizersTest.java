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

  public void testUnderscoreTokenizer() {
    assertEquals(NameTokenizers.UNDERSCORE.tokenize("abc_Def_G", null), new String[] { "abc",
        "Def", "G" });
    assertEquals(NameTokenizers.UNDERSCORE.tokenize("Abc_Def", null), new String[] { "Abc", "Def" });
    assertEquals(NameTokenizers.UNDERSCORE.tokenize("A_Bc_D_Ef_Gh", null), new String[] { "A",
        "Bc", "D", "Ef", "Gh" });
    assertEquals(NameTokenizers.UNDERSCORE.tokenize("a_BCD", null), new String[] { "a", "BCD" });
    assertEquals(NameTokenizers.UNDERSCORE.tokenize("Aa_Bb_Cc_Dd", null), new String[] { "Aa",
        "Bb", "Cc", "Dd" });
    assertEquals(NameTokenizers.UNDERSCORE.tokenize("", null), new String[] { "" });
  }
}
