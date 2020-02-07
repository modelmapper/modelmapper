package org.modelmapper.protobuf.gh392;

import static org.testng.Assert.assertEquals;

import java.util.Collections;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.protobuf.ProtobufModule;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Test
@SuppressWarnings("unused")
public class GH392 {
  static class Source {
    String type ="cool";
    List<Element1> e1 = Collections.singletonList(new Element1());
    String status = "hello";
  }

  static class Element1 {
    List<Element2> e2 = Collections.singletonList(new Element2());
  }

  static class Element2 {
    String name = "foo";
    String grade = "bar";
  }

  private ModelMapper modelMapper;

  @BeforeTest
  public void setUp() {
    modelMapper = new ModelMapper();
    modelMapper.registerModule(new ProtobufModule());
    modelMapper.getConfiguration()
        .setFieldAccessLevel(AccessLevel.PACKAGE_PRIVATE)
        .setFieldMatchingEnabled(true);
  }

  public void test() {
    Source source = new Source();
    DestinationInstructionProto.Destination.Builder destinationBuilder = modelMapper.map(source, DestinationInstructionProto.Destination.Builder.class);
    DestinationInstructionProto.Destination destination = destinationBuilder.build();
    assertEquals(destination.getType(), "cool");
    assertEquals(destination.getStatus(), "hello");
    assertEquals(destination.getE1Count(), 1);
    assertEquals(destination.getE1(0).getE2Count(), 1);
    assertEquals(destination.getE1(0).getE2(0).getName(), "foo");
    assertEquals(destination.getE1(0).getE2(0).getGrade(), "bar");
  }
}
