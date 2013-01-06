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
 * See {@link MatchingStrategies#STANDARD}.
 * 
 * @author Jonathan Halterman
 */
final class StandardMatchingStrategy implements MatchingStrategy {
  public boolean matches(PropertyNameInfo propertyNameInfo) {
    return new Matcher(propertyNameInfo).match();
  }

  static class Matcher extends InexactMatcher {
    private final boolean[] sourceMatches;

    Matcher(PropertyNameInfo propertyNameInfo) {
      super(propertyNameInfo);
      sourceMatches = new boolean[sourceTokens.size()];
    }

    boolean match() {
      List<String[]> destTokens = propertyNameInfo.getDestinationPropertyTokens();
      for (int destIndex = 0; destIndex < destTokens.size(); destIndex++) {
        String[] tokens = destTokens.get(destIndex);

        for (int destTokenIndex = 0; destTokenIndex < tokens.length;) {
          int matchedTokens = matchSourcePropertyName(tokens, destTokenIndex);
          if (matchedTokens == 0)
            if (matchSourcePropertyType(tokens[destTokenIndex])
                || matchSourceClass(tokens[destTokenIndex]))
              destTokenIndex += 1;
            else
              return false;
          else
            destTokenIndex += matchedTokens;
        }
      }

      // Ensure that each source property has at least one token matched
      for (int i = 0; i < sourceMatches.length; i++)
        if (!sourceMatches[i] && !matchedPreviously(i))
          return false;

      return true;
    }

    /**
     * Returns the number of {@code destTokens} that were matched to a source token starting at
     * {@code destStartIndex}.
     */
    int matchSourcePropertyName(String[] destTokens, int destStartIndex) {
      for (int sourceIndex = 0; sourceIndex < sourceTokens.size(); sourceIndex++) {
        String[] srcTokens = sourceTokens.get(sourceIndex);
        int matched = matchTokens(srcTokens, destTokens, destStartIndex);
        if (matched > 0) {
          sourceMatches[sourceIndex] = true;
          return matched;
        }
      }

      return 0;
    }

    /**
     * Checks to see whether an unmatched source property contains any tokens that were matched by a
     * previous property in the source hierarchy.
     * 
     * @param index of unmatched source property
     */
    boolean matchedPreviously(int index) {
      for (int sourceIndex = 0; sourceIndex < index; sourceIndex++) {
        if (sourceMatches[sourceIndex]) {
          String[] current = sourceTokens.get(index);
          String[] previous = sourceTokens.get(sourceIndex);

          for (int i = 0; i < current.length; i++)
            for (int j = 0; j < previous.length; j++)
              if (current[i].equalsIgnoreCase(previous[j]))
                return true;
        }
      }

      return false;
    }
  }

  public boolean isExact() {
    return false;
  }

  @Override
  public String toString() {
    return "Standard";
  }
}
