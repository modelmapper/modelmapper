package org.modelmapper.bugs;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.spi.SourceGetter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test
public class GH364 {
  private ModelMapper modelMapper;

  class Source {
    String first = "1.0";
    StringContainer second = new StringContainer();
    String getString() { return first; }
    StringContainer getContainer() { return second; }
  }

  abstract class AbstractContainer<T> {
    T value;
    T getValue() { return value; }
  }

  class StringContainer extends AbstractContainer<String> { { value = "2"; } }

  class Destination {
    float numberOne;
    double numberTwo;
    void setNumberOne(float numberOne) { this.numberOne = numberOne; }
    void setNumberTwo(int numberTwo) { this.numberTwo = numberTwo; }
  }

  @BeforeMethod
  public void setUp() {
    modelMapper = new ModelMapper();
  }

  public void work() {
    modelMapper.addMappings(new PropertyMap<Source, Destination>() {
          @Override
          protected void configure() {
            map(source.getContainer().getValue(), destination.numberTwo);
          }
        });
    Destination destination = new Destination();
    modelMapper.map(new Source(), destination);
    assertEquals(destination.numberTwo, 2d);
  }

  public void shouldWork() {
    modelMapper.typeMap(Source.class, Destination.class)
        .addMapping(new SourceGetter<Source>() {
          @Override
          public Object get(Source source) {
            return source.getContainer().getValue();
          }
        }, new DestinationSetter<Destination, Integer>() {
          @Override
          public void accept(Destination destination, Integer value) {
            destination.setNumberTwo(value);
          }
        });
    Destination destination = new Destination();
    modelMapper.map(new Source(), destination);
    assertEquals(destination.numberTwo, 2d);
  }

}
