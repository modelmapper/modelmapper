package org.modelmapper.functional.provider;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.modelmapper.AbstractProvider;
import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.modelmapper.Provider;
import org.modelmapper.TypeMap;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class ProviderTest2 extends AbstractTest {
  public static class Source {
    private Inner1 value;

    {
      value = new Inner1();
      value.valueinner = "abc";
    }

    public Inner1 getValue() {
      return value;
    }
  }

  public static class Dest1 {
    private Inner1 value;

    public void setValue(Inner1 value) {
      this.value = value;
    }
  }

  public static class Dest2 {
    private Inner2 value;

    public void setValue(Inner2 value) {
      this.value = value;
    }
  }

  public static class Inner1 {
    String valueinner;
  }

  public static class Inner2 {
    String valueinner;
  }

  static class CustomProvider extends AbstractProvider<Object> {
    private Object provided;

    CustomProvider(Object provided) {
      this.provided = provided;
    }

    @Override
    protected Object get() {
      return provided;
    }
  }

  /**
   * Asserts that the provided value is used for a type and mapping takes place with that value.
   */
  public void shouldMapTypesWithProvider() {
    final Dest1 dest = new Dest1();
    TypeMap<Source, Dest1> typeMap = modelMapper.createTypeMap(Source.class, Dest1.class);
    typeMap.setProvider(new Provider<Dest1>() {
      public Dest1 get(ProvisionRequest<Dest1> request) {
        return dest;
      }
    });

    Dest1 result = modelMapper.map(new Source(), Dest1.class);
    assertTrue(dest == result);
    assertEquals(result.value.valueinner, "abc");
  }

  public void shouldMapWithGlobalProvider() {
    final Inner1 inner1 = new Inner1();
    inner1.valueinner = "def";
    final Inner2 inner2 = new Inner2();
    inner2.valueinner = "xyz";

    modelMapper.getConfiguration().setProvider(new Provider<Object>() {
      public Object get(ProvisionRequest<Object> request) {
        if (request.getRequestedType().equals(Inner1.class))
          return inner1;
        else if (request.getRequestedType().equals(Inner2.class))
          return inner2;
        return null;
      }
    });

    Dest1 result1 = modelMapper.map(new Source(), Dest1.class);
    assertTrue(result1.value == inner1);
    assertEquals(result1.value.valueinner, "def");

    Dest2 result2 = modelMapper.map(new Source(), Dest2.class);
    assertTrue(result2.value == inner2);
    assertEquals(result2.value.valueinner, "abc");
  }

  public void shouldMapWithPropertyProviderViaTypeMap() {
    Inner1 destInner = new Inner1();
    destInner.valueinner = "test";

    modelMapper.createTypeMap(Source.class, Dest1.class).setPropertyProvider(
        new CustomProvider(destInner));

    Dest1 result = modelMapper.map(new Source(), Dest1.class);
    assertTrue(result.value == destInner);
    assertEquals(result.value.valueinner, "test");
  }

  /**
   * Asserts that the provided value is used for a property and that mapping takes place with that
   * value.
   */
  public void shouldMapWithPropertyProviderViaPropertyMap() {
    final Inner1 destInner = new Inner1();

    modelMapper.addMappings(new PropertyMap<Source, Dest1>() {
      protected void configure() {
        with(new CustomProvider(destInner)).map().setValue(source.getValue());
      }
    });

    Dest1 result = modelMapper.map(new Source(), Dest1.class);
    assertTrue(result.value == destInner);
    assertEquals(result.value.valueinner, "abc");
  }

  /**
   * Asserts that the provided value is used for a property and that mapping takes place with that
   * value even when the source and dest property types are different.
   */
  public void shouldMapPropertyWithProviderWhenPropertyTypesDiffer() {
    final Inner2 destInner = new Inner2();

    modelMapper.addMappings(new PropertyMap<Source, Dest2>() {
      protected void configure() {
        with(new CustomProvider(destInner)).map(source.getValue()).setValue(null);
      }
    });

    Dest2 result = modelMapper.map(new Source(), Dest2.class);
    assertTrue(result.value == destInner);
    assertEquals(result.value.valueinner, "abc");
  }
}
