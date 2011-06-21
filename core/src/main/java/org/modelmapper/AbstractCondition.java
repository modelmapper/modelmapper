package org.modelmapper;

import org.modelmapper.internal.util.Assert;

/**
 * Condition support class.
 * 
 * @author Jonathan Halterman
 */
public abstract class AbstractCondition implements Condition {
  /**
   * Returns a new condition that applies if {@code this} AND the given {@code condition} apply.
   * 
   * @return new condition
   * @throws IllegalArgumentException if {@code condition} is null
   */
  public Condition and(Condition condition) {
    Assert.notNull(condition, "condition");
    return Conditions.and(this, condition);
  }

  /**
   * Returns a new condition that applies if {@code this} OR the given {@code condition} apply.
   * 
   * @return new condition
   * @throws IllegalArgumentException if {@code condition} is null
   */
  public Condition or(Condition condition) {
    Assert.notNull(condition, "condition");
    return Conditions.or(this, condition);
  }
}
