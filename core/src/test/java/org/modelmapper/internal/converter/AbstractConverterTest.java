package org.modelmapper.internal.converter;

import static org.testng.Assert.fail;

import org.modelmapper.Fixtures;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.InheritingConfiguration;
import org.modelmapper.internal.MappingContextImpl;
import org.modelmapper.internal.MappingEngineImpl;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingEngine;
import org.testng.annotations.BeforeMethod;

/**
 * @author Jonathan Halterman
 */
public abstract class AbstractConverterTest {
  protected final ConditionalConverter<Object, Object> converter;
  protected Class<Object> destinationType;
  protected ModelMapper modelMapper;
  protected InheritingConfiguration config = new InheritingConfiguration();
  protected MappingEngine engine = new MappingEngineImpl(config);

  @SuppressWarnings("unchecked")
  AbstractConverterTest(ConditionalConverter<?, ?> converter) {
    this.converter = (ConditionalConverter<Object, Object>) converter;
  }

  @BeforeMethod
  protected void init() {
    modelMapper = Fixtures.createModelMapper();
  }

  @SuppressWarnings("unchecked")
  public AbstractConverterTest(ConditionalConverter<?, ?> converter, Class<?> destinationType) {
    this.converter = (ConditionalConverter<Object, Object>) converter;
    this.destinationType = (Class<Object>) destinationType;
  }

  /**
   * Converts {@code source} from {@code source.getClass()} to {@link #destinationType} using
   * {@link #converter}. Assumes that {@link #destinationType} has been set in the constructor.
   */
  @SuppressWarnings("unchecked")
  protected Object convert(Object source) {
    return converter.convert(new MappingContextImpl<Object, Object>(source,
        (Class<Object>) source.getClass(), null, destinationType, null, null, engine));
  }

  /**
   * Converts {@code source} from {@code source.getClass()} to {@code destinationType} using
   * {@link #converter}.
   */
  @SuppressWarnings("unchecked")
  protected Object convert(Object source, Class<?> destinationType) {
    return converter.convert(new MappingContextImpl<Object, Object>(source,
        (Class<Object>) source.getClass(), null, (Class<Object>) destinationType, null, null,
        engine));
  }

  protected void assertInvalid(Object source, Class<?> destinationType) {
    try {
      convert(source, destinationType);
      fail();
    } catch (Exception expected) {
    }
  }
}
