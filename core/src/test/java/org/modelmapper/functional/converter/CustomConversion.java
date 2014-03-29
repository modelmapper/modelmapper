package org.modelmapper.functional.converter;

import org.modelmapper.*;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

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

  @Test(groups = "functionl")
  public static class When_using_converter_for_mapping_to_one_destination_from_many_sources extends AbstractTest {
      public static class Source {
          private Source1 source1;
          private Source2 source2;

          public Source1 getSource1() {
              return source1;
          }

          public void setSource1(Source1 source1) {
              this.source1 = source1;
          }

          public Source2 getSource2() {
              return source2;
          }

          public void setSource2(Source2 source2) {
              this.source2 = source2;
          }
      }

      public static class Source1 {
          private int value;

          public int getValue() {
              return value;
          }

          public void setValue(int value) {
              this.value = value;
          }
      }

      public static class Source2 {
          private int value;

          public int getValue() {
              return value;
          }

          public void setValue(int value) {
              this.value = value;
          }
      }

      public static class Destination {
          private SubDest dest1 = new SubDest();
          private SubDest dest2 = new SubDest();

          public SubDest getDest1() {
              return dest1;
          }

          public void setDest1(SubDest dest1) {
              this.dest1 = dest1;
          }

          public SubDest getDest2() {
              return dest2;
          }

          public void setDest2(SubDest dest2) {
              this.dest2 = dest2;
          }
      }

      public static class SubDest {
          private int squared;

          public int getSquared() {
              return squared;
          }

          public void setSquared(int squared) {
              this.squared = squared;
          }
      }

      public void shouldMapSubDestValueWithConverter() {
          final ModelMapper modelMapper = new ModelMapper();

          modelMapper.addMappings(new PropertyMap<Source, Destination>() {
              @Override
              protected void configure() {
                  map(source.getSource1()).setDest1(null);
                  map(source.getSource2()).setDest2(null);
              }
          });

          modelMapper.addMappings(new PropertyMap<Source1, SubDest>() {
              @Override
              protected void configure() {
                using(new AbstractConverter<Source1, Integer>() {
                    @Override
                    protected Integer convert(Source1 source) {
                        return source.value * source.value;
                    }
                })
                .map(source).setSquared(0);
              }
          });

          modelMapper.addMappings(new PropertyMap<Source2, SubDest>() {

              @Override
              protected void configure() {
                  using(new AbstractConverter<Source2, Integer>() {
                      @Override
                      protected Integer convert(Source2 source) {
                          return source.value * source.value;
                      }
                  })
                  .map(source).setSquared(0);
              }
          });

          final Source source = new Source();

          source.source1 = new Source1();
          source.source1.value = 2;

          source.source2 = new Source2();
          source.source2.value = 3;

          Destination dest = modelMapper.map(source, Destination.class);

          assertEquals(dest.dest1.squared, 4);
          assertEquals(dest.dest2.squared, 9);
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
