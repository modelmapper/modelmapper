package org.modelmapper.bugs;

import java.util.Collections;
import java.util.List;
import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test
public class GH360 extends AbstractTest {
  static class Foo {
    private String id;
    private List<Bar> bars;

    public Foo() {
    }

    public Foo(String id, List<Bar> bars) {
      this.id = id;
      this.bars = bars;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public List<Bar> getBars() {
      return bars;
    }

    public void setBars(List<Bar> bars) {
      this.bars = bars;
    }
  }

  static class Bar {
    private String id;
    private String name;

    public Bar() {
    }

    public Bar(String id, String name) {
      this.id = id;
      this.name = name;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  @BeforeMethod
  public void setUp() {
    modelMapper.typeMap(Foo.class, Foo.class).addMappings(new PropertyMap<Foo, Foo>() {
      @Override
      protected void configure() {
        skip().setId(null);
      }
    });
    modelMapper.typeMap(Bar.class, Bar.class).addMappings(new PropertyMap<Bar, Bar>() {
      @Override
      protected void configure() {
        skip().setId(null);
      }
    });
  }

  public void shouldSkip() {
    Foo foo1 = new Foo("foo1", Collections.singletonList(new Bar("bar1", "bar1")));
    Foo foo2 = new Foo("foo2", Collections.singletonList(new Bar("bar2", "bar2")));

    modelMapper.map(foo1, foo2);

    assertEquals(foo1.getId(), "foo1");
    assertEquals(foo2.getId(), "foo2");

    assertEquals(foo1.getBars().get(0).getId(), "bar1");
    assertEquals(foo2.getBars().get(0).getId(), "bar2");
  }
}
