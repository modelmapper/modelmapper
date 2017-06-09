package org.modelmapper;

import static org.testng.Assert.*;

import java.util.Map;

import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

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
        modelMapper.getConfiguration()
            .copy()
            .setFieldMatchingEnabled(false)
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
    TypeMap<Person, PersonDTO> personMap = modelMapper.addMappings(new PropertyMap<Person, PersonDTO>() {
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
      modelMapper.validate();
    } catch (ValidationException e) {
      Asserts.assertContains(e.getMessage(), "1) Unmapped destination properties found");
      return;
    }

    fail();
  }

  public void shouldTypeMapCreateOrGet() {
    TypeMap<Person, PersonDTO> typeMap = modelMapper.typeMap(Person.class, PersonDTO.class);
    assertNotNull(typeMap);
    assertSame(modelMapper.typeMap(Person.class, PersonDTO.class), typeMap);

    TypeMap<Person, PersonDTO> typeMapWithName = modelMapper.typeMap(Person.class, PersonDTO.class, "foo");
    assertNotNull(typeMapWithName);
    assertNotSame(typeMapWithName, typeMap);
    assertSame(modelMapper.typeMap(Person.class, PersonDTO.class, "foo"), typeMapWithName);

    TypeMap<Person, PersonDTO> typeMapWithDifferentName = modelMapper.typeMap(Person.class, PersonDTO.class, "bar");
    assertNotNull(typeMapWithDifferentName);
    assertNotSame(typeMapWithDifferentName, typeMap);
    assertNotSame(typeMapWithDifferentName, typeMapWithName);
    assertSame(modelMapper.typeMap(Person.class, PersonDTO.class, "bar"), typeMapWithDifferentName);
  }
}
