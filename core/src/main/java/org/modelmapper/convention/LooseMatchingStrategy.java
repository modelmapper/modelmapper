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

import org.modelmapper.spi.MatchingStrategy;
import org.modelmapper.spi.PropertyNameInfo;
import org.modelmapper.spi.Tokens;

/**
 * See {@link MatchingStrategies#LOOSE}.
 * 
 * @author Jonathan Halterman
 */
final class LooseMatchingStrategy implements MatchingStrategy {
  @Override
  public boolean matches(PropertyNameInfo propertyNameInfo) {
    return new Matcher(propertyNameInfo).match();
  }

  /**
   * Since this strategy only requires matching the last source and destination properties, property
   * iteration is done in reverse.
   */
  static class Matcher extends InexactMatcher {
    Matcher(PropertyNameInfo propertyNameInfo) {
      super(propertyNameInfo);
    }

    boolean match() {
      if (!matchLastDestTokens())
        return false;

      for (Tokens destTokens : propertyNameInfo.getDestinationPropertyTokens()) {
        for (int destTokenIndex = 0; destTokenIndex < destTokens.size(); destTokenIndex++) {
          DestTokensMatcher matchedTokens = matchSourcePropertyName(destTokens, destTokenIndex);
          if (matchedTokens.match(sourceTokens.size() - 1))
            return true;
        }
      }

      return false;
    }

    boolean matchLastDestTokens() {
      Tokens tokens = destTokens.get(destTokens.size() - 1);
      for (int destTokenIndex = 0; destTokenIndex < tokens.size(); destTokenIndex++) {
        DestTokensMatcher matchedTokens = matchSourcePropertyName(tokens, destTokenIndex);
        if (matchedTokens.match())
          return true;
        if (matchSourcePropertyType(tokens.token(destTokenIndex)) || matchSourceClass(tokens.token(destTokenIndex)))
          return true;
      }
      return false;
    }
  }

  @Override
  public boolean isExact() {
    return false;
  }

  @Override
  public String toString() {
    return "Loose";
  }
}
