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

import java.util.HashSet;
import java.util.Set;

import org.modelmapper.spi.MatchingStrategy;
import org.modelmapper.spi.PropertyNameInfo;
import org.modelmapper.spi.Tokens;

/**
 * See {@link MatchingStrategies#STANDARD}.
 * 
 * @author Jonathan Halterman
 */
final class StandardMatchingStrategy implements MatchingStrategy {
  @Override
  public boolean matches(PropertyNameInfo propertyNameInfo) {
    return new Matcher(propertyNameInfo).match();
  }

  static class Matcher extends InexactMatcher {
    Matcher(PropertyNameInfo propertyNameInfo) {
      super(propertyNameInfo);
    }

    boolean match() {
      Set<Integer> matchSources = new HashSet<Integer>();
      for (Tokens destTokens : propertyNameInfo.getDestinationPropertyTokens()) {
        for (int destTokenIndex = 0; destTokenIndex < destTokens.size();) {
          DestTokensMatcher matchedTokens = matchSourcePropertyName(destTokens, destTokenIndex);
          if (matchedTokens.match()) {
            destTokenIndex += matchedTokens.maxMatchTokens();
            matchSources.addAll(matchedTokens.matchSources());
          } else if (matchSourcePropertyType(destTokens.token(destTokenIndex))
              || matchSourceClass(destTokens.token(destTokenIndex)))
            destTokenIndex++;
          else
            return false;
        }
      }

      return matchSources.size() == sourceTokens.size();
    }
  }

  @Override
  public boolean isExact() {
    return false;
  }

  @Override
  public String toString() {
    return "Standard";
  }
}
