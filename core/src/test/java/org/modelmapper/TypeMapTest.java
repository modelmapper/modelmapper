package org.modelmapper;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.modelmapper.spi.ConstantMapping;
import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.PropertyInfo;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class TypeMapTest extends AbstractTest {
  public static class Person {
    String firstName;
    String lastName;
    String employer;

    String getEmployer() {
      return employer;
    }

    String getLastName() {
      return lastName;
    }
  }

  public static class PersonDTO {
    String firstName;
    String surName;
    String employerName;

    void setFirstName(String firstName) {
      this.firstName = firstName;
    }

    void setEmployerName(String employerName) {
      this.employerName = employerName;
    }

    void setSurName(String surName) {
      this.surName = surName;
    }
  }

  public void shouldGetUnmappedMembers() {
    TypeMap<Person, PersonDTO> personMap = modelMapper.createTypeMap(Person.class, PersonDTO.class);
    List<PropertyInfo> memberInfo = personMap.getUnmappedProperties();
    assertEquals(memberInfo.size(), 2);

    Collections.sort(memberInfo, new Comparator<PropertyInfo>() {
      public int compare(PropertyInfo o1, PropertyInfo o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });

    PropertyInfo employerName = memberInfo.get(0);
    assertEquals(employerName.getMember().getName(), "setEmployerName");

    PropertyInfo surName = memberInfo.get(1);
    assertEquals(surName.getMember().getName(), "setSurName");
  }

  public void shouldMergeMappings() {
    TypeMap<Person, PersonDTO> personMap = modelMapper.createTypeMap(Person.class, PersonDTO.class);
    assertEquals(personMap.getMappings().size(), 1);

    personMap.addMappings(new PropertyMap<Person, PersonDTO>() {
      protected void configure() {
        map().setEmployerName(source.getEmployer());
        map().setSurName("smith");
      }
    });

    assertEquals(personMap.getMappings().size(), 3);
  }

  public void shouldOverrideMappings() {
    TypeMap<Person, PersonDTO> personMap = modelMapper.createTypeMap(Person.class, PersonDTO.class);
    assertEquals(personMap.getMappings().size(), 1);

    personMap.addMappings(new PropertyMap<Person, PersonDTO>() {
      protected void configure() {
        map().setFirstName("bob");
      }
    });

    assertEquals(personMap.getMappings().size(), 1);
    Map<String, Mapping> mappings = Mappings.groupByLastMemberName(personMap.getMappings());
    ConstantMapping firstName = (ConstantMapping) mappings.get("setFirstName");
    assertEquals(firstName.getConstant(), "bob");
  }

  public void shouldThrowOnLoadWhenDuplicateMappingsDetected() {
    TypeMap<Person, PersonDTO> personMap = modelMapper.createTypeMap(Person.class, PersonDTO.class);
    personMap.addMappings(new PropertyMap<Person, PersonDTO>() {
      protected void configure() {
        map().setEmployerName("bob");
      }
    });

    try {
      personMap.addMappings(new PropertyMap<Person, PersonDTO>() {
        protected void configure() {
          map().setEmployerName("joe");
        }
      });
    } catch (ConfigurationException e) {
      Asserts.assertContains(e.getMessage(), "1) A mapping already exists for");
      return;
    }

    fail();
  }

  public void shouldLoadWhenMemberMapIsValid() {
    TypeMap<Person, PersonDTO> personMap = modelMapper.createTypeMap(Person.class, PersonDTO.class);
    personMap.addMappings(new PropertyMap<Person, PersonDTO>() {
      protected void configure() {
        map().setSurName("smith");
      }
    });

    Map<String, Mapping> mappings = Mappings.groupByLastMemberName(personMap.getMappings());
    ConstantMapping surName = (ConstantMapping) mappings.get("setSurName");
    assertEquals(surName.getConstant(), "smith");
  }

  public void shouldThrowOnValidateWhenDestinationMembersMissing() {
    try {
      modelMapper.createTypeMap(Person.class, PersonDTO.class).validate();
    } catch (ValidationException e) {
      Asserts.assertContains(e.getMessage(), "1) Unmapped destination properties");
      return;
    }
  }

  public void shouldValidateWhenAllDestinationMembersMapped() {
    modelMapper.addMappings(new PropertyMap<Person, PersonDTO>() {
      @Override
      protected void configure() {
        map().setSurName("smith");
        map().setEmployerName("acme");
      }
    }).validate();
  }
}
