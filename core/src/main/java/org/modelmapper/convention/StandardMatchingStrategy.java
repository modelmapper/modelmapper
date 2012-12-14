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

import org.modelmapper.internal.util.Strings;
import org.modelmapper.spi.MatchingStrategy;
import org.modelmapper.spi.PropertyInfo;

/**
 * See {@link MatchingStrategies#STANDARD}.
 * 
 * @author Jonathan Halterman
 */
final class StandardMatchingStrategy implements MatchingStrategy {
  public boolean matches(PropertyNameInfo propertyNameInfo) {
    return new Matcher(propertyNameInfo).match();
  }

  static class Matcher {
    private final PropertyNameInfo propertyNameInfo;
    private final List<PropertyInfo> sourceProperties;
    private final List<String[]> sourceTokens;
    private final boolean[] sourceMatches;

    Matcher(PropertyNameInfo propertyNameInfo) {
      this.propertyNameInfo = propertyNameInfo;
      sourceProperties = propertyNameInfo.getSourceProperties();
      sourceTokens = propertyNameInfo.getSourcePropertyTokens();
      sourceMatches = new boolean[sourceTokens.size()];
    }

    boolean match() {
      List<String[]> destTokens = propertyNameInfo.getDestinationPropertyTokens();
      for (int destIndex = 0; destIndex < destTokens.size(); destIndex++) {
        String[] tokens = destTokens.get(destIndex);

        for (int destTokenIndex = 0; destTokenIndex < tokens.length; destTokenIndex++)
          if (!matchSourcePropertyName(tokens[destTokenIndex])
              && !matchSourcePropertyType(tokens[destTokenIndex])
              && !matchSourceClass(tokens[destTokenIndex]))
            return false;
      }

      for (int i = 0; i < sourceMatches.length; i++)
        if (!sourceMatches[i] && !matchedPreviously(i))
          return false;

      return true;
    }

    boolean matchSourcePropertyName(String destination) {
      for (int sourceIndex = 0; sourceIndex < sourceTokens.size(); sourceIndex++) {
        // Attempt to match full source property name
        if (sourceProperties.get(sourceIndex).getName().equalsIgnoreCase(destination)) {
          sourceMatches[sourceIndex] = true;
          return true;
        }

        String[] tokens = sourceTokens.get(sourceIndex);

        // Attempt to match individual source tokens
        for (int tokenIndex = 0; tokenIndex < tokens.length; tokenIndex++) {
          // Attempt to match an individual token
          if (tokens[tokenIndex].equalsIgnoreCase(destination)) {
            sourceMatches[sourceIndex] = true;
            return true;
          }

          // Attempt to match combined source tokens
          if (tokenIndex != 0 && tokenIndex < tokens.length - 1
              && Strings.contentEqualsIgnoreCase(tokenIndex + 1, tokens, destination)) {
            sourceMatches[sourceIndex] = true;
            return true;
          }
        }
      }

      return false;
    }

    boolean matchSourcePropertyType(String destination) {
      for (int sourceIndex = 0; sourceIndex < sourceTokens.size(); sourceIndex++) {
        String[] tokens = propertyNameInfo.getSourcePropertyTypeTokens().get(sourceIndex);
        for (int tokenIndex = 0; tokenIndex < tokens.length; tokenIndex++)
          if (tokens[tokenIndex].equalsIgnoreCase(destination))
            return true;
      }

      return false;
    }

    boolean matchSourceClass(String destination) {
      String[] tokens = propertyNameInfo.getSourceClassTokens();
      for (int tokenIndex = 0; tokenIndex < tokens.length; tokenIndex++)
        if (tokens[tokenIndex].equalsIgnoreCase(destination))
          return true;
      return false;
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
}
