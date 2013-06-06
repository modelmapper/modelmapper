package org.modelmapper.internal;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Member;

import org.modelmapper.config.Configuration.AccessLevel;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class PropertyInfoSetResolverTest {
  static class Members {
    public int a;
    protected int b;
    int c;
    @SuppressWarnings("unused") private int d;
  }

  public void testCanAccessMember() throws Exception {
    Member a = Members.class.getDeclaredField("a");
    assertTrue(PropertyInfoSetResolver.canAccessMember(a, AccessLevel.PUBLIC));
    assertTrue(PropertyInfoSetResolver.canAccessMember(a, AccessLevel.PROTECTED));
    assertTrue(PropertyInfoSetResolver.canAccessMember(a, AccessLevel.PACKAGE_PRIVATE));
    assertTrue(PropertyInfoSetResolver.canAccessMember(a, AccessLevel.PRIVATE));

    Member b = Members.class.getDeclaredField("b");
    assertFalse(PropertyInfoSetResolver.canAccessMember(b, AccessLevel.PUBLIC));
    assertTrue(PropertyInfoSetResolver.canAccessMember(b, AccessLevel.PROTECTED));
    assertTrue(PropertyInfoSetResolver.canAccessMember(b, AccessLevel.PACKAGE_PRIVATE));
    assertTrue(PropertyInfoSetResolver.canAccessMember(b, AccessLevel.PRIVATE));

    Member c = Members.class.getDeclaredField("c");
    assertFalse(PropertyInfoSetResolver.canAccessMember(c, AccessLevel.PUBLIC));
    assertFalse(PropertyInfoSetResolver.canAccessMember(c, AccessLevel.PROTECTED));
    assertTrue(PropertyInfoSetResolver.canAccessMember(c, AccessLevel.PACKAGE_PRIVATE));
    assertTrue(PropertyInfoSetResolver.canAccessMember(c, AccessLevel.PRIVATE));

    Member d = Members.class.getDeclaredField("d");
    assertFalse(PropertyInfoSetResolver.canAccessMember(d, AccessLevel.PUBLIC));
    assertFalse(PropertyInfoSetResolver.canAccessMember(d, AccessLevel.PROTECTED));
    assertFalse(PropertyInfoSetResolver.canAccessMember(d, AccessLevel.PACKAGE_PRIVATE));
    assertTrue(PropertyInfoSetResolver.canAccessMember(d, AccessLevel.PRIVATE));
  }
}
