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
package org.modelmapper.internal.util;

/**
 * @author Jonathan Halterman
 */
public final class Assert {
  private Assert() {
  }

  public static void isNull(Object object) {
    if (object != null)
      throw new IllegalArgumentException();
  }

  public static void isNull(Object object, String message, Object... args) {
    if (object != null)
      throw new IllegalArgumentException(String.format(message, args));
  }

  public static void isTrue(boolean expression) {
    if (!expression)
      throw new IllegalArgumentException();
  }

  public static void isTrue(boolean expression, String errorMessage, Object... args) {
    if (!expression)
      throw new IllegalArgumentException(String.format(errorMessage, args));
  }

  public static <T> T notNull(T reference) {
    if (reference == null)
      throw new IllegalArgumentException();
    return reference;
  }

  public static <T> T notNull(T reference, String parameterName) {
    if (reference == null)
      throw new IllegalArgumentException(parameterName + " cannot be null");
    return reference;
  }

  public static void state(boolean expression) {
    if (!expression)
      throw new IllegalStateException();
  }

  public static void state(boolean expression, String errorMessage, Object... args) {
    if (!expression)
      throw new IllegalStateException(String.format(errorMessage, args));
  }
}
