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
   * Specifies that mapping for the destination property be skipped during the mapping process. See
   * the EDSL examples at {@link PropertyMap}.
   * 
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  D skip();
}
