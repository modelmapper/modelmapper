package org.modelmapper.internal;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

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
    List<String> orderMutators = new ArrayList<String>();
    orderMutators.add(mutators.get(0).getMember().getName()); 
    orderMutators.add(mutators.get(1).getMember().getName());                           
    Collections.sort(orderMutators);                    
    assertEquals(mutators.size(), 2);
    assertEquals(orderMutators.get(0), "age");
    assertEquals(orderMutators.get(1), "setFirstName");
  }

  public void shouldProduceMutators() {
    List<Accessor> accessors = new ArrayList<Accessor>(personInfo.getAccessors().values());
    List<String> orderAccessors = new ArrayList<String>();
    for (int i = 0; i < accessors.size(); i++) {
      orderAccessors.add(accessors.get(i).getMember().getName()); 
    }                      
    Collections.sort(orderAccessors);
    assertEquals(accessors.size(), 3);
    assertEquals(orderAccessors.get(0), "age");
    assertEquals(orderAccessors.get(1), "firstName");
    assertEquals(orderAccessors.get(2), "getLastName");
  }
}
