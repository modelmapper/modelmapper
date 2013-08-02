package org.modelmapper.functional;

import org.modelmapper.AbstractTest;
import org.modelmapper.ConfigurationException;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

@Test
public class PropertyMappingTest extends AbstractTest {
  static class Source {
  }

  static class Dest {
    void setValue(Object value) {
    }
  }

  @Test(expectedExceptions = ConfigurationException.class)
  public void shouldThrowWhenUsingNullConverter() {
    modelMapper.addMappings(new PropertyMap<Source, Dest>() {
      @Override
      protected void configure() {
        using(null).map().setValue("1234");
      }
    });
  }

  @Test(expectedExceptions = ConfigurationException.class)
  public void shouldThrowWhenUsingNullProvider() {
    modelMapper.addMappings(new PropertyMap<Source, Dest>() {
      @Override
      protected void configure() {
        with(null).map().setValue("1234");
      }
    });
  }

  @Test(expectedExceptions = ConfigurationException.class)
  public void shouldThrowWhenUsingNullCondition() {
    modelMapper.addMappings(new PropertyMap<Source, Dest>() {
      @Override
      protected void configure() {
        when(null).map().setValue("1234");
      }
    });
  }

  @Test(expectedExceptions = ConfigurationException.class)
  public void shouldThrowWhenUsingNullSource() {
    modelMapper.addMappings(new PropertyMap<Source, Dest>() {
      @Override
      protected void configure() {
        map().setValue(source(null));
      }
    });
  }
}
