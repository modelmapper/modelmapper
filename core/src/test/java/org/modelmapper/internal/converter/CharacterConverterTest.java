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

import org.modelmapper.internal.converter.CharacterConverter;
import org.modelmapper.spi.ConditionalConverter.MatchResult;
import org.testng.annotations.Test;

@Test
public class CharacterConverterTest extends AbstractConverterTest {
  static class Source {
    Entity value = new Entity();
  }

  static class Entity {
    char value = 'a';
  }

  static class Dest {
    char value;
  }

  public CharacterConverterTest() {
    super(new CharacterConverter(), Character.class);
  }

  public void shouldConvertToCharacter() {
    assertEquals(new Character('N'), convert(new Character('N')));
    assertEquals(new Character('F'), convert("FOO"));
    assertEquals(new Character('3'), convert(new Integer(321)));
  }

  public void shouldConvertComplexModel() {
    Dest dest = modelMapper.map(new Source(), Dest.class);
    assertEquals(dest.value, 'a');
  }

  public void testSupported() {
    assertEquals(converter.match(Character.class, Character.class), MatchResult.FULL);
    assertEquals(converter.match(String.class, Character.class), MatchResult.PARTIAL);
    assertEquals(converter.match(String.class, String.class), MatchResult.NONE);
  }
}
