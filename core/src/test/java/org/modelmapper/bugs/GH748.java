package org.modelmapper.bugs;

import org.modelmapper.AbstractTest;
import org.modelmapper.Converter;
import org.modelmapper.Fixtures;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.spi.DestinationSetter;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

/**
 * @author Fabian Landwehr
 */
@Test
public class GH748 extends AbstractTest {
  static class Source {
    private String firstName;
    private String lastName;

    public String getFirstName() {
      return firstName;
    }

    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public void setLastName(String lastName) {
      this.lastName = lastName;
    }
  }

  public static class TargetParent {
    private TargetChild targetChild;

    public TargetChild getTargetChild() {
      return targetChild;
    }

    public void setTargetChild(TargetChild targetChild) {
      this.targetChild = targetChild;
    }

  }

  public static class TargetChild {
    private String fullName;

    public String getFullName() {
      return fullName;
    }

    public void setFullName(String fullName) {
      this.fullName = fullName;
    }

  }

  public void shouldBehaveTheSameForBothSourceGetters() {
    ModelMapper modelMapper = Fixtures.createModelMapper();

    Converter<Source, String> converter = context -> {
      Source source = context.getSource();
      return source.getFirstName() + " " + source.getLastName();
    };
    modelMapper.createTypeMap(Source.class, TargetParent.class).addMappings(mapper -> mapper.using(converter)
        .map(src -> src, (TargetParent dest, String value) -> dest.getTargetChild().setFullName(value)));

    Source source = new Source();
    source.setFirstName("foo");
    source.setLastName("bar");

    TargetParent target2 = modelMapper.map(source, TargetParent.class);
    assertEquals(target2.getTargetChild().getFullName(), "foo bar");
  }
}
