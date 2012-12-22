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

  static class Matcher {
    private final PropertyNameInfo propertyNameInfo;
    private final List<String[]> sourceTokens;
    private final boolean[] sourceMatches;

    Matcher(PropertyNameInfo propertyNameInfo) {
      this.propertyNameInfo = propertyNameInfo;
      sourceTokens = propertyNameInfo.getSourcePropertyTokens();
      sourceMatches = new boolean[sourceTokens.size()];
    }

    boolean match() {
      List<String[]> destTokens = propertyNameInfo.getDestinationPropertyTokens();
      for (int destIndex = 0; destIndex < destTokens.size(); destIndex++) {
        String[] tokens = destTokens.get(destIndex);

        for (int destTokenIndex = 0; destTokenIndex < tokens.length;) {
          int matchedTokens = matchSourcePropertyName(tokens, destTokenIndex);
          if (matchedTokens == 0)
            if (!matchSourcePropertyType(tokens[destTokenIndex])
                && !matchSourceClass(tokens[destTokenIndex]))
              return false;
            else
              destTokenIndex += 1;
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
        int result = matchTokens(srcTokens, destTokens, destStartIndex);
        if (result > 0) {
          sourceMatches[sourceIndex] = true;
          return result;
        }
      }

      return 0;
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

  /**
   * Returns the number of {@code dst} elements that were matched to a source starting at
   * {@code dstStartIndex}. {@code src} and {@code dst} elements can be matched in combination.
   */
  static int matchTokens(String[] src, String[] dst, int dstStartIndex) {
    for (int srcStartIndex = 0; srcStartIndex < src.length; srcStartIndex++) {
      String srcStr = src[srcStartIndex];
      String dstStr = dst[dstStartIndex];

      for (int srcIndex = srcStartIndex, dstIndex = dstStartIndex, srcCharIndex = 0, dstCharIndex = 0;;) {
        char c1 = srcStr.charAt(srcCharIndex);
        char c2 = dstStr.charAt(dstCharIndex);

        if (Character.toUpperCase(c1) != Character.toUpperCase(c2)
            || Character.toLowerCase(c1) != Character.toLowerCase(c2))
          break;

        if (dstCharIndex == dstStr.length() - 1) {
          // Token match
          if (srcCharIndex == srcStr.length() - 1)
            return (dstIndex - dstStartIndex) + 1;
          // Done with dest tokens
          if (dstIndex == dst.length - 1)
            return 0;

          dstStr = dst[++dstIndex];
          dstCharIndex = 0;
        } else
          dstCharIndex++;

        if (srcCharIndex == srcStr.length() - 1) {
          // Done with source tokens
          if (srcIndex == src.length - 1)
            return 0;
          srcStr = src[++srcIndex];
          srcCharIndex = 0;
        } else
          srcCharIndex++;
      }
    }

    return 0;
  }

  public boolean isExact() {
    return false;
  }
}
