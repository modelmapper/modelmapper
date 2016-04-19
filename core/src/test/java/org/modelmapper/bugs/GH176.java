package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

import org.modelmapper.AbstractConverter;
import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * @author Lasse Lindgard / lldata
 */
@Test(groups = "functional")
public class GH176 extends AbstractTest {
  static class Source {
    DTO key;

    public DTO getKey() {
      return key;
    }

    public void setKey(DTO key) {
      this.key = key;
    }
  }

  static class DTO {
    String value;
  }

  static class Wrapper {
    private final String value;

    // required by ModelMapper, even though there is no reason to call it
    private Wrapper() {
      this.value = null;
    }

    public Wrapper(String value) {
      this.value = value;
    }
  }

  static class Wrapper2 {
    private final String value;

    // required by ModelMapper, even though there is no reason to call it
    private Wrapper2() {
      this.value = null;
    }

    public Wrapper2(String value) {
      this.value = value;
    }
  }

  static class Dest {
    Wrapper key1;
    Wrapper key2;
    String key3;
    Wrapper2 key4;
    Wrapper2 key5;

    public Wrapper getKey1() {
      return key1;
    }

    public void setKey1(Wrapper key1) {
      this.key1 = key1;
    }

    public Wrapper getKey2() {
      return key2;
    }

    public void setKey2(Wrapper key2) {
      this.key2 = key2;
    }

    public String getKey3() {
      return key3;
    }

    public void setKey3(String key3) {
      this.key3 = key3;
    }
  }

  public void shouldReuseConverter() {
    modelMapper.addMappings(new PropertyMap<Source, Dest>() {
      @Override
      protected void configure() {
        map(source.key, destination.key1);
        map(source.key, destination.key2);
      }
    });
    modelMapper.addConverter(new AbstractConverter<DTO, Wrapper>() {
      @Override
      protected Wrapper convert(DTO source) {
        return new Wrapper(source.value);
      }
    });

    Source source = new Source();
    source.key = new DTO();
    source.key.value = "test";
    Dest dest = modelMapper.map(source, Dest.class);
    assertNotNull(dest.key1);
    assertEquals(dest.key1.value, "test");
    // would expect that key2 == key1, but it is not
    // instead it uses a cached version of a call to the empty constuctor of Wrapper, that is discarded in the first mapping
    assertEquals(dest.key2.value, "test");
    assertSame(dest.key1, dest.key2);
  }

  public void shouldAllowConverterToMultipleDestinationSources() {
    modelMapper.addMappings(new PropertyMap<Source, Dest>() {
      @Override
      protected void configure() {
        // DTO -> Wrapper (OK)
        map(source.key, destination.key1);
        // DTO -> String (fails - ModelMapper attempts to reuse cached value of type Wrapper)
        map(source.key, destination.key3);
      }
    });
    modelMapper.addConverter(new AbstractConverter<DTO, Wrapper>() {
      @Override
      protected Wrapper convert(DTO source) {
        return new Wrapper(source.value);
      }
    });
    modelMapper.addConverter(new AbstractConverter<DTO, String>() {
      @Override
      protected String convert(DTO source) {
        return source.value;
      }
    });

    Source source = new Source();
    source.key = new DTO();
    source.key.value = "test";
    Dest dest = modelMapper.map(source, Dest.class);
    assertNotNull(dest.key1);
    assertEquals(dest.key1.value, "test");
    assertEquals(dest.key3, "test");
  }

  public void shouldAllowToReuseMultipleConverters() {
    modelMapper.addMappings(new PropertyMap<Source, Dest>() {
      @Override
      protected void configure() {
        // DTO -> Wrapper
        map(source.key, destination.key1);
        // DTO -> Wrapper2
        map(source.key, destination.key4);

        // DTO -> Wrapper
        map(source.key, destination.key2);
        // DTO -> Wrapper2
        map(source.key, destination.key5);
      }
    });
    modelMapper.addConverter(new AbstractConverter<DTO, Wrapper>() {
      @Override
      protected Wrapper convert(DTO source) {
        return new Wrapper(source.value);
      }
    });
    modelMapper.addConverter(new AbstractConverter<DTO, Wrapper2>() {
      @Override
      protected Wrapper2 convert(DTO source) {
        return new Wrapper2(source.value);
      }
    });

    Source source = new Source();
    source.key = new DTO();
    source.key.value = "test";
    Dest dest = modelMapper.map(source, Dest.class);
    assertNotNull(dest.key1);
    assertEquals(dest.key1.value, "test");
    assertEquals(dest.key2.value, "test");
    assertSame(dest.key1, dest.key2);
    assertEquals(dest.key4.value, "test");
    assertEquals(dest.key5.value, "test");
    // will only be the same if we are able to cache converter results of multiple types
    assertSame(dest.key4, dest.key5);

  }
}
