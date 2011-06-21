package org.modelmapper.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.config.Configuration.AccessLevel;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class TypeInfoImplTest {
  TypeInfo<Person> personInfo;

  static class Person {
    String firstName;
    int age;

    void setFirstName(String name) {
    }

    String getLastName() {
      return null;
    }
  }

  static class Members {
    public int a;
    protected int b;
    int c;
    @SuppressWarnings("unused")
    private int d;
  }

  @BeforeClass
  public void setupClass() {
    InheritingConfiguration config = new InheritingConfiguration();
    config.enableFieldMatching(true);
    config.setFieldAccessLevel(AccessLevel.PACKAGE_PRIVATE);
    config.setMethodAccessLevel(AccessLevel.PACKAGE_PRIVATE);
    personInfo = TypeInfoRegistry.typeInfoFor(Person.class, config);
  }

  /**
   * Ensures that accessors and mutators are linear.
   */
  public void shouldProduceAccessors() {
    List<Mutator> mutators = new ArrayList<Mutator>(personInfo.getMutators().values());
    assertEquals(mutators.size(), 2);
    assertEquals(mutators.get(0).getMember().getName(), "setFirstName");
    assertEquals(mutators.get(1).getMember().getName(), "age");
  }

  public void testCanAccessMember() throws Exception {
    Member a = Members.class.getDeclaredField("a");
    assertTrue(TypeInfoImpl.canAccessMember(a, AccessLevel.PUBLIC));
    assertTrue(TypeInfoImpl.canAccessMember(a, AccessLevel.PROTECTED));
    assertTrue(TypeInfoImpl.canAccessMember(a, AccessLevel.PACKAGE_PRIVATE));
    assertTrue(TypeInfoImpl.canAccessMember(a, AccessLevel.PRIVATE));

    Member b = Members.class.getDeclaredField("b");
    assertFalse(TypeInfoImpl.canAccessMember(b, AccessLevel.PUBLIC));
    assertTrue(TypeInfoImpl.canAccessMember(b, AccessLevel.PROTECTED));
    assertTrue(TypeInfoImpl.canAccessMember(b, AccessLevel.PACKAGE_PRIVATE));
    assertTrue(TypeInfoImpl.canAccessMember(b, AccessLevel.PRIVATE));

    Member c = Members.class.getDeclaredField("c");
    assertFalse(TypeInfoImpl.canAccessMember(c, AccessLevel.PUBLIC));
    assertFalse(TypeInfoImpl.canAccessMember(c, AccessLevel.PROTECTED));
    assertTrue(TypeInfoImpl.canAccessMember(c, AccessLevel.PACKAGE_PRIVATE));
    assertTrue(TypeInfoImpl.canAccessMember(c, AccessLevel.PRIVATE));

    Member d = Members.class.getDeclaredField("d");
    assertFalse(TypeInfoImpl.canAccessMember(d, AccessLevel.PUBLIC));
    assertFalse(TypeInfoImpl.canAccessMember(d, AccessLevel.PROTECTED));
    assertFalse(TypeInfoImpl.canAccessMember(d, AccessLevel.PACKAGE_PRIVATE));
    assertTrue(TypeInfoImpl.canAccessMember(d, AccessLevel.PRIVATE));
  }

  public void shouldProduceMutators() {
    List<Accessor> accessors = new ArrayList<Accessor>(personInfo.getAccessors().values());
    assertEquals(accessors.size(), 3);
    assertEquals(accessors.get(0).getMember().getName(), "firstName");
    assertEquals(accessors.get(1).getMember().getName(), "age");
    assertEquals(accessors.get(2).getMember().getName(), "getLastName");
  }
}
