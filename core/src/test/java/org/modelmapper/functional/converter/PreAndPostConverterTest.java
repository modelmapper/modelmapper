package org.modelmapper.functional.converter;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class PreAndPostConverterTest extends AbstractTest {
  static class Source {
    String value;
  }

  static class Dest {
    String value;

    void setValue(String value) {
      this.value = value;
    }
  }
}
