package org.modelmapper.functional.iterable;

import java.util.Arrays;
import java.util.Collection;

import org.modelmapper.AbstractConverter;
import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author Jonathan Halterman
 */
@Test
public class CollectionWithConverter extends AbstractTest {
  static class Source {
    Collection<String> images;
  }

  static class Destination {
    int numOfImages;
  }

  public void shouldMapWithCustomConverter() {
    modelMapper.createTypeMap(Source.class, Destination.class).setConverter(
        new AbstractConverter<Source, Destination>() {
          protected Destination convert(Source source) {
            Destination dest = new Destination();
            dest.numOfImages = source.images.size();
            return dest;
          }
        });

    Source source = new Source();
    source.images = Arrays.asList("1", "2", "3");
    Destination dest = modelMapper.map(source, Destination.class);
    assertEquals(dest.numOfImages, 3);
  }
}
