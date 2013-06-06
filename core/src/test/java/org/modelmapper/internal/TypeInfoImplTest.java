package org.modelmapper.internal;

import static org.testng.Assert.assertEquals;

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

  @BeforeClass
  public void setupClass() {
    InheritingConfiguration config = new InheritingConfiguration();
    config.setFieldMatchingEnabled(true);
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

  public void shouldProduceMutators() {
    List<Accessor> accessors = new ArrayList<Accessor>(personInfo.getAccessors().values());
    assertEquals(accessors.size(), 3);
    assertEquals(accessors.get(0).getMember().getName(), "firstName");
    assertEquals(accessors.get(1).getMember().getName(), "age");
    assertEquals(accessors.get(2).getMember().getName(), "getLastName");
  }
}
