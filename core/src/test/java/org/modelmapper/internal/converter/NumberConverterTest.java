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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
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
public class NumberConverterTest extends AbstractConverterTest {
  public NumberConverterTest() {
    super(new NumberConverter());
  }

  @DataProvider(name = "numbersProvider")
  public Object[][] provideNumbers() {
    return new Object[][] { { new Integer(36) }, { new Short("44") }, { new Double(55) },
        { new Byte("3") }, { new Long(2345) }, { new BigDecimal(3454) },
        { BigInteger.valueOf(7773) }, { new Float(664) } };
  }

  @DataProvider(name = "typesProvider")
  public Object[][] provideTypes() {
    return new Object[][] { { Integer.class }, { Short.class }, { Double.class }, { Byte.class },
        { Long.class }, { BigDecimal.class }, { BigInteger.class }, { Float.class } };
  }

  /**
   * Test specifying an invalid type.
   */
  @Test(expectedExceptions = MappingException.class, dataProvider = "numbersProvider")
  public void shouldFailOnInvalidDestinationType(Number number) {
    convert(number, Object.class);
  }

  public void shouldConvertBigDecimals() {
    Object[] input = { "-17.2", "-1.1", "0.0", "1.1", "17.2", new Byte((byte) 7),
        new Short((short) 8), new Integer(9), new Long(10), new Float("11.1"), new Double("12.2") };

    BigDecimal[] expected = { new BigDecimal("-17.2"), new BigDecimal("-1.1"),
        new BigDecimal("0.0"), new BigDecimal("1.1"), new BigDecimal("17.2"), new BigDecimal("7"),
        new BigDecimal("8"), new BigDecimal("9"), new BigDecimal("10"), new BigDecimal("11.1"),
        new BigDecimal("12.2") };

    for (int i = 0; i < expected.length; i++) {
      assertEquals(convert(input[i], BigDecimal.class), expected[i]);
    }
  }

