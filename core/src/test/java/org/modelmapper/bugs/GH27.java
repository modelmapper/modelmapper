package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

/**
 * https://github.com/jhalterman/modelmapper/issues/27
 * 
 * Using a Converter when mapping to an existing destination object, the MappingContext's
 * destination value should be populated with the current property value before the Converter is
 * called.
 */
@Test
public class GH27 extends AbstractTest {
  static class SourceOuter {
    private SourceInner inner;

    public SourceInner getInner() {
      return inner;
    }
  }

  static class SourceInner {
    private Integer value;

    public Integer getValue() {
      return value;
    }
  }

  static class DestinationOuter {
    private DestinationInner inner;

    public DestinationInner getInner() {
      return inner;
    }

    public void setInner(DestinationInner inner) {
      this.inner = inner;
    }
  }

  static class DestinationInner {
    private Integer count;

    public Integer getCount() {
      return count;
    }

    public void setCount(Integer count) {
      this.count = count;
    }
  }

  public void test() {
    modelMapper.addMappings(new PropertyMap<SourceOuter, DestinationOuter>() {
      @Override
      protected void configure() {
        Converter<Integer, Integer> c = new Converter<Integer, Integer>() {
          public Integer convert(MappingContext<Integer, Integer> context) {
            return context.getSource() + context.getDestination();
          }
        };

        using(c).map().getInner().setCount(source.getInner().getValue());
      }
    });

    SourceOuter source = new SourceOuter();
    source.inner = new SourceInner();
    source.inner.value = 5;
    DestinationOuter destination = new DestinationOuter();
    destination.inner = new DestinationInner();
    destination.inner.count = 5;

    modelMapper.map(source, destination);
    assertEquals(destination.inner.count.intValue(), 10);
  }
}
