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
 * See {@link MatchingStrategies#LOOSE}.
 * 
 * @author Jonathan Halterman
 */
final class LooseMatchingStrategy implements MatchingStrategy {
  public boolean matches(PropertyNameInfo propertyNameInfo) {
    return new Matcher(propertyNameInfo).match();
  }

  /**
   * Since this strategy only requires matching the last source and destination properties, property
   * iteration is done in reverse.
   */
  static class Matcher extends InexactMatcher {
    boolean lastSourceMatched;
    boolean lastDestinationMatched;

    Matcher(PropertyNameInfo propertyNameInfo) {
      super(propertyNameInfo);
    }

    boolean match() {
      List<String[]> destTokens = propertyNameInfo.getDestinationPropertyTokens();

      // Match the last destination property first
      for (int destIndex = destTokens.size() - 1; destIndex >= 0 && !lastSourceMatched; destIndex--) {
        String[] tokens = destTokens.get(destIndex);

        for (int destTokenIndex = 0; destTokenIndex < tokens.length; destTokenIndex++) {
          int matchedTokens = matchSourcePropertyName(tokens, destTokenIndex);
          if (destIndex == destTokens.size() - 1
              && (matchedTokens > 0 || matchSourcePropertyType(tokens[destTokenIndex]) || matchSourceClass(tokens[destTokenIndex])))
            lastDestinationMatched = true;
          if (matchedTokens > 1)
            destTokenIndex += (matchedTokens - 1);
        }
      }

      return lastSourceMatched && lastDestinationMatched;
    }

    /**
     * Returns the number of {@code destTokens} that were matched to a source token starting at
     * {@code destStartIndex}.
     */
    int matchSourcePropertyName(String[] destTokens, int destStartIndex) {
      for (int sourceIndex = sourceTokens.size() - 1; sourceIndex >= 0; sourceIndex--) {
        String[] srcTokens = sourceTokens.get(sourceIndex);
        int matched = matchTokens(srcTokens, destTokens, destStartIndex);
        if (matched > 0) {
          if (sourceIndex == sourceTokens.size() - 1)
            lastSourceMatched = true;
          return matched;
        }
      }

      return 0;
    }
  }

  public boolean isExact() {
    return false;
  }

  @Override
  public String toString() {
    return "Loose";
  }
}
