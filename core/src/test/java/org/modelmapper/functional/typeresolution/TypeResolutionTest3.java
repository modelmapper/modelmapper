package org.modelmapper.functional.typeresolution;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.Provider;
import org.testng.annotations.Test;

/**
 * @author https://github.com/andy-m
 */
@Test(groups = "functional")
public class TypeResolutionTest3 extends AbstractTest {
  static class Source {
    Prop<?> someValue = new StringProp("Some String");
  }

  static abstract class Prop<T> {
    public abstract T getValue();

    public abstract void setValue(T value);
  }

  static class StringProp extends Prop<String> {
    String value;

    public StringProp(final String value) {
      this.value = value;
    }

    @Override
    public String getValue() {
      return value;
    }

    @Override
    public void setValue(final String value) {
      this.value = value;
    }
  }

  static class Dest {
    DestProp<?> someValue;

    public void setSomeValue(final DestProp<?> someValue) {
      this.someValue = someValue;
    }
  }

  static abstract class DestProp<T> {
    public abstract T getValue();

    public abstract void setValue(T value);
  }

  static class DestStringProp extends DestProp<String> {
    String value;

    @Override
    public void setValue(final String value) {
      this.value = value;
    }

    @Override
    public String getValue() {
      return value;
    }
  }

  static class DestPropProvider implements Provider<Object> {
    public Object get(ProvisionRequest<Object> request) {
      if (DestProp.class.equals(request.getRequestedType()))
        return new DestStringProp();
      return null;
    }
  }

  public void shouldAllowMappingOfNestedPropertiesInComplexGenericTypes() {
    Source source = new Source();
    modelMapper.getConfiguration().setProvider(new DestPropProvider());
    Dest d = modelMapper.map(source, Dest.class);

    assertEquals(source.someValue.getValue(), d.someValue.getValue());
  }
}