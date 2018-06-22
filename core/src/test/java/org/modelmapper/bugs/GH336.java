package org.modelmapper.bugs;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

@Test
public class GH336 {
  static class Husband {
    String name;
    Wife wife;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Wife getWife() {
      return wife;
    }

    public void setWife(Wife wife) {
      this.wife = wife;
    }
  }

  static class Wife {
    String name;
    Husband husband;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Husband getHusband() {
      return husband;
    }

    public void setHusband(Husband husband) {
      this.husband = husband;
    }
  }

  private ModelMapper modelMapper;
  private PropertyMap<Husband, Husband> husbandPropertyMap = new PropertyMap<Husband, Husband>() {
    @Override
    protected void configure() {
      skip().setWife(null);
    }
  };
  private PropertyMap<Wife, Wife> wifePropertyMap = new PropertyMap<Wife, Wife>() {
    @Override
    protected void configure() {
      skip().setHusband(null);
    }
  };

  @BeforeMethod
  public void setUp() {
    modelMapper = new ModelMapper();
  }

  public void shouldMap() {
    modelMapper.emptyTypeMap(Husband.class, Husband.class)
        .addMappings(husbandPropertyMap)
        .implicitMappings();
    modelMapper.emptyTypeMap(Wife.class, Wife.class)
        .addMappings(wifePropertyMap)
        .implicitMappings();

    Husband husband = husband("Imtiaz");
    Wife wife = wife("Sarah");
    merry(husband, wife);

    Husband updatedHusband = husband("Imtiaz Shakil");
    modelMapper.map(updatedHusband, husband);
    assertSame(husband.wife, wife);
    assertEquals(husband.name, "Imtiaz Shakil");

    Wife updatedWife = wife("Sarah Shakil");
    modelMapper.map(updatedWife, wife);
    assertSame(wife.husband, husband);
    assertEquals(wife.name, "Sarah Shakil");
  }

  public void shouldNotMap() {
    modelMapper.typeMap(Husband.class, Husband.class)
        .addMappings(husbandPropertyMap);
    modelMapper.typeMap(Wife.class, Wife.class)
        .addMappings(wifePropertyMap);

    Husband husband = husband("Imtiaz");
    Wife wife = wife("Sarah");
    merry(husband, wife);

    Husband updatedHusband = husband("Imtiaz Shakil");
    modelMapper.map(updatedHusband, husband);
    assertSame(husband.wife, wife);
    assertEquals(husband.name, "Imtiaz Shakil");

    Wife updatedWife = wife("Sarah Shakil");
    modelMapper.map(updatedWife, wife);
    assertSame(wife.husband, husband);
    assertEquals(wife.name, "Sarah Shakil");
  }

  private static Husband husband(String name) {
    Husband husband = new Husband();
    husband.name = name;
    return husband;
  }

  private static Wife wife(String name) {
    Wife wife = new Wife();
    wife.name = name;
    return wife;
  }

  private static void merry(Husband husband, Wife wife) {
    husband.wife = wife;
    wife.husband = husband;
  }
}
