package org.modelmapper.functional.converter;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.AbstractTest;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

/**
 * From https://github.com/jhalterman/modelmapper/issues/95
 */
@Test(groups = "functional")
public class PassNullSourcesToGlobalConditionalConvertersTest extends AbstractTest {
  static class Source {
    public List<String> list;
  }

  static class Dest {
    public List<String> list;
  }

  private static class ReplaceNullWithEmptyLists implements ConditionalConverter<Object, Object> {
    public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
      return List.class.isAssignableFrom(sourceType)
          && List.class.isAssignableFrom(destinationType) ? MatchResult.FULL : MatchResult.NONE;
    }

    public Object convert(MappingContext<Object, Object> context) {
      return new ArrayList<Object>();
    }
  }

  @Test
  public void empowerConditionalConvertersToHandleNullPropertiesInSourceObject() throws Exception {
    modelMapper.getConfiguration().getConverters().add(0, new ReplaceNullWithEmptyLists());
    assertTrue(modelMapper.map(new Source(), Dest.class).list.isEmpty());
  }
}