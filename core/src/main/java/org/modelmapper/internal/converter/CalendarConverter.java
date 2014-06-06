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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.modelmapper.internal.Errors;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;

/**
 * Converts:
 * 
 * <ul>
 * <li>Date (and subclasses)</li>
 * <li>Calendar (and subclasses)</li>
 * <li>XMLGregorianCalendar</li>
 * <li>Long</li>
 * </ul>
 * 
 * instances to destination instances of:
 * 
 * <ul>
 * <li>java.util.Calendar</li>
 * <li>javax.xml.datatype.XMLGregorianCalendar</li>
 * </ul>
 * 
 * @author Jonathan Halterman
 */
class CalendarConverter implements ConditionalConverter<Object, Object> {
  private static DatatypeFactory dataTypeFactory;

  private static DatatypeFactory getDataTypeFactory() {
    if (dataTypeFactory == null) {
      try {
        dataTypeFactory = DatatypeFactory.newInstance();
      } catch (DatatypeConfigurationException e) {
        throw new Errors().addMessage(e,
            "Failed to create DataTypeFactory required for XMLGregorianCalendar conversion")
            .toMappingException();
      }
    }

    return dataTypeFactory;
  }

  public Object convert(MappingContext<Object, Object> context) {
    Object source = context.getSource();
    if (source == null)
      return null;

    Class<?> destinationType = context.getDestinationType();
    if (!Calendar.class.isAssignableFrom(destinationType)
        && !destinationType.equals(XMLGregorianCalendar.class))
      throw new Errors().errorMapping(source, destinationType).toMappingException();

    GregorianCalendar calendar = new GregorianCalendar();

    if (source instanceof Date)
      calendar.setTimeInMillis(((Date) source).getTime());
    else if (source instanceof Calendar) {
      Calendar cal = (Calendar) source;
      calendar.setTimeZone(cal.getTimeZone());
      calendar.setTimeInMillis(cal.getTime().getTime());
    } else if (source instanceof XMLGregorianCalendar) {
      XMLGregorianCalendar xmlCal = (XMLGregorianCalendar) source;
      GregorianCalendar cal = xmlCal.toGregorianCalendar();
      calendar.setTimeZone(cal.getTimeZone());
      calendar.setTimeInMillis(cal.getTime().getTime());
    } else if (source instanceof Long)
      calendar.setTimeInMillis(((Long) source).longValue());

    return destinationType.equals(XMLGregorianCalendar.class) ? getDataTypeFactory().newXMLGregorianCalendar(
        calendar)
        : calendar;
  }

  public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
    return (Calendar.class.isAssignableFrom(destinationType) || destinationType == XMLGregorianCalendar.class)
        && (Date.class.isAssignableFrom(sourceType) || Calendar.class.isAssignableFrom(sourceType)
            || sourceType == XMLGregorianCalendar.class || sourceType == Long.class || sourceType == Long.TYPE) ? MatchResult.FULL
        : MatchResult.NONE;
  }
}
