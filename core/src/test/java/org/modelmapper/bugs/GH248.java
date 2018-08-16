package org.modelmapper.bugs;

import java.util.Arrays;
import java.util.List;
import org.modelmapper.AbstractTest;
import org.modelmapper.Converters;
import org.modelmapper.Converters.Converter;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test
public class GH248 extends AbstractTest {
  static class Source {
    private List<String> lowerCase;
    private List<String> upperCase;

    public Source(List<String> lowerCase, List<String> upperCase) {
      this.lowerCase = lowerCase;
      this.upperCase = upperCase;
    }

    List<String> getLowerCase() {
      return lowerCase;
    }

    public void setLowerCase(List<String> lowerCase) {
      this.lowerCase = lowerCase;
    }

    List<String> getUpperCase() {
      return upperCase;
    }

    public void setUpperCase(List<String> upperCase) {
      this.upperCase = upperCase;
    }
  }

  static class Destination {
    private List<String> lowerCase;
    private List<String> upperCase;

    List<String> getLowerCase() {
      return lowerCase;
    }

    void setLowerCase(List<String> lowerCase) {
      this.lowerCase = lowerCase;
    }

    List<String> getUpperCase() {
      return upperCase;
    }

    void setUpperCase(List<String> upperCase) {
      this.upperCase = upperCase;
    }
  }

  public void shouldUseConvertersCollectionToConvert() {
    modelMapper.addMappings(new PropertyMap<Source, Destination>() {
      @Override
      protected void configure() {
        using(Converters.Collection.map(toLowerCase())).map().setLowerCase(source.getLowerCase());
        using(Converters.Collection.map(toUpperCase())).map().setUpperCase(source.getUpperCase());
      }
    });

    Source source = new Source(
        Arrays.asList("Foo", "Bar"),
        Arrays.asList("Foo", "Bar"));
    Destination destination = modelMapper.map(source, Destination.class);

    assertEquals(destination.getLowerCase().get(0), "foo");
    assertEquals(destination.getLowerCase().get(1), "bar");
    assertEquals(destination.getUpperCase().get(0), "FOO");
    assertEquals(destination.getUpperCase().get(1), "BAR");
  }

  private static Converters.Converter<String, String> toLowerCase() {
    return new Converter<String, String>() {
      @Override
      public String convert(String source) {
        return source.toLowerCase();
      }
    };
  }

  private static Converters.Converter<String, String> toUpperCase() {
    return new Converter<String, String>() {
      @Override
      public String convert(String source) {
        return source.toUpperCase();
      }
    };
  }
}
