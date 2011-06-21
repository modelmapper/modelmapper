package org.modelmapper;

import java.io.Serializable;

import org.modelmapper.internal.util.Assert;
import org.modelmapper.spi.MappingContext;

/**
 * {@link Condition} utilities and implementations. This class can be extended by a PropertyMap to
 * provide convenient access to methods.
 * 
 * @author Jonathan Halterman
 */
public class Conditions {
  private static class AndCondition extends AbstractCondition implements Serializable {
    private static final long serialVersionUID = 0;
    private final Condition a;
    private final Condition b;

    AndCondition(Condition a, Condition b) {
      this.a = a;
      this.b = b;
    }

    @Override
    public boolean applies(MappingContext<?, ?> context) {
      return a.applies(context) && b.applies(context);
    }

    @Override
    public boolean equals(Object other) {
      return other instanceof AndCondition && ((AndCondition) other).a.equals(a)
          && ((AndCondition) other).b.equals(b);
    }

    @Override
    public int hashCode() {
      return 41 * (a.hashCode() ^ b.hashCode());
    }

    @Override
    public String toString() {
      return String.format("and(%s, %s)", a, b);
    }
  }

  private static class Not extends AbstractCondition implements Serializable {
    private static final long serialVersionUID = 0;
    private final Condition delegate;

    private Not(Condition delegate) {
      this.delegate = Assert.notNull(delegate, "delegate");
    }

    public boolean applies(MappingContext<?, ?> context) {
      return !delegate.applies(context);
    }

    @Override
    public boolean equals(Object other) {
      return other instanceof Not && ((Not) other).delegate.equals(delegate);
    }

    @Override
    public int hashCode() {
      return -delegate.hashCode();
    }

    @Override
    public String toString() {
      return "not(" + delegate + ")";
    }
  }

  private static class OrCondition extends AbstractCondition implements Serializable {
    private static final long serialVersionUID = 0;
    private final Condition a;
    private final Condition b;

    OrCondition(Condition a, Condition b) {
      this.a = a;
      this.b = b;
    }

    @Override
    public boolean applies(MappingContext<?, ?> context) {
      return a.applies(context) || b.applies(context);
    }

    @Override
    public boolean equals(Object other) {
      return other instanceof OrCondition && ((OrCondition) other).a.equals(a)
          && ((OrCondition) other).b.equals(b);
    }

    @Override
    public int hashCode() {
      return 37 * (a.hashCode() ^ b.hashCode());
    }

    @Override
    public String toString() {
      return String.format("or(%s, %s)", a, b);
    }
  }

  /**
   * Returns a new condition that applies if {@code condition1} AND {@code condition2} apply.
   * 
   * @return new condition
   * @throws IllegalArgumentException if {@code condition1} or {@code condition2} is null
   */
  public static Condition and(Condition condition1, Condition condition2) {
    Assert.notNull(condition1, "condition1");
    Assert.notNull(condition2, "condition2");
    return new AndCondition(condition1, condition2);
  }

  /**
   * Returns a condition that applies when the mapping source is not {@code null}.
   */
  public static Condition isNotNull() {
    return new AbstractCondition() {
      private static final long serialVersionUID = 0;

      @Override
      public boolean applies(MappingContext<?, ?> context) {
        return context.getSource() != null;
      }

      @Override
      public String toString() {
        return "isNotNull()";
      }
    };
  }

  /**
   * Returns a condition that applies when the mapping source is {@code null}.
   */
  public static Condition isNull() {
    return new AbstractCondition() {
      private static final long serialVersionUID = 0;

      @Override
      public boolean applies(MappingContext<?, ?> context) {
        return context.getSource() == null;
      }

      @Override
      public String toString() {
        return "isNull()";
      }
    };
  }

  /**
   * Returns a condition that does NOT apply when the given {@code condition} applies.
   * 
   * @throws IllegalArgumentException if {@code condition} is null
   */
  public static Condition not(Condition condition) {
    Assert.notNull(condition, "condition");
    return new Not(condition);
  }

  /**
   * Returns a new condition that applies if {@code condition1} OR {@code condition2} apply.
   * 
   * @return new condition
   * @throws IllegalArgumentException if {@code condition1} or {@code condition2} is null
   */
  public static Condition or(Condition condition1, Condition condition2) {
    Assert.notNull(condition1, "condition1");
    Assert.notNull(condition2, "condition2");
    return new OrCondition(condition1, condition2);
  }
}
