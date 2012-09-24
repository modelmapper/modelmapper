package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * @author Andy Moody
 */
@Test
public class CircularMappingBug extends AbstractTest {
  public static class TestContainer {
    TestContainer2 someMembers;
  }

  public static class TestContainer2 {
    List<String> someMembers;
  }

  public static class TestContainerDTO {
    TestContainer2DTO someMembers;
  }

  public static class TestContainer2DTO {
    List<String> someMembers;
  }

  public void testMapping() {
    TestContainer source = new TestContainer();
    source.someMembers = new TestContainer2();
    source.someMembers.someMembers = Arrays.asList("some String", "some String 2");

    TestContainerDTO dest = modelMapper.map(source, TestContainerDTO.class);
    assertEquals(dest.someMembers.someMembers, source.someMembers.someMembers);
  }
}
