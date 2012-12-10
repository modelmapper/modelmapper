package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.AbstractTest;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

/**
 * https://github.com/jhalterman/modelmapper/issues/26
 */
@Test
public class GH26 extends AbstractTest {
  public static class Configuration {
    List<Location> locations;

    public List<Location> getLocations() {
      return locations;
    }
  }

  public static class Location {
    Long id;
    String name;

    public Location(long id, String name) {
      this.id = id;
      this.name = name;
    }
  }

  public static class ConfigurationDTO {
    List<Long> locationIds;
    List<String> locationNames;

    public void setLocationIds(List<Long> locationIds) {
      this.locationIds = locationIds;
    }

    public void setLocationNames(List<String> locationNames) {
      this.locationNames = locationNames;
    }
  }

  public void test() {
    Configuration source = new Configuration();
    source.locations = new ArrayList<Location>();
    source.locations.add(new Location(1, "test1"));
    source.locations.add(new Location(2, "test2"));
    source.locations.add(new Location(3, "test3"));

    modelMapper.addConverter(new Converter<Location, Long>() {
      public Long convert(MappingContext<Location, Long> context) {
        return context.getSource().id;
      }
    });

    modelMapper.addConverter(new Converter<Location, String>() {
      public String convert(MappingContext<Location, String> context) {
        return context.getSource().name;
      }
    });

    modelMapper.addMappings(new PropertyMap<Configuration, ConfigurationDTO>() {
      @Override
      protected void configure() {
        map(source.getLocations()).setLocationIds(null);
        map(source.getLocations()).setLocationNames(null);
      }
    });

    ConfigurationDTO dto = modelMapper.map(source, ConfigurationDTO.class);
    assertEquals(dto.locationIds.get(0), Long.valueOf(1));
    assertEquals(dto.locationIds.get(1), Long.valueOf(2));
    assertEquals(dto.locationIds.get(2), Long.valueOf(3));
    assertEquals(dto.locationNames.get(0), "test1");
    assertEquals(dto.locationNames.get(1), "test2");
    assertEquals(dto.locationNames.get(2), "test3");
  }
}
