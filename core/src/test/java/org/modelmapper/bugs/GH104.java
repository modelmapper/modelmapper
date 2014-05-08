package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

@Test
public class GH104 extends AbstractTest {
  static class Person {
    String name;
    Person person;
  }

  public void shouldMapExistingInstancesOfSameType() {
    Person source = new Person();
    source.name = "John Smith";
    source.person = new Person();
    source.person.name = "foo";
    Person destination = new Person();

    modelMapper.map(source, destination);

    assertEquals(destination.name, source.name);
    assertEquals(destination.person.name, source.person.name);
  }
}
