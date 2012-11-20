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

import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.testng.annotations.Test;

@Test
public class CalendarConverterTest extends AbstractDateConverterTest {
  public CalendarConverterTest() {
    super(new CalendarConverter());
  }

  @Override
  public Object[][] destinationTypes() {
    return new Object[][] { { Calendar.class }, { XMLGregorianCalendar.class } };
  }

  public void testMatches() {
    assertMatches(new Class<?>[] { Date.class, Calendar.class, XMLGregorianCalendar.class,
        Long.class, Long.TYPE }, new Class<?>[] { Calendar.class, XMLGregorianCalendar.class });
  }
}
