package org.modelmapper.functional.conditional;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.modelmapper.AbstractTest;
import org.modelmapper.Condition;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

/**
 * Written in response to http://stackoverflow.com/questions/6366651/
 * 
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class ConditionalMapping2 extends AbstractTest {
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

  Condition<?, ?> isNotZero = new Condition<PersonDTO, Employee>() {
    public boolean applies(MappingContext<PersonDTO, Employee> context) {
      return context.getSource().getEmployeeId() != 0;
    }
  };

  /**
   * Performs mapping when the source
   */
  public void shouldMapConditionally() {
    modelMapper.addMappings(new PropertyMap<PersonDTO, Person>() {
      protected void configure() {
        when(isNotZero).map(source).setEmployee(null);
      }
    });

    // Positive
    PersonDTO dto = new PersonDTO();
    dto.setEmployeeId(5);
    Person person = modelMapper.map(dto, Person.class);
    assertEquals(person.employee.id, 5);

    // Negative
    dto.setEmployeeId(0);
    person = modelMapper.map(dto, Person.class);
    assertNull(person.employee);
  }
}
