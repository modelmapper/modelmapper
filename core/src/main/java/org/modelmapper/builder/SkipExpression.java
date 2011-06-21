package org.modelmapper.builder;

import org.modelmapper.PropertyMap;

/**
 * Expresses a mapping that is skipped.
 * 
 * @param <D> destination type
 * 
 * @author Jonathan Halterman
 */
public interface SkipExpression<D> extends MapExpression<D> {
  /**
   * Defines a mapping to a destination to be skipped during the mapping process. See the EDSL
   * examples at {@link PropertyMap}.
   * 
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  D skip();
}
