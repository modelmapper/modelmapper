package org.modelmapper.functional.converter;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractConverter;
import org.modelmapper.AbstractTest;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Adapted from the Automapper test suite.
 */
public class CustomConversion {
  @Test(groups = "functional")
  public static class When_using_member_converter extends AbstractTest {
    private ModelDTO result;

    public static class ModelObject {
      int value1 = 42;
      int value2 = 42;

      public int getValue2() {
        return value2;
      }
    }

    public static class ModelDTO {
      int value1;
      int value2;

      public void setValue2(int value2) {
        this.value2 = value2;
      }
    }

    Converter<Integer, Integer> converter1 = new AbstractConverter<Integer, Integer>() {
      public Integer convert(Integer source) {
        return source + 1;
      }
    };

    @Override
    @BeforeMethod
    protected void initContext() {
      super.initContext();
      modelMapper.addMappings(new PropertyMap<ModelObject, ModelDTO>() {
        protected void configure() {
          using(converter1).map().setValue2(source.getValue2());
        }
      });

      result = modelMapper.map(new ModelObject(), ModelDTO.class);
    }

    public void shouldMapWithImplicitMappings() {
      assertEquals(result.value1, 42);
    }

    public void shouldMapWithExplicitMappings() {
      assertEquals(result.value2, 43);
    }
  }

  @Test(groups = "functional")
  public static class When_using_member_converter_for_deep_model extends AbstractTest {
    private ModelDTO result;

    public static class ModelObject {
      ModelSubObject sub = new ModelSubObject();

      public ModelSubObject getSub() {
        return sub;
      }
    }

    public static class ModelSubObject {
      int someValue = 45;
    }

    public static class ModelDTO {
      int someValue;

      public void setSomeValue(int someValue) {
        this.someValue = someValue;
      }
    }

    public static class CustomConverter extends AbstractConverter<ModelSubObject, Integer> {
      public Integer convert(ModelSubObject source) {
        return source.someValue + 1;
      }
    }

    @Override
    @BeforeMethod
    protected void initContext() {
      super.initContext();
      modelMapper.addMappings(new PropertyMap<ModelObject, ModelDTO>() {
        protected void configure() {
          using(new CustomConverter()).map(source.getSub()).setSomeValue(0);
        }
      });
    }

    public void shouldMap() {
      result = modelMapper.map(new ModelObject(), ModelDTO.class);
      assertEquals(result.someValue, 46);
    }
  }

  @Test(groups = "functional")
  public static class When_using_member_converter_for_different_member extends
      AbstractTest {
    public static class Source {
      int someValue = 36;
      int someOtherValue = 53;

      public int getSomeOtherValue() {
        return someOtherValue;
      }
    }

    public static class Dest {
      int someValue;

      public void setSomeValue(int someValue) {
        this.someValue = someValue;
      }
    }

    Converter<Integer, Integer> customConverter1 = new AbstractConverter<Integer, Integer>() {
      public Integer convert(Integer source) {
        return source + 5;
      }
    };

    Converter<Source, Integer> customConverter2 = new AbstractConverter<Source, Integer>() {
      public Integer convert(Source source) {
        return source.someOtherValue + 5;
      }
    };

    public void shouldMapFromSourceMember() {
      modelMapper.addMappings(new PropertyMap<Source, Dest>() {
        protected void configure() {
          using(customConverter1).map().setSomeValue(source.getSomeOtherValue());
        }
      });

      Dest result = modelMapper.map(new Source(), Dest.class);
      assertEquals(result.someValue, 58);
    }

    public void shouldMapFromSourceObject() {
      modelMapper.addMappings(new PropertyMap<Source, Dest>() {
        protected void configure() {
          using(customConverter2).map(source).setSomeValue(33);
        }
      });

      Dest result = modelMapper.map(new Source(), Dest.class);
      assertEquals(result.someValue, 58);
    }
  }

  @Test(groups = "functional")
  public static class When_using_converter extends AbstractTest {
    protected Source source = new Source();
    protected Destination dest;

