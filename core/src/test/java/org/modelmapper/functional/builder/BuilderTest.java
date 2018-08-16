package org.modelmapper.functional.builder;

import static org.testng.Assert.assertEquals;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.NameTransformers;
import org.modelmapper.convention.NamingConventions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class BuilderTest {
  static class Source {
    private String foo;
    private String bar;

    public Source(String foo, String bar) {
      this.foo = foo;
      this.bar = bar;
    }

    public String getFoo() {
      return foo;
    }

    public void setFoo(String foo) {
      this.foo = foo;
    }

    public String getBar() {
      return bar;
    }

    public void setBar(String bar) {
      this.bar = bar;
    }
  }

  static class Destination {
    private final String foo;
    private final String bar;

    public Destination(String foo, String bar) {
      this.foo = foo;
      this.bar = bar;
    }

    static class Builder {
      private String foo;
      private String bar;

      public Builder foo(String foo) {
        this.foo = foo;
        return this;
      }

      public Builder bar(String bar) {
        this.bar = bar;
        return this;
      }

      Destination build() {
        return new Destination(foo, bar);
      }
    }

    static class BuilderWith {
      private String foo;
      private String bar;

      public BuilderWith withFoo(String foo) {
        this.foo = foo;
        return this;
      }

      public BuilderWith withBar(String bar) {
        this.bar = bar;
        return this;
      }

      Destination build() {
        return new Destination(foo, bar);
      }
    }
  }

  private ModelMapper modelMapper;

  @BeforeMethod
  public void setUp() {
    modelMapper = new ModelMapper();
  }

  public void shouldMap() {
    Configuration builderConfiguration = modelMapper.getConfiguration().copy()
        .setDestinationNameTransformer(NameTransformers.builder())
        .setDestinationNamingConvention(NamingConventions.builder());
    modelMapper.createTypeMap(Source.class, Destination.Builder.class, builderConfiguration);

    Source source = new Source("foo", "bar");
    Destination destination = modelMapper.map(source, Destination.Builder.class).build();
    assertEquals("foo", destination.foo);
    assertEquals("bar", destination.bar);
  }

  public void shouldMapWithDifferentPrefix() {
    Configuration builderConfiguration = modelMapper.getConfiguration().copy()
        .setDestinationNameTransformer(NameTransformers.builder("with"))
        .setDestinationNamingConvention(NamingConventions.builder("with"));
    modelMapper.createTypeMap(Source.class, Destination.BuilderWith.class, builderConfiguration);

    Source source = new Source("foo", "bar");
    Destination destination = modelMapper.map(source, Destination.BuilderWith.class).build();
    assertEquals("foo", destination.foo);
    assertEquals("bar", destination.bar);
  }
}
