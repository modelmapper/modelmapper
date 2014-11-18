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

import org.modelmapper.internal.Errors;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;

/**
 * Converts to {@link Boolean} instances.
 * 
 * @author Jonathan Halterman
 */
class BooleanConverter implements ConditionalConverter<Object, Boolean> {
  private static final String[] TRUE_STRINGS = { "true", "yes", "y", "on", "1" };
  private static final String[] FALSE_STRINGSS = { "false", "no", "n", "off", "0" };

  public Boolean convert(MappingContext<Object, Boolean> context) {
    Object source = context.getSource();
    if (source == null)
      return null;

    String stringValue = source.toString().toLowerCase();
    if (stringValue.length() == 0)
      return null;

    for (int i = 0; i < TRUE_STRINGS.length; i++)
      if (TRUE_STRINGS[i].equals(stringValue))
        return Boolean.TRUE;

    for (int i = 0; i < FALSE_STRINGSS.length; i++)
      if (FALSE_STRINGSS[i].equals(stringValue))
        return Boolean.FALSE;

    throw new Errors().errorMapping(context.getSource(), context.getDestinationType())
        .toMappingException();
  }

  public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
    boolean destMatch = destinationType == Boolean.class || destinationType == Boolean.TYPE;
    return destMatch ? sourceType == Boolean.class || sourceType == Boolean.TYPE ? MatchResult.FULL
        : MatchResult.PARTIAL : MatchResult.NONE;
  }
}