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

import org.modelmapper.MappingException;
import org.modelmapper.spi.ConditionalConverter.MatchResult;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Adapted from the BeanUtils test suite.
 */
@Test
public class DateConverterTest extends AbstractConverterTest {
  public DateConverterTest() {
    super(new DateConverter());
  }

  @DataProvider(name = "typesProvider")
  public Object[][] provideTypes() {
    return new Object[][] { { Date.class }, { Calendar.class }, { java.sql.Date.class },
        { Time.class }, { Timestamp.class } };
  }

  @Test(dataProvider = "typesProvider")
  public void shouldConvertDate1(Class<?> type) {
    long now = System.currentTimeMillis();
    Object[] date = { new Date(now), new java.util.GregorianCalendar(), new java.sql.Date(now),
        new java.sql.Time(now), new java.sql.Timestamp(now) };
    ((GregorianCalendar) date[1]).setTime(new Date(now));

    for (int i = 0; i < date.length; i++) {
      Object val = convert(date[i], type);
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

  public void shouldConvertStringToSqlDate() {
    String testString = "2006-05-16";
    Object expected = toSqlDate(toCalendar(testString, "yyyy-MM-dd"));

    assertValid(testString, java.sql.Date.class, expected);
    assertInvalid("01/01/2006", java.sql.Date.class);
  }

  public void shouldConvertStringToTime() {
    String testString = "15:36:21";
    Time expected = toTime(toCalendar(testString, "HH:mm:ss"));

    assertValid(testString, Time.class, expected);
    assertInvalid("15:36", Time.class);
  }

  public void shouldConvertStringToTimestamp() {
    String testString = "2006-10-23 15:36:01.0";
    Object expected = toTimestamp(toCalendar(testString, "yyyy-MM-dd HH:mm:ss.S"));

    assertValid(testString, Timestamp.class, expected);
    assertInvalid("2006/09/21 15:36:01.0", Timestamp.class);
    assertInvalid("2006-10-22", Timestamp.class);
    assertInvalid("15:36:01", Timestamp.class);
  }

  public void shouldConvertToSqlDate() {
    String testString = "2006-05-16";
    java.sql.Date expected = toSqlDate(toCalendar(testString, "yyyy-MM-dd"));

    assertValid(testString, java.sql.Date.class, expected);
    assertInvalid("01/01/2006", java.sql.Date.class);
  }

  @Test(dataProvider = "typesProvider")
  public void shouldThrowOnInvalidSourceString(Class<?> type) {
    assertInvalid(null, type);
    assertInvalid("", type);
    assertInvalid("2006-10-2X", type);
    assertInvalid("2006/10/01", type);
    assertInvalid("02/10/2006", type);
    assertInvalid("02/10/06", type);
    assertInvalid(new Integer(2), type);
  }

  @Test(expectedExceptions = MappingException.class)
  public void shouldThrowOnInvalidType() {
    convert(new Date(), Character.class);
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void shouldThrowOnNull() {
    convert(null);
  }

  public void testMatches() {
    Class<?>[] sourceTypes = { Date.class, Calendar.class, java.sql.Date.class, Time.class,
        Timestamp.class, Long.class, Long.TYPE, String.class };
    Class<?>[] destinationTypes = { Date.class, Calendar.class, java.sql.Date.class, Time.class,
        Timestamp.class };

    for (Class<?> sourceType : sourceTypes)
      for (Class<?> destinationType : destinationTypes)
        assertEquals(converter.match(sourceType, destinationType), MatchResult.FULL);

    // Negative
    assertEquals(converter.match(Object[].class, Date.class), MatchResult.NONE);
    assertEquals(converter.match(Number.class, Date.class), MatchResult.NONE);
  }

  private long getTimeInMillis(Object date) {
    if (date instanceof Timestamp)
      return ((Timestamp) date).getTime();
    if (date instanceof Calendar)
      return ((Calendar) date).getTime().getTime();
    else
      return ((Date) date).getTime();
  }

  private Calendar toCalendar(String value, String pattern) {
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

  private Date toDate(Calendar calendar) {
    return calendar.getTime();
  }

  private java.sql.Date toSqlDate(Calendar value) {
    return new java.sql.Date(getTimeInMillis(value));
  }

  private Time toTime(Calendar value) {
    return new Time(getTimeInMillis(value));
  }

  private Timestamp toTimestamp(Calendar calendar) {
    return new Timestamp(getTimeInMillis(calendar));
  }

  private Object toType(Calendar calendar, Class<?> destinationType) {
    if (destinationType == Date.class)
      return toDate(calendar);
    if (destinationType == Calendar.class)
      return calendar;
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
