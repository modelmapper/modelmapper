package org.modelmapper;

import org.modelmapper.internal.util.Assert;

/**
 * Condition support class.
 * 
 * @param <S> source type
 * @param <D> destination type
 * 
 * @author Jonathan Halterman
 */
public abstract class AbstractCondition<S, D> implements Condition<S, D> {
  /**
   * Returns a new condition that applies if {@code this} AND the given {@code condition} apply.
   * 
   * @return new condition
   * @throws IllegalArgumentException if {@code condition} is null
   */
  public Condition<S, D> and(Condition<S, D> condition) {
    Assert.notNull(condition, "condition");
    return Conditions.and(this, condition);
  }

  /**
   * Returns a new condition that applies if {@code this} OR the given {@code condition} apply.
   * 
   * @return new condition
   * @throws IllegalArgumentException if {@code condition} is null
   */
  public Condition<S, D> or(Condition<S, D> condition) {
    Assert.notNull(condition, "condition");
    return Conditions.or(this, condition);
  }
}
