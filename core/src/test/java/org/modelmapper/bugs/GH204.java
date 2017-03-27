package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

@Test
public class GH204 extends AbstractTest {
  static class SomeDto {
    private String someValue;

    public SomeDto() {
    }

    public SomeDto(String someValue) {
      this.someValue = someValue;
    }

    public String someValue() {
      return someValue;
    }

    public SomeDto someValue(String someValue) {
      this.someValue = someValue;
      return this;
    }
  }

  static class SomeEntity {
    private String otherValue;

    public SomeEntity() {
    }

    public SomeEntity(String otherValue) {
      this.otherValue = otherValue;
    }

    public String otherValue() {
      return otherValue;
    }

    public SomeEntity otherValue(String otherValue) {
      this.otherValue = otherValue;
      return this;
    }
  }

  public void shouldMappingMethod1() {
    modelMapper.addMappings(new PropertyMap<SomeDto, SomeEntity>() {
      @Override
      protected void configure() {
        map(source.someValue(), destination.otherValue(null));
      }
    });
    modelMapper.addMappings(new PropertyMap<SomeEntity, SomeDto>() {
      @Override
      protected void configure() {
        map(source.otherValue(), destination.someValue(null));
      }
    });

    modelMapper.validate();

    assertEquals("foo",
        modelMapper.map(new SomeDto("foo"), SomeEntity.class).otherValue);
    assertEquals("bar",
        modelMapper.map(new SomeEntity("bar"), SomeDto.class).someValue);
  }

  public void shouldMappingMethod2() {
    modelMapper.addMappings(new PropertyMap<SomeDto, SomeEntity>() {
      @Override
      protected void configure() {
        map().otherValue(source.someValue());
      }
    });
    modelMapper.addMappings(new PropertyMap<SomeEntity, SomeDto>() {
      @Override
      protected void configure() {
        map().someValue(source.otherValue());
      }
    });

    modelMapper.validate();

    assertEquals("foo",
        modelMapper.map(new SomeDto("foo"), SomeEntity.class).otherValue);
    assertEquals("bar",
        modelMapper.map(new SomeEntity("bar"), SomeDto.class).someValue);
  }

  public void shouldMappingMethod3() {
    modelMapper.addMappings(new PropertyMap<SomeDto, SomeEntity>() {
      @Override
      protected void configure() {
        map(source.someValue()).otherValue(null);
      }
    });
    modelMapper.addMappings(new PropertyMap<SomeEntity, SomeDto>() {
      @Override
      protected void configure() {
        map(source.otherValue()).someValue(null);
      }
    });

    modelMapper.validate();

    assertEquals("foo",
        modelMapper.map(new SomeDto("foo"), SomeEntity.class).otherValue);
    assertEquals("bar",
        modelMapper.map(new SomeEntity("bar"), SomeDto.class).someValue);
  }
}
