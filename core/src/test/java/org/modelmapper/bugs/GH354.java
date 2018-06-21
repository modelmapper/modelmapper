package org.modelmapper.bugs;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.TypeSafeSourceGetter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test
public class GH354 {
  class Source {
    String value1 = "1.0";
    Sub sub = new Sub();

    public String getValue1() {
      return value1;
    }

    public void setValue1(String value1) {
      this.value1 = value1;
    }

    public Sub getSub() {
      return sub;
    }

    public void setSub(Sub sub) {
      this.sub = sub;
    }
  }

  class Sub {
    String sub1 = "2.0";
    String sub2 = "3";
  }

  class Destination {
    float numberOne;
    double numberTwo;
    int numberThree;
  }

  private ModelMapper modelMapper;

  @BeforeMethod
  public void setUp() {
    modelMapper = new ModelMapper();
    modelMapper.addMappings(new PropertyMap<Sub, Destination>() {
      @Override
      protected void configure() {
        map(source.sub1, destination.numberTwo);
        map(source.sub2, destination.numberThree);
      }
    });
    modelMapper.addMappings(new PropertyMap<Source, Destination>() {
      @Override
      protected void configure() {
        map(source.value1, destination.numberOne);
      }
    }).include(new TypeSafeSourceGetter<Source, Sub>() {
      @Override
      public Sub get(Source source) {
        return source.getSub();
      }
    }, Sub.class);
  }

  public void mapSub() { // works
    Destination destination = new Destination();
    modelMapper.map(new Sub(), destination);
    assertEquals(destination.numberTwo, 2d);
    assertEquals(destination.numberThree, 3);
  }

  public void mapSource() { // how to make this work?
    Destination destination = new Destination();
    modelMapper.map(new Source(), destination);
    assertEquals(destination.numberOne, 1f);
    assertEquals(destination.numberTwo, 2d);
    assertEquals(destination.numberThree, 3);
  }
}