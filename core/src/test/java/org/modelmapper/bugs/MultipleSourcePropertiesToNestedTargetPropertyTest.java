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
public class MultipleSourcePropertiesToNestedTargetPropertyTest extends AbstractTest {

  public static class Source {
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

    public Source getSelf() {
      return this;
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
    ModelMapper modelMapper1 = Fixtures.createModelMapper();
    ModelMapper modelMapper2 = Fixtures.createModelMapper();

    TypeMap<Source, TargetParent> typeMap1 = modelMapper1.createTypeMap(Source.class, TargetParent.class);
    TypeMap<Source, TargetParent> typeMap2 = modelMapper2.createTypeMap(Source.class, TargetParent.class);

    Converter<Source, String> converter = context -> {
      Source source = context.getSource();
      return source.getFirstName() + " " + source.getLastName();
    };

    DestinationSetter<TargetParent, String> destinationSetter = (dest, val) -> dest.getTargetChild().setFullName(val);

    typeMap1.addMappings(mapper -> {
      mapper.using(converter).<String>map(src -> src.getSelf(), destinationSetter);
    });

    typeMap2.addMappings(mapper -> {
      mapper.using(converter).<String>map(src -> src, destinationSetter);
    });

    Source source = new Source();
    source.setFirstName("foo");
    source.setLastName("bar");

    TargetParent target1 = modelMapper1.map(source, TargetParent.class);
    TargetParent target2 = modelMapper2.map(source, TargetParent.class);

    assertNotNull(target1.getTargetChild());
    assertNotNull(target2.getTargetChild());
    assertEquals(
        target1.getTargetChild().getFullName(),
        target2.getTargetChild().getFullName());
  }

}
