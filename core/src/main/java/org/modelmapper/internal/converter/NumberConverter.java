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
package org.modelmapper.internal.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import org.modelmapper.internal.Errors;
import org.modelmapper.internal.util.Primitives;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Converts:
 * 
 * <ul>
 * <li>Number</li>
 * <li>Boolean</li>
 * <li>Date</li>
 * <li>Calendar</li>
 * <li>String</li>
 * <li>Object</li>
 * </ul>
 * 
 * instances to instances of:
 * 
 * <ul>
 * <li>Byte</li>
 * <li>Short</li>
 * <li>Integer</li>
 * <li>Long</li>
 * <li>Float</li>
 * <li>Double</li>
 * <li>BigDecimal</li>
 * <li>BigInteger</li>
 * </ul>
 * 
 * @author Jonathan Halterman
 */
class NumberConverter implements ConditionalConverter<Object, Number> {
  public Number convert(MappingContext<Object, Number> context) {
    Object source = context.getSource();
    if (source == null)
      return null;

    Class<?> destinationType = Primitives.wrapperFor(context.getDestinationType());

    if (source instanceof Number)
      return numberFor((Number) source, destinationType);
    if (source instanceof Boolean)
      return numberFor(((Boolean) source).booleanValue() ? 1 : 0, destinationType);
    if (source instanceof Date && Long.class.equals(destinationType))
      return Long.valueOf(((Date) source).getTime());
    if (source instanceof Calendar && Long.class.equals(destinationType))
      return Long.valueOf(((Calendar) source).getTime().getTime());
    if (source instanceof XMLGregorianCalendar && Long.class.equals(destinationType))
      return ((XMLGregorianCalendar) source).toGregorianCalendar().getTimeInMillis();
    return numberFor(source.toString(), destinationType);
  }

  public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
    boolean destMatch = Number.class.isAssignableFrom(Primitives.wrapperFor(destinationType));
    if (destMatch) {
      return Number.class.isAssignableFrom(Primitives.wrapperFor(sourceType))
          || sourceType == Boolean.class || sourceType == Boolean.TYPE
          || sourceType == String.class || Date.class.isAssignableFrom(sourceType)
          || Calendar.class.isAssignableFrom(sourceType)
          || XMLGregorianCalendar.class.isAssignableFrom(sourceType) ? MatchResult.FULL : MatchResult.PARTIAL;
    } else
      return MatchResult.NONE;
  }

  /**
   * Creates a Number for the {@code source} and {@code destinationType}.
   */
  Number numberFor(Number source, Class<?> destinationType) {
    if (destinationType.equals(source.getClass()))
      return source;

    if (destinationType.equals(Byte.class)) {
      long longValue = source.longValue();
      if (longValue > Byte.MAX_VALUE)
        throw new Errors().errorTooLarge(source, destinationType).toMappingException();
      if (longValue < Byte.MIN_VALUE)
        throw new Errors().errorTooSmall(source, destinationType).toMappingException();
      return Byte.valueOf(source.byteValue());
    }

    if (destinationType.equals(Short.class)) {
      long longValue = source.longValue();
      if (longValue > Short.MAX_VALUE)
        throw new Errors().errorTooLarge(source, destinationType).toMappingException();
      if (longValue < Short.MIN_VALUE)
        throw new Errors().errorTooSmall(source, destinationType).toMappingException();
      return Short.valueOf(source.shortValue());
    }

    if (destinationType.equals(Integer.class)) {
      long longValue = source.longValue();
      if (longValue > Integer.MAX_VALUE)
        throw new Errors().errorTooLarge(source, destinationType).toMappingException();
      if (longValue < Integer.MIN_VALUE)
        throw new Errors().errorTooSmall(source, destinationType).toMappingException();
      return Integer.valueOf(source.intValue());
    }

    if (destinationType.equals(Long.class))
      return Long.valueOf(source.longValue());

    if (destinationType.equals(Float.class)) {
      if (source.doubleValue() > Float.MAX_VALUE)
        throw new Errors().errorTooLarge(source, destinationType).toMappingException();
      return Float.valueOf(source.floatValue());
    }

    if (destinationType.equals(Double.class))
      return Double.valueOf(source.doubleValue());

    if (destinationType.equals(BigDecimal.class)) {
      if (source instanceof Float || source instanceof Double)
        return new BigDecimal(source.toString());
      else if (source instanceof BigInteger)
        return new BigDecimal((BigInteger) source);
      else
        return BigDecimal.valueOf(source.longValue());
    }

    if (destinationType.equals(BigInteger.class)) {
      if (source instanceof BigDecimal)
        return ((BigDecimal) source).toBigInteger();
      else
        return BigInteger.valueOf(source.longValue());
    }

    throw new Errors().errorMapping(source, destinationType).toMappingException();
  }

  /**
   * Creates a Number for the {@code source} and {@code destinationType}.
   */
  Number numberFor(String source, Class<?> destinationType) {
    String sourceString = source.trim();
    if (sourceString.length() == 0)
      return null;

    try {
      if (destinationType.equals(Byte.class))
        return Byte.valueOf(source);
      if (destinationType.equals(Short.class))
        return Short.valueOf(source);
      if (destinationType.equals(Integer.class))
        return Integer.valueOf(source);
      if (destinationType.equals(Long.class))
        return Long.valueOf(source);
      if (destinationType.equals(Float.class))
        return Float.valueOf(source);
      if (destinationType.equals(Double.class))
        return Double.valueOf(source);
      if (destinationType.equals(BigDecimal.class))
        return new BigDecimal(source);
      if (destinationType.equals(BigInteger.class))
        return new BigInteger(source);
    } catch (Exception e) {
      throw new Errors().errorMapping(source, destinationType, e).toMappingException();
    }

    throw new Errors().errorMapping(source, destinationType).toMappingException();
  }
}
