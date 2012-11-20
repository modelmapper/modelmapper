/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.modelmapper.MappingException;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.ConditionalConverter.MatchResult;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Adapted from the BeanUtils test suite.
 */
public abstract class AbstractDateConverterTest extends AbstractConverterTest {
  private static DatatypeFactory dataTypeFactory;

  static {
    try {
      dataTypeFactory = DatatypeFactory.newInstance();
    } catch (DatatypeConfigurationException e) {
      fail();
    }
  }

  AbstractDateConverterTest(ConditionalConverter<?, ?> converter) {
    super(converter);
  }

  public abstract Object[][] destinationTypes();

  @DataProvider(name = "typesProvider")
  Object[][] destinationTypesProvider() {
    return destinationTypes();
  }

  @Test(dataProvider = "typesProvider")
  public void shouldConvertDate1(Class<?> type) {
    long now = System.currentTimeMillis();
    Date date = new Date(now);
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    XMLGregorianCalendar xmlCalendar = dataTypeFactory.newXMLGregorianCalendar(calendar);
    Object[] dates = { date, new java.sql.Date(now), new java.sql.Time(now),
        new java.sql.Timestamp(now), calendar, xmlCalendar, now };

    for (int i = 0; i < dates.length; i++) {
      Object val = convert(dates[i], type);
      assertNotNull(val);
      assertTrue(type.isInstance(val));
      assertEquals(now, getTimeInMillis(val));
    }
  }

  @Test(dataProvider = "typesProvider")
  public void shouldConvertDate2(Class<?> type) {
    String testString = "2006-10-29";
    Calendar calendar = toCalendar(testString, "yyyy-MM-dd");
    Object expected = toType(calendar, type);

    assertValid(toDate(calendar), type, expected);
    assertValid(calendar, type, expected);
    assertValid(toSqlDate(calendar), type, expected);
    assertValid(toTime(calendar), type, expected);
    assertValid(toTimestamp(calendar), type, expected);
  }

  @Test(expectedExceptions = MappingException.class)
  public void shouldThrowOnInvalidDestinationType() {
    convert(new Date(), Character.class);
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void shouldThrowOnNullSource() {
    convert(null);
  }

  public void testInvalidMatches() {
    // Negative
    assertEquals(converter.match(Object[].class, Date.class), MatchResult.NONE);
    assertEquals(converter.match(Number.class, Date.class), MatchResult.NONE);
  }

  protected void assertValid(Object source, Class<?> destinationType, Object expected) {
    try {
      Object result = convert(source, destinationType);
      Class<?> resultType = result == null ? null : result.getClass();
      Class<?> expectType = expected == null ? null : expected.getClass();
      assertEquals(resultType, expectType);
      assertEquals(getTimeInMillis(result), getTimeInMillis(expected));
    } catch (Exception e) {
      fail(e.toString());
    }
  }

  protected void assertMatches(Class<?>[] sourceTypes, Class<?>[] destinationTypes) {
    for (Class<?> sourceType : sourceTypes)
      for (Class<?> destinationType : destinationTypes)
        assertEquals(converter.match(sourceType, destinationType), MatchResult.FULL);
  }

  protected long getTimeInMillis(Object date) {
    if (date instanceof Timestamp)
      return ((Timestamp) date).getTime();
    if (date instanceof Calendar)
      return ((Calendar) date).getTimeInMillis();
    else if (date instanceof XMLGregorianCalendar)
      return ((XMLGregorianCalendar) date).toGregorianCalendar().getTimeInMillis();
    else
      return ((Date) date).getTime();
  }

  protected Calendar toCalendar(String value, String pattern) {
    try {
      DateFormat format = new SimpleDateFormat(pattern);
      format.setLenient(false);
      format.parse(value);
      return format.getCalendar();
    } catch (Exception e) {
      fail(e.toString());
    }

    return null;
  }

  protected Date toDate(Calendar calendar) {
    return calendar.getTime();
  }

  protected java.sql.Date toSqlDate(Calendar value) {
    return new java.sql.Date(getTimeInMillis(value));
  }

  protected Time toTime(Calendar value) {
    return new Time(getTimeInMillis(value));
  }

  protected Timestamp toTimestamp(Calendar calendar) {
    return new Timestamp(getTimeInMillis(calendar));
  }

  private Object toType(Calendar calendar, Class<?> destinationType) {
    if (destinationType == Date.class)
      return toDate(calendar);
    if (destinationType == Calendar.class)
      return calendar;
    if (destinationType == XMLGregorianCalendar.class)
      return dataTypeFactory.newXMLGregorianCalendar((GregorianCalendar) calendar);
    if (destinationType == java.sql.Date.class)
      return toSqlDate(calendar);
    if (destinationType == Time.class)
      return toTime(calendar);
    if (destinationType == Timestamp.class)
      return toTimestamp(calendar);

    fail();
    return null;
  }
}
