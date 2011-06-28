package org.modelmapper.internal.converter;

import org.modelmapper.spi.ConditionalConverter;

/**
 * A base conditional converter implementation that verifies source types.
 * 
 * @author Jonathan Halterman
 */
abstract class AbstractConditionalConverter<S, D> implements ConditionalConverter<S, D> {
  @Override
  public boolean supportsSource(Class<?> sourceType) {
    return true;
  }

  @Override
  public boolean verifiesSource() {
    return true;
  }
}
