package org.modelmapper.internal.util;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class StringsTest {
  public void testContentEqualsIgnoreCase() {
    String[] a = new String[] { "te", "st" };
    assertTrue(Strings.contentEqualsIgnoreCase(a.length, a, "test"));
    assertFalse(Strings.contentEqualsIgnoreCase(a.length, a, "tset"));

    String[] b = new String[] { "cuStomer", "OrdeR", "billIngaddre", "ss" };
    assertTrue(Strings.contentEqualsIgnoreCase(b.length, b, "customerOrderBillingAddress"));
    assertTrue(Strings.contentEqualsIgnoreCase(b.length, b, "customerorderbillingaddress"));
    assertFalse(Strings.contentEqualsIgnoreCase(b.length, b, "customerrderbillingaddress"));
    assertFalse(Strings.contentEqualsIgnoreCase(b.length, b, "customerorderbillingaddresss"));
  }
}
