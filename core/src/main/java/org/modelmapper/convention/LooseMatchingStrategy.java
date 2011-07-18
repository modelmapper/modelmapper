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
 * See {@link MatchingStrategies#Loose}.
 * 
 * @author Jonathan Halterman
 */
final class LooseMatchingStrategy implements MatchingStrategy {
  @Override
  public boolean matches(PropertyNameInfo propertyNameInfo) {
    return new Matcher(propertyNameInfo).match();
  }

  /**
   * Since this strategy only requires matching the last token, property and token iteration is done
   * in reverse.
   */
  static class Matcher {
    private final PropertyNameInfo propertyNameInfo;
    private final List<String[]> sourceTokens;
    boolean lastSourceMatched;

    Matcher(PropertyNameInfo propertyNameInfo) {
      this.propertyNameInfo = propertyNameInfo;
      sourceTokens = propertyNameInfo.getSourcePropertyTokens();
    }

    boolean match() {
      List<String[]> destTokens = propertyNameInfo.getDestinationPropertyTokens();
      for (int destIndex = destTokens.size() - 1; destIndex >= 0 && !lastSourceMatched; destIndex--) {
        String[] tokens = destTokens.get(destIndex);

        for (int destTokenIndex = tokens.length - 1; destTokenIndex >= 0; destTokenIndex--)
          if (!matchSource(tokens[destTokenIndex]))
            return false;
      }

      return lastSourceMatched;
    }

    boolean matchSource(String destination) {
      for (int sourceIndex = sourceTokens.size() - 1; sourceIndex >= 0; sourceIndex--) {
        String[] tokens = sourceTokens.get(sourceIndex);
        for (int tokenIndex = tokens.length - 1; tokenIndex >= 0; tokenIndex--) {
          if (tokens[tokenIndex].equalsIgnoreCase(destination)) {
            if (sourceIndex == sourceTokens.size() - 1)
              lastSourceMatched = true;
            return true;
          }
        }

        tokens = propertyNameInfo.getSourcePropertyTypeTokens().get(sourceIndex);
        for (int tokenIndex = tokens.length - 1; tokenIndex >= 0; tokenIndex--) {
          if (tokens[tokenIndex].equalsIgnoreCase(destination)) {
            return true;
          }
        }

        if (sourceIndex == 0) {
          tokens = propertyNameInfo.getSourceClassTokens();
          for (int tokenIndex = tokens.length - 1; tokenIndex >= 0; tokenIndex--) {
            if (tokens[tokenIndex].equalsIgnoreCase(destination))
              return true;
          }
        }
      }

      return false;
    }
  }

  @Override
  public boolean isExact() {
    return false;
  }
}
