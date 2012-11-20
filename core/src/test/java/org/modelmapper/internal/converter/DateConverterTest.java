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

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.testng.annotations.Test;

@Test
public class DateConverterTest extends AbstractDateConverterTest {
  public DateConverterTest() {
    super(new DateConverter());
  }

  @Override
  public Object[][] destinationTypes() {
    return new Object[][] { { Date.class }, { java.sql.Date.class }, { Time.class },
        { Timestamp.class } };
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

  public void testMatches() {
    assertMatches(new Class<?>[] { Date.class, Calendar.class, XMLGregorianCalendar.class,
        java.sql.Date.class, Time.class, Timestamp.class, Long.class, Long.TYPE, String.class },
        new Class<?>[] { Date.class, java.sql.Date.class, Time.class, Timestamp.class });
  }

}
