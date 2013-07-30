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
package org.modelmapper.convention;

import java.util.regex.Pattern;

import org.modelmapper.spi.NameTokenizer;
import org.modelmapper.spi.NameableType;

/**
 * {@link NameTokenizer} implementations.
 * 
 * @author Jonathan Halterman
 */
public class NameTokenizers {
  /**
   * Tokenizes class and property names according to the CamelCase naming convention.
   */
  public static final NameTokenizer CAMEL_CASE = new CamelCaseNameTokenizer();

  /**
   * Tokenizes class and property names according to the underscore naming convention.
   */
  public static final NameTokenizer UNDERSCORE = new UnderscoreNameTokenizer();

  private static class CamelCaseNameTokenizer implements NameTokenizer {
    private static final Pattern camelCase = Pattern.compile("(?<=[A-Z])(?=[A-Z][a-z])|(?<=[^A-Z])(?=[A-Z])|(?<=[A-Za-z])(?=[^A-Za-z])");

    public String[] tokenize(String name, NameableType nameableType) {
      return camelCase.split(name);
    }

    @Override
    public String toString() {
      return "Camel Case";
    }
  }

  private static class UnderscoreNameTokenizer implements NameTokenizer {
    private static final Pattern underscore = Pattern.compile("_");

    public String[] tokenize(String name, NameableType nameableType) {
      return underscore.split(name);
    }

    @Override
    public String toString() {
      return "Underscore";
    }
  }
}
