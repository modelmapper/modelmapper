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

import java.util.List;

import org.modelmapper.spi.MatchingStrategy;

/**
 * See {@link MatchingStrategies#STRICT}.
 * 
 * @author Jonathan Halterman
 */
final class StrictMatchingStrategy implements MatchingStrategy {
  public boolean isExact() {
    return true;
  }

  public boolean matches(PropertyNameInfo propertyNameInfo) {
    List<String[]> sourceTokens = propertyNameInfo.getSourcePropertyTokens();
    List<String[]> destTokens = propertyNameInfo.getDestinationPropertyTokens();
    if (sourceTokens.size() != destTokens.size())
      return false;

    for (int propIndex = 0; propIndex < destTokens.size(); propIndex++) {
      String[] sTokens = sourceTokens.get(propIndex);
      String[] dTokens = destTokens.get(propIndex);

      if (sTokens.length != dTokens.length)
        return false;

      for (int tokenIndex = 0; tokenIndex < sTokens.length; tokenIndex++)
        if (!sTokens[tokenIndex].equalsIgnoreCase(dTokens[tokenIndex]))
          return false;
    }

    return true;
  }

  @Override
  public String toString() {
    return "Strict";
  }
}
