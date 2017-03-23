package org.modelmapper.functional.inherit;

import java.util.HashMap;
import java.util.Map;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

@Test
public class ValueReaderInheritanceTest extends AbstractTest {
  static class Dest {
    private String name;
    private String address;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getAddress() {
      return address;
    }

    public void setAddress(String address) {
      this.address = address;
    }
  }

  @BeforeMethod
  public void setUp() {
    modelMapper.getConfiguration().setImplicitMappingEnabled(false);
  }

  @SuppressWarnings("unchecked")
  public void shouldInclude() throws Exception {
    modelMapper.addMappings(new PropertyMap<Map<String, Object>, Dest>() {
      @Override
      protected void configure() {
        map(source("name")).setName(null);
        map(source("address")).setAddress(null);
      }
    }).include(HashMap.class, Dest.class);

    Map<String, Object> src = new HashMap<String, Object>();
    src.put("name", "foo");
    src.put("address", "bar");
    Dest dest = modelMapper.map(src, Dest.class);

    assertEquals(dest.name, "foo");
    assertEquals(dest.address, "bar");
  }

  public void shouldNotMapIfNotInclude() throws Exception {
    modelMapper.addMappings(new PropertyMap<Map<String, Object>, Dest>() {
      @Override
      protected void configure() {
        map(source("name")).setName(null);
        map(source("address")).setAddress(null);
      }
    });

    Map<String, Object> src = new HashMap<String, Object>();
    src.put("name", "foo");
    src.put("address", "bar");
    Dest dest = modelMapper.map(src, Dest.class);

    assertNull(dest.name);
    assertNull(dest.address);
  }
}
