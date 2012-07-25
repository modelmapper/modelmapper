package org.modelmapper.functional.typeresolution;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.modelmapper.AbstractTest;
import org.modelmapper.Provider;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author https://github.com/andy-m
 */
@Test(groups = "functional")
public class TypeResolutionTest1 extends AbstractTest {
  public static class Source {
    SourceInner<?> value;

    public Source(final SourceInner<?> value) {
      this.value = value;
    }
  }

  public static abstract class SourceInner<ValueType> {
    private ValueType value;

    public SourceInner(final ValueType value) {
      this.value = value;
    }

    public ValueType getValue() {
      return value;
    }

    public void setValue(final ValueType value) {
      this.value = value;
    }
  }

  public static class DateSource extends SourceInner<Date> {
    public DateSource(final Date value) {
      super(value);
    }
  }

  public static class TextSource extends SourceInner<String> {
    public TextSource(final String value) {
      super(value);
    }
  }

  public static class Dest {
    DestInner<?> value;
  }

  public static abstract class DestInner<ValueType> {
    public abstract ValueType getValue();

    public abstract void setValue(ValueType value);
  }

  public static class DateDest extends DestInner<Date> {
    private Date value;

    @Override
    public Date getValue() {
      return value;
    }

    @Override
    public void setValue(final Date value) {
      this.value = value;
    }
  }

  public static class TextDest extends DestInner<String> {
    private String value;

    @Override
    public String getValue() {
      return value;
    }

    @Override
    public void setValue(final String value) {
      this.value = value;
    }
  }

  public static class SubTypeMappingProvider implements Provider<Object> {
    private final Map<Class<?>, Class<?>> typeMappings;

    public SubTypeMappingProvider() {
      this.typeMappings = new HashMap<Class<?>, Class<?>>();
      typeMappings.put(DateSource.class, DateDest.class);
      typeMappings.put(TextSource.class, TextDest.class);
    }

    public Object get(final ProvisionRequest<Object> request) {
      Object source = request.getSource();
      Class<?> requestedClass = request.getRequestedType();
      if (source != null) {
        Class<?> sourceClass = source.getClass();
        if (typeMappings.containsKey(sourceClass)) {
          requestedClass = typeMappings.get(sourceClass);
          try {
            return requestedClass.newInstance();
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        }
      }
      return null;
    }
  }

  /**
   * DateSource ---> DateDest TextSource ---> TextDest
   */
  public void providersAreAbleToResolveDestinationTypesBasedOnActualSourceType() {
    modelMapper.getConfiguration().setProvider(new SubTypeMappingProvider());
    Source dateBasedSource = new Source(new DateSource(new Date()));
    Source textBasedSource = new Source(new TextSource("Some Value"));

    Dest dateDest = modelMapper.map(dateBasedSource, Dest.class);
    Assert.assertEquals(DateDest.class, dateDest.value.getClass());
    Assert.assertEquals(dateBasedSource.value.getValue(), dateDest.value.getValue());

    Dest textDest = modelMapper.map(textBasedSource, Dest.class);
    Assert.assertEquals(TextDest.class, textDest.value.getClass());
    Assert.assertEquals(textBasedSource.value.getValue(), textDest.value.getValue());
  }
}