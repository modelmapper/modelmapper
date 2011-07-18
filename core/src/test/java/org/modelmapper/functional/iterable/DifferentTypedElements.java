package org.modelmapper.functional.iterable;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.AbstractConverter;
import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * Maps collections with elements whose types vary.
 * 
 * Taken from http://stackoverflow.com/questions/1916786/
 * 
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class DifferentTypedElements extends AbstractTest {
  static class Foo {
    String id;
    NameGroup nameGroup;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public NameGroup getNameGroup() {
      return nameGroup;
    }

    public void setNameGroup(NameGroup nameGroup) {
      this.nameGroup = nameGroup;
    }
  }

  static class NameGroup {
    private List<Name> names;

    public List<Name> getNames() {
      return names;
    }

    public void setNames(List<Name> names) {
      this.names = names;
    }
  }

  static class Name {
    private String nameValue;

    public String getNameValue() {
      return nameValue;
    }

    public void setNameValue(String nameValue) {
      this.nameValue = nameValue;
    }
  }

  static class Bar {
    private String barId;
    private BarNames barNames;

    public String getBarId() {
      return barId;
    }

    public void setBarId(String barId) {
      this.barId = barId;
    }

    public BarNames getBarNames() {
      return barNames;
    }

    public void setBarNames(BarNames barNames) {
      this.barNames = barNames;
    }
  }

  static class BarNames {
    private List<String> names;

    public List<String> getNames() {
      return names;
    }

    public void setNames(List<String> names) {
      this.names = names;
    }
  }

  public void shouldMapFooToBar() {
    Foo foo = new Foo();
    foo.id = "123";
    foo.nameGroup = new NameGroup();
    foo.nameGroup.names = new ArrayList<Name>();
    Name name1 = new Name();
    name1.nameValue = "1";
    Name name2 = new Name();
    name2.nameValue = "2";
    Name name3 = new Name();
    name3.nameValue = "3";
    foo.nameGroup.names.add(name1);
    foo.nameGroup.names.add(name2);
    foo.nameGroup.names.add(name3);

    modelMapper.createTypeMap(Name.class, String.class).setConverter(
        new AbstractConverter<Name, String>() {
          protected String convert(Name source) {
            return source.nameValue;
          }
        });

    modelMapper.addMappings(new PropertyMap<Foo, Bar>() {
      protected void configure() {
        map().setBarId(source.getId());
        map(source.getNameGroup().getNames()).getBarNames().setNames(null);
      }
    });

    Bar bar = modelMapper.map(foo, Bar.class);
    assertEquals(bar.barId, "123");
    // assertEquals(bar.getBarNames().getNames().get(0), "1");
    // assertEquals(bar.getBarNames().getNames().get(1), "2");
    // assertEquals(bar.getBarNames().getNames().get(2), "3");
  }
}
