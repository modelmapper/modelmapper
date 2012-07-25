/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
  private static final Condition<?, ?> IS_NULL = new AbstractCondition<Object, Object>() {
    @SuppressWarnings("unused") private static final long serialVersionUID = 0;

    public boolean applies(MappingContext<Object, Object> context) {
      return context.getSource() == null;
    }

    @Override
    public String toString() {
      return "isNull()";
    }
  };

  private static final Condition<?, ?> IS_NOT_NULL = new AbstractCondition<Object, Object>() {
    @SuppressWarnings("unused") private static final long serialVersionUID = 0;

    public boolean applies(MappingContext<Object, Object> context) {
      return context.getSource() != null;
    }

    @Override
    public String toString() {
      return "isNotNull()";
    }
  };

  private static class AndCondition<S, D> extends AbstractCondition<S, D> implements Serializable {
    private static final long serialVersionUID = 0;
    private final Condition<S, D> a;
    private final Condition<S, D> b;

    AndCondition(Condition<S, D> a, Condition<S, D> b) {
      this.a = a;
      this.b = b;
    }

    public boolean applies(MappingContext<S, D> context) {
      return a.applies(context) && b.applies(context);
    }

    @Override
    public boolean equals(Object other) {
      return other instanceof AndCondition && ((AndCondition<?, ?>) other).a.equals(a)
          && ((AndCondition<?, ?>) other).b.equals(b);
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

  private static class Not<S, D> extends AbstractCondition<S, D> implements Serializable {
    private static final long serialVersionUID = 0;
    private final Condition<S, D> delegate;

    private Not(Condition<S, D> delegate) {
      this.delegate = Assert.notNull(delegate, "delegate");
    }

    public boolean applies(MappingContext<S, D> context) {
      return !delegate.applies(context);
    }

    @Override
    public boolean equals(Object other) {
      return other instanceof Not && ((Not<?, ?>) other).delegate.equals(delegate);
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

  private static class OrCondition<S, D> extends AbstractCondition<S, D> implements Serializable {
    private static final long serialVersionUID = 0;
    private final Condition<S, D> a;
    private final Condition<S, D> b;

    OrCondition(Condition<S, D> a, Condition<S, D> b) {
      this.a = a;
      this.b = b;
    }

    public boolean applies(MappingContext<S, D> context) {
      return a.applies(context) || b.applies(context);
    }

    @Override
    public boolean equals(Object other) {
      return other instanceof OrCondition && ((OrCondition<?, ?>) other).a.equals(a)
          && ((OrCondition<?, ?>) other).b.equals(b);
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
  public static <S, D> Condition<S, D> and(Condition<S, D> condition1, Condition<S, D> condition2) {
    Assert.notNull(condition1, "condition1");
    Assert.notNull(condition2, "condition2");
    return new AndCondition<S, D>(condition1, condition2);
  }

  /**
   * Returns a condition that applies when the mapping source is not {@code null}.
   */
  public static Condition<?, ?> isNotNull() {
    return IS_NOT_NULL;
  }

  /**
   * Returns a condition that applies when the mapping source is {@code null}.
   */
  public static Condition<?, ?> isNull() {
    return IS_NULL;
  }

  /**
   * Returns a condition that applies when the mapping source is of the type {@code type}.
   */
  public static Condition<?, ?> isType(final Class<?> type) {
    return new Condition<Object, Object>() {
      public boolean applies(MappingContext<Object, Object> context) {
        return type.isAssignableFrom(context.getSourceType());
      }
    };
  }

  /**
   * Returns a condition that does NOT apply when the given {@code condition} applies.
   * 
   * @throws IllegalArgumentException if {@code condition} is null
   */
  public static <S, D> Condition<S, D> not(Condition<S, D> condition) {
    Assert.notNull(condition, "condition");
    return new Not<S, D>(condition);
  }

  /**
   * Returns a new condition that applies if {@code condition1} OR {@code condition2} apply.
   * 
   * @return new condition
   * @throws IllegalArgumentException if {@code condition1} or {@code condition2} is null
   */
  public static <S, D> Condition<S, D> or(Condition<S, D> condition1, Condition<S, D> condition2) {
    Assert.notNull(condition1, "condition1");
    Assert.notNull(condition2, "condition2");
    return new OrCondition<S, D>(condition1, condition2);
  }
}
