package org.modelmapper.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Map;

import org.modelmapper.Asserts;
import org.modelmapper.Condition;
import org.modelmapper.ConfigurationException;
import org.modelmapper.Converter;
import org.modelmapper.Mappings;
import org.modelmapper.PropertyMap;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.spi.ConstantMapping;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.PropertyMapping;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class MappingBuilderImplTest {
  InheritingConfiguration configuration;
  ExplicitMappingBuilder<Person, PersonDTO> builder;
  ExplicitMappingBuilder<Object, Object> objectObjectBuilder;

  private static Converter<String, String> UPPERCASE_CONVERTER = new Converter<String, String>() {
    public String convert(MappingContext<String, String> context) {
      return context.getSource().toUpperCase();
    }
  };

  private static Condition<?, ?> CONDITION = new Condition<Object, Object>() {
    public boolean applies(MappingContext<Object, Object> context) {
      return true;
    }
  };

  static class Address {
    String getStreetName() {
      return "1234 main street";
    }
  }

  static class AddressDTO {
    void setStreet(String street) {
    }
  }

  static class D1 {
    D2 d2 = new D2();

    D2 getD2() {
      return d2;
    }
  }

  static class D2 {
    String value1;
    String value2;

    void setValue1(String value1) {
      this.value1 = value1;
    }

    void setValue2(String value2) {
      this.value2 = value2;
    }
  }

  static class MiddleName {
    String middleName;
  }

  static class Person {
    String firstName;
    MiddleName middleName;
    String lastName;
    String employer;
    Address address;

    final String finalMethod() {
      return null;
    }

    Address getAddress() {
      return address;
    }

    String getEmployer() {
      return employer;
    }

    String getLastName() {
      return lastName;
    }

    MiddleName getMiddleName() {
      return middleName;
    }
  }

  static class PersonDTO {
    String firstName;
    String middleName;
    String surName;
    String employerName;

    AddressDTO getAddress() {
      return null;
    }

    String getEmployerName() {
      return employerName;
    }

    void setActive(boolean active) {
    }

    void setAddress(AddressDTO address) {
    }

    void setEmployerName(String employerName) {
      this.employerName = employerName;
    }

    void setObject(Object o) {
    }

    void setSurName(String surName) {
      this.surName = surName;
    }
  }

  @BeforeMethod
  public void init() {
    configuration = (InheritingConfiguration) new InheritingConfiguration().setFieldMatchingEnabled(
        true)
        .setFieldAccessLevel(AccessLevel.PACKAGE_PRIVATE)
        .setMethodAccessLevel(AccessLevel.PACKAGE_PRIVATE);
    builder = new ExplicitMappingBuilder<Person, PersonDTO>(Person.class, PersonDTO.class,
        configuration);
    objectObjectBuilder = new ExplicitMappingBuilder<Object, Object>(Object.class, Object.class,
        configuration);
  }

  public void shouldBuildConditionalSkippedMappings() {
    Map<String, MappingImpl> mappings = Mappings.groupByLastMemberName(builder.build(new PropertyMap<Person, PersonDTO>() {
      protected void configure() {
        when(CONDITION).skip().setEmployerName(null);
        when(CONDITION).map().setSurName("smith");
      }
    }));

    ConstantMapping employer = (ConstantMapping) mappings.get("setEmployerName");
    assertEquals(employer.getCondition(), CONDITION);
    assertTrue(employer.isSkipped());
    assertEquals(employer.getConstant(), null);

    ConstantMapping surName = (ConstantMapping) mappings.get("setSurName");
    assertEquals(surName.getCondition(), CONDITION);
    assertFalse(surName.isSkipped());
    assertEquals(surName.getConstant(), "smith");
  }

  public void shouldBuildConditionalMappingsWithConverter() {
    Map<String, MappingImpl> mappings = Mappings.groupByLastMemberName(builder.build(new PropertyMap<Person, PersonDTO>() {
      protected void configure() {
        when(CONDITION).using(UPPERCASE_CONVERTER).map().setEmployerName("joe");
      }
    }));

    ConstantMapping employer = (ConstantMapping) mappings.get("setEmployerName");
    assertEquals(employer.getCondition(), CONDITION);
    assertEquals(employer.getConverter(), UPPERCASE_CONVERTER);
    assertEquals(employer.getConstant(), "joe");
  }

  public void shouldBuildDeepMappings() {
    Map<String, MappingImpl> mappings = Mappings.groupByLastMemberName(builder.build(new PropertyMap<Person, PersonDTO>() {
      protected void configure() {
        map().getAddress().setStreet(source.getAddress().getStreetName());
      }
    }));

    assertEquals(mappings.size(), 1);
  }

  public void shouldBuildSkippedMappings() {
    Map<String, MappingImpl> mappings = Mappings.groupByLastMemberName(builder.build(new PropertyMap<Person, PersonDTO>() {
      protected void configure() {
        skip().setEmployerName(null);
      }
    }));

    assertTrue(mappings.get("setEmployerName").isSkipped());
  }

  public void shouldBuildMappings() {
    Map<String, MappingImpl> mappings = Mappings.groupByLastMemberName(builder.build(new PropertyMap<Person, PersonDTO>() {
      protected void configure() {
        map().setEmployerName(source.getEmployer());
        map().setSurName("jones");
      }
    }));

    PropertyMapping employer = (PropertyMapping) mappings.get("setEmployerName");
    ConstantMapping surName = (ConstantMapping) mappings.get("setSurName");
    assertEquals(employer.getLastSourceProperty().getMember().getName(), "getEmployer");
    assertEquals(employer.getLastDestinationProperty().getMember().getName(), "setEmployerName");
    assertEquals(surName.getConstant(), "jones");
    assertEquals(surName.getLastDestinationProperty().getMember().getName(), "setSurName");
  }

  public void shouldBuildMappingsWithConverter() {
    Map<String, MappingImpl> mappings = Mappings.groupByLastMemberName(builder.build(new PropertyMap<Person, PersonDTO>() {
      protected void configure() {
        using(UPPERCASE_CONVERTER).map().setEmployerName("joe");
      }
    }));

    ConstantMapping employer = (ConstantMapping) mappings.get("setEmployerName");
    assertEquals(employer.getConverter(), UPPERCASE_CONVERTER);
    assertEquals(employer.getConstant(), "joe");
  }

  public void shouldThrowWhenDestinationMethodIsFinal() {
    try {
      objectObjectBuilder.build(new PropertyMap<Object, Object>() {
        protected void configure() {
          map().getClass();
        }
      });
    } catch (ConfigurationException e) {
      assertEquals(e.getErrorMessages().size(), 1);
      Asserts.assertContains(e.getMessage(),
          "1) A mapping is missing a required destination method");
      return;
    }

    fail();
  }

  public void shouldThrowWhenDestinationMethodIsMissing() {
    try {
      objectObjectBuilder.build(new PropertyMap<Object, Object>() {
        protected void configure() {
          map();
        }
      });
    } catch (ConfigurationException e) {
      Asserts.assertContains(e.getMessage(),
          "1) A mapping is missing a required destination method.");
      return;
    }

    fail();
  }

  // Not sure if this scenario can be checked
  // public void shouldThrowOnMisplacedMap() {
  // try {
  // objectObjectBuilder.record(new MemberMap<Object, Object>() {
  // protected void configure() {
  // map().equals(map());
  // }
  // });
  // } catch (ConfigurationException e) {
  // Asserts.assertContains(e.getMessage(), "1) xxx");
  // return;
  // }
  //
  // fail();
  // }

  public void shouldThrowWhenDestinationTypeIsFinal() {
    try {
      ExplicitMappingBuilder<Object, String> builder = new ExplicitMappingBuilder<Object, String>(
          Object.class, String.class, configuration);
      builder.build(new PropertyMap<Object, String>() {
        protected void configure() {
          map().length();
        }
      });
    } catch (ConfigurationException e) {
      Asserts.assertContains(e.getMessage(), "1) Cannot map to final type.");
      return;
    }

    fail();
  }

  public void shouldThrowWhenDuplicateMappingsAreDefined() {
    try {
      builder.build(new PropertyMap<Person, PersonDTO>() {
        protected void configure() {
          map().setEmployerName("joe");
          map().setEmployerName("bob");
        }
      });
    } catch (ConfigurationException e) {
      Asserts.assertContains(e.getMessage(), "1) A mapping already exists for");
      return;
    }

    fail();
  }

  public void shouldThrowWhenInvalidDestinationMethod() {
    try {
      objectObjectBuilder.build(new PropertyMap<Object, Object>() {
        protected void configure() {
          map().equals(null);
        }
      });
    } catch (ConfigurationException e) {
      Asserts.assertContains(e.getMessage(), "1) Invalid destination method");
      return;
    }

    fail();
  }

  public void shouldThrowWhenInvalidSourceAndDestinationMethods() {
    try {
      builder.build(new PropertyMap<Person, PersonDTO>() {
        protected void configure() {
          map().setActive(source.equals(null));
          map().equals(null);
        }
      });
    } catch (ConfigurationException e) {
      Asserts.assertContains(e.getMessage(), "Invalid source method");
      Asserts.assertContains(e.getMessage(), "Invalid destination method");
      return;
    }

    fail();
  }

  public void shouldThrowWhenInvalidSourceMethod() {
    try {
      builder.build(new PropertyMap<Person, PersonDTO>() {
        protected void configure() {
          map().setActive(source.equals(null));
        }
      });
    } catch (ConfigurationException e) {
      Asserts.assertContains(e.getMessage(), "1) Invalid source method");
      return;
    }

    fail();
  }

  public void shouldThrowWhenSourceMethodIsFinal() {
    try {
      builder.build(new PropertyMap<Person, PersonDTO>() {
        protected void configure() {
          map().setEmployerName(source.finalMethod().toLowerCase());
        }
      });
    } catch (ConfigurationException e) {
      assertEquals(e.getErrorMessages().size(), 1);
      Asserts.assertContains(e.getMessage(), "1) Cannot map to final type");
      return;
    }

    fail();
  }

  public void shouldThrowWhenSourceMethodIsMissing() {
    try {
      builder.build(new PropertyMap<Person, PersonDTO>() {
        protected void configure() {
          map().setObject(source);
        }
      });
    } catch (ConfigurationException e) {
      Asserts.assertContains(e.getMessage(), "1) A mapping is missing a required source method.");
      return;
    }

    fail();
  }

  public void shouldThrowWhenSourceTypeIsFinal() {
    try {
      ExplicitMappingBuilder<String, Object> builder = new ExplicitMappingBuilder<String, Object>(
          String.class, Object.class, configuration);
      builder.build(new PropertyMap<String, Object>() {
        protected void configure() {
          map().equals(source.length());
        }
      });
    } catch (ConfigurationException e) {
      Asserts.assertContains(e.getMessage(), "1) Cannot map to final type.");
      return;
    }

    fail();
  }
}