    public static class Source {
      int value = 10;
      int anotherValue = 20;

      public int getValue() {
        return value;
      }

      public int getAnotherValue() {
        return anotherValue;
      }
    }

    public static class Destination {
      public int value = 3;

      Destination() {
      }

      public void setValue(int value) {
        this.value = value;
      }

      Destination(int value) {
        this.value = value;
      }
    }

    Converter<Source, Destination> customConverter = new Converter<Source, Destination>() {
      public Destination convert(MappingContext<Source, Destination> context) {
        context.getDestination().value = context.getSource().value + 10;
        return context.getDestination();
      }
    };

    public void shouldUseConverter() {
      modelMapper.createTypeMap(Source.class, Destination.class).setConverter(customConverter);
      dest = modelMapper.map(source, Destination.class);
      assertEquals(dest.value, 20);
    }

    public void shouldSkipUnusedSourceMembers() {
      modelMapper.addMappings(new PropertyMap<Source, Destination>() {
        protected void configure() {
          map().setValue(source.getAnotherValue());
        }
      }).setConverter(customConverter);

      dest = modelMapper.map(source, Destination.class);
      assertEquals(dest.value, 20);
    }
  }

  @Test(groups = "functional")
  public static class When_using_converter_for_destination_object extends When_using_converter {
    Converter<Integer, Integer> customMemberConverter = new AbstractConverter<Integer, Integer>() {
      protected Integer convert(Integer value) {
        return value + 10;
      }
    };

    public void shouldMapUsingMemberConverter() {
      modelMapper.addMappings(new PropertyMap<Source, Destination>() {
        protected void configure() {
          using(customMemberConverter).map().setValue(source.getValue());
        }
      });

      Destination dest = new Destination();
      modelMapper.map(source, dest);
      assertEquals(dest.value, 20);
    }

    public void shouldMapUsingConverter() {
      modelMapper.addMappings(new PropertyMap<Source, Destination>() {
        protected void configure() {
          map().setValue(source.getAnotherValue());
        }
      }).setConverter(customConverter);

      Destination dest = new Destination();
      modelMapper.map(source, dest);
      assertEquals(dest.value, 20);
    }
  }

  @Test(groups = "functional")
  public static class When_using_converter_for_mismatched_properties extends AbstractTest {
    public static class Source {
      int value1 = 10;
      int anotherValue = 15;
    }

    public static class Destination {
      private int value2;
    }

    Converter<Source, Destination> customConverter = new AbstractConverter<Source, Destination>() {
      protected Destination convert(Source source) {
        Destination d = new Destination();
        d.value2 = source.value1 + 10;
        return d;
      }
    };

    @Override
    @BeforeMethod
    protected void initContext() {
      super.initContext();
      modelMapper.createTypeMap(Source.class, Destination.class).setConverter(customConverter);
    }

    public void shouldValidate() {
      modelMapper.validate();
    }

    public void shouldMap() {
      Destination d = modelMapper.map(new Source(), Destination.class);
      assertEquals(d.value2, 20);
    }
  }

  @Test(groups = "functional")
  public static class When_using_converter_for_deep_mapping extends AbstractTest {
    public static class Source {
      int value;
    }

    public static class Destination {
      SubDest dest;

      public SubDest getDest() {
        return dest;
      }

      public void setDest(SubDest dest) {
        this.dest = dest;
      }
    }

    public static class SubDest {
      int value;

      public void setValue(int value) {
        this.value = value;
      }
    }

    Converter<Integer, Integer> customConverter = new AbstractConverter<Integer, Integer>() {
      protected Integer convert(Integer source) {
        return source;
      }
    };

    public void shouldMapConvertUsingLastProperty() {
      modelMapper.addMappings(new PropertyMap<Source, Destination>() {
        protected void configure() {
          using(customConverter).map().getDest().setValue(5);
        }
      });

      Destination dest = modelMapper.map(new Source(), Destination.class);
      assertEquals(dest.dest.value, 5);
    }
  }
}
