package org.modelmapper;

import org.modelmapper.spi.MappingContext;

/**
 * Converter support class. Allows for simpler Converter implementations.
 * 
 * @param <S> source type
 * @param <D> destination type
 * 
 * @author Jonathan Halterman
 */
public abstract class AbstractConverter<S, D> implements Converter<S, D> {
  /**
   * Delegates conversion to {@link #convert(Object)}.
   */
  @Override
  public D convert(MappingContext<S, D> context) {
    return convert(context.getSource());
  }

  /**
   * Converts {@code source} to an instance of type {@code D}.
   */
  protected abstract D convert(S source);
}