  public void shouldConvertBigIntegers() {
    Object[] input = { String.valueOf(Long.MIN_VALUE), "-17", "-1", "0", "1", "17",
        String.valueOf(Long.MAX_VALUE), new Byte((byte) 7), new Short((short) 8), new Integer(9),
        new Long(10), new Float(11.1), new Double(12.2) };

    BigInteger[] expected = { BigInteger.valueOf(Long.MIN_VALUE), BigInteger.valueOf(-17),
        BigInteger.valueOf(-1), BigInteger.valueOf(0), BigInteger.valueOf(1),
        BigInteger.valueOf(17), BigInteger.valueOf(Long.MAX_VALUE), BigInteger.valueOf(7),
        BigInteger.valueOf(8), BigInteger.valueOf(9), BigInteger.valueOf(10),
        BigInteger.valueOf(11), BigInteger.valueOf(12) };

    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i], convert(input[i], BigInteger.class));
    }
  }

  public void shouldConvertBytes() {
    Object[] input = { String.valueOf(Byte.MIN_VALUE), "-17", "-1", "0", "1", "17",
        String.valueOf(Byte.MAX_VALUE), new Byte((byte) 7), new Short((short) 8), new Integer(9),
        new Long(10), new Float(11.1), new Double(12.2) };

    Byte[] expected = { new Byte(Byte.MIN_VALUE), new Byte((byte) -17), new Byte((byte) -1),
        new Byte((byte) 0), new Byte((byte) 1), new Byte((byte) 17), new Byte(Byte.MAX_VALUE),
        new Byte((byte) 7), new Byte((byte) 8), new Byte((byte) 9), new Byte((byte) 10),
        new Byte((byte) 11), new Byte((byte) 12) };

    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i], convert(input[i], Byte.class));
      assertEquals(expected[i], convert(input[i], Byte.TYPE));
    }
  }

  public void shouldConvertCalendarToLong() {
    Calendar calendarValue = Calendar.getInstance();
    assertEquals(new Long(calendarValue.getTime().getTime()), convert(calendarValue, Long.class));
  }

  /**
   * Date -> Long
   */
  @Test
  public void shouldConvertDateToLong() {
    Date dateValue = new Date();
    assertEquals(new Long(dateValue.getTime()), convert(dateValue, Long.class));
  }

  @Test
  public void shouldConvertXmlGregorianCalendarToLong() throws DatatypeConfigurationException {
    XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
    assertEquals(xmlGregorianCalendar.toGregorianCalendar().getTimeInMillis(), convert(xmlGregorianCalendar, Long.class));
  }

  public void shouldConvertDoubles() {
    Object[] input = { String.valueOf(Double.MIN_VALUE), "-17.2", "-1.1", "0.0", "1.1", "17.2",
        String.valueOf(Double.MAX_VALUE), new Byte((byte) 7), new Short((short) 8), new Integer(9),
        new Long(10), new Float(11.1), new Double(12.2) };

    Double[] expected = { new Double(Double.MIN_VALUE), new Double(-17.2), new Double(-1.1),
        new Double(0.0), new Double(1.1), new Double(17.2), new Double(Double.MAX_VALUE),
        new Double(7), new Double(8), new Double(9), new Double(10), new Double(11.1),
        new Double(12.2) };

    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i].doubleValue(),
          ((Double) (convert(input[i], Double.class))).doubleValue(), 0.00001D);
      assertEquals(expected[i].doubleValue(),
          ((Double) (convert(input[i], Double.TYPE))).doubleValue(), 0.00001D);
    }
  }

  public void shouldConvertFloats() {
    Object[] input = { String.valueOf(Float.MIN_VALUE), "-17.2", "-1.1", "0.0", "1.1", "17.2",
        String.valueOf(Float.MAX_VALUE), new Byte((byte) 7), new Short((short) 8), new Integer(9),
        new Long(10), new Float(11.1), new Double(12.2), };

    Float[] expected = { new Float(Float.MIN_VALUE), new Float(-17.2), new Float(-1.1),
        new Float(0.0), new Float(1.1), new Float(17.2), new Float(Float.MAX_VALUE), new Float(7),
        new Float(8), new Float(9), new Float(10), new Float(11.1), new Float(12.2) };

    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i].floatValue(),
          ((Float) (convert(input[i], Float.class))).floatValue(), 0.00001);
      assertEquals(expected[i].floatValue(),
          ((Float) (convert(input[i], Float.TYPE))).floatValue(), 0.00001);

    }
  }

  public void shouldConvertIntegers() {
    Object[] input = { String.valueOf(Integer.MIN_VALUE), "-17", "-1", "0", "1", "17",
        String.valueOf(Integer.MAX_VALUE), new Byte((byte) 7), new Short((short) 8),
        new Integer(9), new Long(10), new Float(11.1), new Double(12.2) };

    Integer[] expected = { new Integer(Integer.MIN_VALUE), new Integer(-17), new Integer(-1),
        new Integer(0), new Integer(1), new Integer(17), new Integer(Integer.MAX_VALUE),
        new Integer(7), new Integer(8), new Integer(9), new Integer(10), new Integer(11),
        new Integer(12) };

    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i], convert(input[i], Integer.class));
      assertEquals(expected[i], convert(input[i], Integer.TYPE));

    }
  }

  public void shouldConvertLongs() {
    Object[] input = { String.valueOf(Long.MIN_VALUE), "-17", "-1", "0", "1", "17",
        String.valueOf(Long.MAX_VALUE), new Byte((byte) 7), new Short((short) 8), new Integer(9),
        new Long(10), new Float(11.1), new Double(12.2) };

    Long[] expected = { new Long(Long.MIN_VALUE), new Long(-17), new Long(-1), new Long(0),
        new Long(1), new Long(17), new Long(Long.MAX_VALUE), new Long(7), new Long(8), new Long(9),
        new Long(10), new Long(11), new Long(12) };

    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i], convert(input[i], Long.class));
      assertEquals(expected[i], convert(input[i], Long.TYPE));
    }
  }

  public void shouldConvertShorts() {
    Object[] input = { String.valueOf(Short.MIN_VALUE), "-17", "-1", "0", "1", "17",
        String.valueOf(Short.MAX_VALUE), new Byte((byte) 7), new Short((short) 8), new Integer(9),
        new Long(10), new Float(11.1), new Double(12.2) };

    Short[] expected = { new Short(Short.MIN_VALUE), new Short((short) -17), new Short((short) -1),
        new Short((short) 0), new Short((short) 1), new Short((short) 17),
        new Short(Short.MAX_VALUE), new Short((short) 7), new Short((short) 8),
        new Short((short) 9), new Short((short) 10), new Short((short) 11), new Short((short) 12) };

    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i], convert(input[i], Short.class));
      assertEquals(expected[i], convert(input[i], Short.TYPE));
    }
  }

  /**
   * Calendar -> Integer
   */
  @Test(expectedExceptions = MappingException.class)
  public void shouldThrowOnMapCalendarToInteger() {
    convert(Calendar.getInstance(), Integer.class);
  }

  /**
   * Date -> Integer
   */
  @Test(expectedExceptions = MappingException.class)
  public void shouldThrowOnMapDateToInteger() {
    convert(new Date(), Integer.class);
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void shouldThrowOnNotANumber() {
    convert("XXXX");
  }

  @Test(dataProvider = "typesProvider")
  public void testBooleanToNumber(Class<?> type) {
    assertEquals(0, ((Number) convert(Boolean.FALSE, type)).intValue());
    assertEquals(1, ((Number) convert(Boolean.TRUE, type)).intValue());
  }

  public void testInvalidByteAmount() {
    Long min = new Long(Byte.MIN_VALUE);
    Long max = new Long(Byte.MAX_VALUE);
    Long minMinusOne = new Long(min.longValue() - 1);
    Long maxPlusOne = new Long(max.longValue() + 1);

    assertEquals(new Byte(Byte.MIN_VALUE), convert(min, Byte.class));
    assertEquals(new Byte(Byte.MAX_VALUE), convert(max, Byte.class));

    try {
      assertEquals(null, convert(minMinusOne, Byte.class));
      fail();
    } catch (Exception e) {
    }

    try {
      assertEquals(null, convert(maxPlusOne, Byte.class));
      fail();
    } catch (Exception e) {
    }
  }

  public void testInvalidFloatAmount() {
    Double max = new Double(Float.MAX_VALUE);
    Double tooBig = new Double(Double.MAX_VALUE);

    assertEquals(new Float(Float.MAX_VALUE), convert(max, Float.class));

    try {
      assertEquals(null, convert(tooBig, Float.class));
      fail("More than maximum, expected ConversionException");
    } catch (Exception expected) {
    }
  }

  public void testInvalidIntegerAmount() {
    Long min = new Long(Integer.MIN_VALUE);
    Long max = new Long(Integer.MAX_VALUE);
    Long minMinusOne = new Long(min.longValue() - 1);
    Long maxPlusOne = new Long(max.longValue() + 1);

    assertEquals(new Integer(Integer.MIN_VALUE), convert(min, Integer.class));
    assertEquals(new Integer(Integer.MAX_VALUE), convert(max, Integer.class));

    try {
      assertEquals(null, convert(minMinusOne, Integer.class));
      fail("Less than minimum, expected ConversionException");
    } catch (Exception expected) {
    }

    try {
      assertEquals(null, convert(maxPlusOne, Integer.class));
      fail("More than maximum, expected ConversionException");
    } catch (Exception expected) {
    }
  }

  public void testInvalidShortAmount() {
    Long min = new Long(Short.MIN_VALUE);
    Long max = new Long(Short.MAX_VALUE);
    Long minMinusOne = new Long(min.longValue() - 1);
    Long maxPlusOne = new Long(max.longValue() + 1);

    assertEquals(new Short(Short.MIN_VALUE), convert(min, Short.class));
    assertEquals(new Short(Short.MAX_VALUE), convert(max, Short.class));

    try {
      assertEquals(null, convert(minMinusOne, Short.class));
      fail("Less than minimum, expected ConversionException");
    } catch (Exception expected) {
    }

    try {
      assertEquals(null, convert(maxPlusOne, Short.class));
      fail("More than maximum, expected ConversionException");
    } catch (Exception expected) {
    }
  }

  @Test(dataProvider = "typesProvider")
  public void testConvertNumber(Class<?> type) {
    Object[] number = { new Byte((byte) 7), new Short((short) 8), new Integer(9), new Long(10),
        new Float(11.1), new Double(12.2), new BigDecimal("17.2"), new BigInteger("33") };

    for (int i = 0; i < number.length; i++) {
      Object val = convert(number[i], type);
      assertNotNull(val);
      assertTrue(type.isInstance(val));
    }
  }

  public void testMatches() {
    Class<?>[] sourceTypes = { Byte.class, Byte.TYPE, Short.class, Short.TYPE, Integer.class,
        Integer.TYPE, Long.class, Long.TYPE, Float.class, Float.TYPE, Double.class, Double.TYPE,
        BigDecimal.class, BigInteger.class, Boolean.class, Boolean.TYPE, Date.class,
        Calendar.class, String.class, XMLGregorianCalendar.class };
    Class<?>[] destinationTypes = { Byte.class, Byte.TYPE, Short.class, Short.TYPE, Integer.class,
        Integer.TYPE, Long.class, Long.TYPE, Float.class, Float.TYPE, Double.class, Double.TYPE,
        BigDecimal.class, BigInteger.class };

    for (Class<?> sourceType : sourceTypes)
      for (Class<?> destinationType : destinationTypes)
        assertEquals(converter.match(sourceType, destinationType), MatchResult.FULL);

    // Negative
    assertEquals(converter.match(Object[].class, ArrayList.class), MatchResult.NONE);
    assertEquals(converter.match(Number.class, Boolean.class), MatchResult.NONE);
  }

  @Test(expectedExceptions = MappingException.class, dataProvider = "numbersProvider")
  public void testStringToNumber(Number number) {
    Object[][] types = provideTypes();

    for (int i = 0; i < types.length; i++) {
      Number result = (Number) convert(number.toString(), (Class<?>) types[i][0]);
      assertEquals(result.longValue(), number.longValue());
    }

    for (int i = 0; i < types.length; i++)
      convert("12x", (Class<?>) types[i][0]);
  }
}
