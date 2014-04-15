package org.modelmapper.functional.iterable;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

@Test(groups = "functional")
public class TypedMapMapping extends AbstractTest {
  static class Person {
    Map<String, List<String>> groups;
  }

  static class PersonDTO {
    Map<String, List<String>> groups;
  }

  public void test() {
    Person p = new Person();
    p.groups = new HashMap<String, List<String>>();
    p.groups.put("1", Arrays.asList("a", "b", "c"));
    p.groups.put("2", Arrays.asList("d", "e", "f"));

    PersonDTO dto = modelMapper.map(p, PersonDTO.class);
    assertEquals(dto.groups, p.groups);
  }
}
