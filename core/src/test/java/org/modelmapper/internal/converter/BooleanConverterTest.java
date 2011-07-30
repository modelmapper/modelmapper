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

import org.modelmapper.MappingException;
import org.modelmapper.internal.converter.BooleanConverter;
import org.modelmapper.spi.ConditionalConverter.MatchResult;
import org.testng.annotations.Test;

@Test
public class BooleanConverterTest extends AbstractConverterTest {
  public static final String[] STANDARD_TRUES = new String[] { "yes", "y", "true", "on", "1" };
  public static final String[] STANDARD_FALSES = new String[] { "no", "n", "false", "off", "0" };

  public BooleanConverterTest() {
    super(new BooleanConverter(), Boolean.class);
  }

  public void testStandardValues() {
    testConversionValues(STANDARD_TRUES, STANDARD_FALSES);
  }

  public void testCaseInsensitivity() {
    testConversionValues(new String[] { "Yes", "TRUE" }, new String[] { "NO", "fAlSe" });
  }

  @Test(expectedExceptions = MappingException.class)
  public void shouldThrowOnInvalidString() {
    convert("abc");
  }

  protected void testConversionValues(String[] trueValues, String[] falseValues) {
    for (int i = 0; i < trueValues.length; i++)
      assertEquals(Boolean.TRUE, convert(trueValues[i]));
    for (int i = 0; i < falseValues.length; i++)
      assertEquals(Boolean.FALSE, convert(falseValues[i]));
  }

  public void testSupported() {
    assertEquals(converter.match(Boolean.class, Boolean.class), MatchResult.FULL);
    assertEquals(converter.match(String.class, Boolean.class), MatchResult.PARTIAL);
    assertEquals(converter.match(String.class, String.class), MatchResult.NONE);
  }
}
