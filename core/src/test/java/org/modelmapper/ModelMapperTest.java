package org.modelmapper;

import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.Mapping;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * @author Jonathan Halterman
 */
@Test
public class ModelMapperTest extends AbstractTest {
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

    String getEmployerName() {
      return employerName;
    }

    void setEmployerName(String employerName) {
      this.employerName = employerName;
    }

    void setSurName(String surName) {
      this.surName = surName;
    }
  }
  public static class PersonDTOSourceMemberMissing {
    String firstName;
    String lastName;

    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }

    public void setLastName(String lastName) {
      this.lastName = lastName;
    }
  }

  public void shouldGetTypeMap() {
    TypeMap<Person, PersonDTO> map = modelMapper.createTypeMap(Person.class, PersonDTO.class);
    assertEquals(modelMapper.getTypeMap(Person.class, PersonDTO.class), map);
    assertEquals(map.getSourceType(), Person.class);
    assertEquals(map.getDestinationType(), PersonDTO.class);
  }

  public void shouldCreateTypeMap() {
    TypeMap<Person, PersonDTO> map = modelMapper.createTypeMap(Person.class, PersonDTO.class);
    assertEquals(map.getSourceType(), Person.class);
    assertEquals(map.getDestinationType(), PersonDTO.class);
  }

  public void shouldCreateTypeMapForConfig() {
    TypeMap<Person, PersonDTO> typeMap = modelMapper.createTypeMap(
        Person.class,
        PersonDTO.class,
        modelMapper.getConfiguration().copy().enableFieldMatching(false)
            .setMethodAccessLevel(AccessLevel.PUBLIC));
    assertTrue(typeMap.getMappings().isEmpty());
  }

  public void shouldRegisterMapper() {
    modelMapper.addConverter(new AbstractConverter<Object, Object>() {
      public Object convert(Object source) {
        return null;
      }
    });
  }

  public void shouldLoadWhenMemberMapIsValid() {
    TypeMap<Person, PersonDTO> personMap = modelMapper
        .addMappings(new PropertyMap<Person, PersonDTO>() {
          @Override
          protected void configure() {
            map().setSurName("smith");
          }
        });

    Map<String, Mapping> mappings = Mappings.groupByLastMemberName(personMap.getMappings());
    assertNotNull(mappings.get("setSurName"));
  }

  public void shouldValidateWhenAllDestinationMembersMapped() {
    modelMapper.addMappings(new PropertyMap<Person, PersonDTO>() {
      @Override
      protected void configure() {
        map().setSurName("smith");
        map().setEmployerName("acme");
      }
    });

    modelMapper.validate();
  }

  public void shouldThrowOnValidateWhenDestinationMembersMissing() {
    modelMapper.createTypeMap(Person.class, PersonDTO.class);
    try {
      modelMapper.validate(Validators.DESTINATION_PROPERTIES_MAPPED);
    } catch (ValidationException e) {
      Asserts.assertContains(e.getMessage(), "1) Unmapped destination properties found");
      return;
    }

    fail();
  }

  public void shouldThrowOnValidateWhenSourceMembersMissing() {
    modelMapper.createTypeMap(Person.class, PersonDTOSourceMemberMissing.class);
    try {
      modelMapper.validate(Validators.SOURCE_PROPERTIES_MAPPED);
    } catch (ValidationException e) {
      Asserts.assertContains(e.getMessage(), "1) Unmapped source properties found");
      return;
    }
    fail("No validation error thrown...");
    }
}
