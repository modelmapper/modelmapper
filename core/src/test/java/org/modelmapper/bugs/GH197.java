package org.modelmapper.bugs;

import org.modelmapper.AbstractTest;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * https://github.com/jhalterman/modelmapper/issues/197
 */
@Test
public class GH197 extends AbstractTest {
  public void shouldNotSkipWhenConditionNotMatch() {
    Source source = new Source();
    source.setId(15);
    source.setName("Blablabla");


    final Condition<Source, Destination> condition = new Condition<Source, Destination>() {
      public boolean applies(MappingContext<Source, Destination> context) {
        return false;
      }
    };

    ModelMapper modelMapper = new ModelMapper();
    modelMapper.addMappings(new PropertyMap<Source, Destination>() {
      protected void configure() {
        when(condition).skip().setName(source.getName());
      }
    });

    Destination result = modelMapper.map(source, Destination.class);
    assertEquals((int) result.getId(), 15);
    assertEquals(result.getName(), "Blablabla");
  }

  public void shouldSkipWhenConditionMatch() {
    Source source = new Source();
    source.setId(15);
    source.setName("Blablabla");


    final Condition<Source, Destination> condition = new Condition<Source, Destination>() {
      public boolean applies(MappingContext<Source, Destination> context) {
        return true;
      }
    };

    ModelMapper modelMapper = new ModelMapper();
    modelMapper.addMappings(new PropertyMap<Source, Destination>() {
      protected void configure() {
        when(condition).skip().setName(source.getName());
      }
    });

    Destination result = modelMapper.map(source, Destination.class);
    assertEquals((int) result.getId(), 15);
    assertNull(result.getName());
  }

  static class Destination {
    private Integer id;
    private String name;

    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  static class Source {
    private int id;
    private String name;

    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }
}
