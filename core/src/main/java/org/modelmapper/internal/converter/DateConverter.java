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

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.modelmapper.internal.Errors;
import org.modelmapper.spi.MappingContext;

/**
 * Converts:
 * 
 * <ul>
 * <li>Date</li>
 * <li>Calendar</li>
 * <li>java.sql.Timestamp</li>
 * <li>Long</li>
 * <li>String</li>
 * </ul>
 * 
 * instances to destination instances of:
 * 
 * <ul>
 * <li>java.util.Date</li>
 * <li>java.util.Calendar</li>
 * <li>java.sql.Date</li>
 * <li>java.sql.Time</li>
 * <li>java.sql.Timestamp</li>
 * </ul>
 * 
 * @author Jonathan Halterman
 */
class DateConverter extends AbstractConditionalConverter<Object, Object> {
  public Object convert(MappingContext<Object, Object> context) {
    Object source = context.getSource();
    Class<?> destinationType = context.getDestinationType();

    if (source instanceof Timestamp)
      return dateFor(((Timestamp) source).getTime(), destinationType);
    if (source instanceof Date)
      return dateFor(((Date) source).getTime(), destinationType);
    if (source instanceof Calendar)
      return dateFor(((Calendar) source).getTime().getTime(), destinationType);
    if (source instanceof Long)
      return dateFor(((Long) source).longValue(), destinationType);
    return dateFor(source.toString(), destinationType);
  }

  public boolean supports(Class<?> sourceType, Class<?> destinationType) {
    boolean validDestination = Date.class.isAssignableFrom(destinationType)
        || Calendar.class.isAssignableFrom(destinationType);
    return validDestination
        && (Date.class.isAssignableFrom(sourceType) || Calendar.class.isAssignableFrom(sourceType)
            || sourceType == Long.class || sourceType == Long.TYPE || sourceType == String.class);
  }

  Object dateFor(long source, Class<?> destinationType) {
    if (destinationType.equals(Date.class))
      return new Date(source);
    if (destinationType.equals(java.sql.Date.class))
      return new java.sql.Date(source);
    if (destinationType.equals(Time.class))
      return new Time(source);
    if (destinationType.equals(Timestamp.class))
      return new Timestamp(source);
    if (destinationType.equals(Calendar.class)) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(new Date(source));
      calendar.setLenient(false);
      return calendar;
    }

    throw new Errors().errorMapping(source, destinationType).toMappingException();
  }

  Date dateFor(String source, Class<?> destinationType) {
    String sourceString = toString().trim();
    if (sourceString.length() == 0)
      throw new Errors().errorMapping(source, destinationType).toMappingException();

    if (destinationType.equals(java.sql.Date.class)) {
      try {
        return java.sql.Date.valueOf(source);
      } catch (IllegalArgumentException e) {
        throw new Errors().addMessage(
            "String must be in JDBC format [yyyy-MM-dd] to create a java.sql.Date")
            .toMappingException();
      }
    }

    if (destinationType.equals(Time.class)) {
      try {
        return Time.valueOf(source);
      } catch (IllegalArgumentException e) {
        throw new Errors().addMessage(
            "String must be in JDBC format [HH:mm:ss] to create a java.sql.Time")
            .toMappingException();
      }
    }

    if (destinationType.equals(Timestamp.class)) {
      try {
        return Timestamp.valueOf(source);
      } catch (IllegalArgumentException e) {
        throw new Errors().addMessage(
            "String must be in JDBC format [yyyy-MM-dd HH:mm:ss.fffffffff] "
                + "to create a java.sql.Timestamp").toMappingException();
      }
    }

    throw new Errors().errorMapping(source, destinationType).toMappingException();
  }
}
