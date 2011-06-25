package org.modelmapper;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.config.Configuration.AccessLevel;

public class Test {
  public static class Baz {
    private List<Foo> foo = new ArrayList<Foo>();
  }

  public static class Foo {
    private String value;

    Foo(String value) {
      this.value = value;
    }
  }

  public static class Target {
    private List<String> fooValue = new ArrayList<String>();
  }

  public static void main(String... args) {
    Baz baz = new Baz();
    baz.foo.add(new Foo("abc"));
    baz.foo.add(new Foo("123"));

    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().enableFieldMatching(true)
        .setFieldAccessLevel(AccessLevel.PRIVATE);
    Target bList = modelMapper.map(baz, Target.class);
    int i = 0;
  }
}
