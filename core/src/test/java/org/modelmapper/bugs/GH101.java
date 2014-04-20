package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractConverter;
import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * https://github.com/jhalterman/modelmapper/issues/15#issuecomment-40890308
 * https://github.com/jhalterman/modelmapper/issues/101
 */
@Test
public class GH101 extends AbstractTest {
  static class SourceObject {
    public Integer complexObject;
    public Integer id;

    public SourceObject(Integer id, Integer complexObject) {
      this.id = id;
      this.complexObject = complexObject;
    }
  }

  static class DestinationObject {
    public ComplexObject complexObject;
    public Integer id;

    public DestinationObject() {/* non-private no-argument constructor for mapping */
    };

    public DestinationObject(Integer id, ComplexObject complexObject) {
      this.id = id;
      this.complexObject = complexObject;
    }
  }

  static class ComplexObject {
    private Integer id;

    public ComplexObject(Integer id) {
      this.id = id;
    }
  }

  public void test() {
    // Add custom converter mapping on Integer
    modelMapper.addConverter(new AbstractConverter<Integer, ComplexObject>() {
      @Override
      protected ComplexObject convert(Integer source) {
        return new ComplexObject(source);
      }
    });

    // Create a source object with two integers wrapping the SAME VALUE
    SourceObject sourceObject = new SourceObject(10, 10);

    // Create a destination (no matter its params)
    DestinationObject destinationObject = new DestinationObject(30, new ComplexObject(40));

    // Map the source to the destination
    modelMapper.map(sourceObject, destinationObject);
    assertEquals(destinationObject.id, Integer.valueOf(10));
    assertEquals(destinationObject.complexObject.id, Integer.valueOf(10));
  }
}
