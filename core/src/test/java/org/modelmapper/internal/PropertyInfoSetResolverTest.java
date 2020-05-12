package org.modelmapper.internal;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Member;
import java.util.Map;

import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.NameTransformers;
import org.modelmapper.convention.NamingConventions;
import org.modelmapper.spi.PropertyType;
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

    public int getA() {
      return a;
    }

    public void setA(int a) {
      this.a = a;
    }

    public int getB() {
      return b;
    }

    public void setB(int b) {
      this.b = b;
    }

    public int getC() {
      return c;
    }

    public void setC(int c) {
      this.c = c;
    }

    public int getD() {
      return d;
    }

    public void setD(int d) {
      this.d = d;
    }
  }

  public void whenIsFieldMatchingEnabledThenPropertyTypeShouldBeEqualsField() {
    Members members = new Members();
    InheritingConfiguration configuration = spy(new InheritingConfiguration());
    when(configuration.isFieldMatchingEnabled()).thenReturn(true);
    when(configuration.getFieldAccessLevel()).thenReturn(AccessLevel.PRIVATE);

    when(configuration.getSourceNamingConvention()).thenReturn(NamingConventions.JAVABEANS_ACCESSOR);
    when(configuration.getSourceNameTransformer()).thenReturn(NameTransformers.JAVABEANS_ACCESSOR);

    Map<String, Accessor> accessors = PropertyInfoSetResolver
            .resolveAccessors(members, Members.class, configuration);

    assertEquals(accessors.get("a").getPropertyType(), PropertyType.FIELD);
    assertEquals(accessors.get("b").getPropertyType(), PropertyType.FIELD);
    assertEquals(accessors.get("c").getPropertyType(), PropertyType.FIELD);
    assertEquals(accessors.get("d").getPropertyType(), PropertyType.FIELD);
  }

  public void whenIsNotFieldMatchingEnabledThenPropertyTypeShouldBeEqualsMethod() {
    Members members = new Members();
    InheritingConfiguration configuration = spy(new InheritingConfiguration());
    when(configuration.isFieldMatchingEnabled()).thenReturn(false);
    when(configuration.getFieldAccessLevel()).thenReturn(AccessLevel.PRIVATE);

    when(configuration.getSourceNamingConvention()).thenReturn(NamingConventions.JAVABEANS_ACCESSOR);
    when(configuration.getSourceNameTransformer()).thenReturn(NameTransformers.JAVABEANS_ACCESSOR);

    Map<String, Accessor> accessors = PropertyInfoSetResolver
            .resolveAccessors(members, Members.class, configuration);

    assertEquals(accessors.get("a").getPropertyType(), PropertyType.METHOD);
    assertEquals(accessors.get("b").getPropertyType(), PropertyType.METHOD);
    assertEquals(accessors.get("c").getPropertyType(), PropertyType.METHOD);
    assertEquals(accessors.get("d").getPropertyType(), PropertyType.METHOD);
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
