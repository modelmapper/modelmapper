package org.modelmapper;

import org.testng.annotations.Test;

@Test
public class ConstructorMapperTest {
  public class Person {
    private final String name;
    private final String address;

    public Person(String name, String address) {
      this.name = name;
      this.address = address;
    }

    public String getName() {
      return name;
    }

    public String getAddress() {
      return address;
    }
  }

  public class NewPerson {
    private final String name;
    private final String address;

    public NewPerson(String name, String address) {
      this.name = name;
      this.address = address;
    }

    public String getName() {
      return name;
    }

    public String getAddress() {
      return address;
    }
  }
  @Test
  public void loadShouldRequireTypeParameters() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.createTypeMap(Person.class, NewPerson.class);
    Person person = new Person("John Doe", "123 Main St");
    NewPerson newPerson = modelMapper.map(person, NewPerson.class);
    assert newPerson.getName().equals("John Doe");
    assert newPerson.getAddress().equals("123 Main St");
  }
}
