package org.modelmapper.functional.conditional;

import static org.testng.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicBoolean;

import org.modelmapper.AbstractTest;
import org.modelmapper.Condition;
import org.modelmapper.Conditions;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class ConditionalTest extends AbstractTest {
  static class Source {
    String value;
  }

  static class Dest {
    String value;

    void setValue(String value) {
      this.value = value;
    }
  }

  public void shouldUseTypeMapPropertyCondition() {
    modelMapper.createTypeMap(Source.class, Dest.class)
               .setPropertyCondition(Conditions.isNotNull());

    Source source = new Source();
    source.value = "dummy";
    Dest dest = modelMapper.map(source, Dest.class);
    assertEquals(dest.value, "dummy");
  }

  public void shouldUseTypeMapCondition() {
    final AtomicBoolean condition = new AtomicBoolean();
    modelMapper.createTypeMap(Source.class, Dest.class).setCondition(
        new Condition<Object, Object>() {
          public boolean applies(MappingContext<Object, Object> context) {
            return condition.get();
          }
        });

    Source source = new Source();
    source.value = "test";

    // Negative test
    Dest dest = modelMapper.map(source, Dest.class);
    assertEquals(dest.value, null);

    // Positive test
    condition.set(true);
    dest = modelMapper.map(source, Dest.class);
    assertEquals(dest.value, "test");
  }

  public void propertyMapConditionShouldOverrideTypeMapPropertyCondition() {
    final AtomicBoolean condition = new AtomicBoolean();
    modelMapper.createTypeMap(Source.class, Dest.class)
               .setPropertyCondition(Conditions.isNotNull())
               .addMappings(new PropertyMap<Source, Dest>() {
                 @Override
                 protected void configure() {
                   when(new Condition<Object, Object>() {
                     public boolean applies(MappingContext<Object, Object> context) {
                       return condition.get();
                     }
                   }).map().setValue("dummy");
                 }
               });

    Source source = new Source();
    source.value = "test";

    // Negative test
    Dest dest = modelMapper.map(new Source(), Dest.class);
    assertEquals(dest.value, null);

    // Positive test
    condition.set(true);
    dest = modelMapper.map(new Source(), Dest.class);
    assertEquals(dest.value, "dummy");
  }
}
