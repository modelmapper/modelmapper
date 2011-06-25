package org.modelmapper.functional;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class MismatchedProperties extends AbstractTest {
  static class Person {
    Employee employee;

    public Employee getEmployee() {
      return employee;
    }

    public void setEmployee(Employee employee) {
      this.employee = employee;
    }
  }

  static class Employee {
    int id;

    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }
  }

  static class PersonDTO {
    int employeeId;

    public int getEmployeeId() {
      return employeeId;
    }

    public void setEmployeeId(int employeeId) {
      this.employeeId = employeeId;
    }
  }

  public void shouldMapMismatchedProperties() {
    modelMapper.addMappings(new PropertyMap<PersonDTO, Person>() {
      protected void configure() {
        map(source).setEmployee(null);
      }
    });

    PersonDTO dto = new PersonDTO();
    dto.setEmployeeId(5);
    Person person = modelMapper.map(dto, Person.class);
    assertEquals(person.employee.id, 5);
  }
}
