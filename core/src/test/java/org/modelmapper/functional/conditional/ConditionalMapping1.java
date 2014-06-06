package org.modelmapper.functional.conditional;

import static org.testng.Assert.*;

import org.modelmapper.AbstractTest;
import org.modelmapper.Conditions;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class ConditionalMapping1 extends AbstractTest {
  static class Source {
    public String getName() {
      return null;
    }
  }

  static class Dest {
    String name = "abc";
  }

  public void shouldSkipConditionalTypeMapping() {
    modelMapper.createTypeMap(Source.class, Dest.class).setCondition(Conditions.isNull());
    Dest dest = modelMapper.map(new Source(), Dest.class);
    assertEquals(dest.name, "abc");
  }

  public void shouldSkipConditionalPropertyMappingViaPropertyMap() {
    modelMapper.addMappings(new PropertyMap<Source, Dest>() {
      @Override
      protected void configure() {
        when(Conditions.isNull()).skip(source.getName(), destination.name);
      }
    });

    Dest dest = modelMapper.map(new Source(), Dest.class);
    assertEquals(dest.name, "abc");
  }
}
