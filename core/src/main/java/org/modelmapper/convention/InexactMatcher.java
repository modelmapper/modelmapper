/*
 * Copyright 2012 the original author or authors.
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

import org.modelmapper.spi.MatchingStrategy.PropertyNameInfo;

/**
 * Performs inexact matching of property tokens.
 * 
 * @author Jonathan Halterman
 */
class InexactMatcher {
  protected final PropertyNameInfo propertyNameInfo;
  protected final List<String[]> sourceTokens;

  InexactMatcher(PropertyNameInfo propertyNameInfo) {
    this.propertyNameInfo = propertyNameInfo;
    sourceTokens = propertyNameInfo.getSourcePropertyTokens();
  }

  /**
   * Returns the number of {@code dst} elements that were matched to a source starting at
   * {@code dstStartIndex}. {@code src} and {@code dst} elements can be matched in combination.
   */
  static int matchTokens(String[] src, String[] dst, int dstStartIndex) {
    for (int srcStartIndex = 0; srcStartIndex < src.length; srcStartIndex++) {
      String srcStr = src[srcStartIndex];
      String dstStr = dst[dstStartIndex];

      for (int srcIndex = srcStartIndex, dstIndex = dstStartIndex, srcCharIndex = 0, dstCharIndex = 0; srcStr.length() > 0 && dstStr.length() > 0;) {
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

  /**
   * Returns whether the source class token is an inexact to the {@code destination}.
   */
  boolean matchSourceClass(String destination) {
    String[] tokens = propertyNameInfo.getSourceClassTokens();
    for (int tokenIndex = 0; tokenIndex < tokens.length; tokenIndex++)
      if (tokens[tokenIndex].equalsIgnoreCase(destination))
        return true;
    return false;
  }

  /**
   * Returns whether any source property type token is an inexact to the {@code destination}.
   */
  boolean matchSourcePropertyType(String destination) {
    for (int sourceIndex = 0; sourceIndex < sourceTokens.size(); sourceIndex++) {
      String[] tokens = propertyNameInfo.getSourcePropertyTypeTokens().get(sourceIndex);
      for (int tokenIndex = 0; tokenIndex < tokens.length; tokenIndex++)
        if (tokens[tokenIndex].equalsIgnoreCase(destination))
          return true;
    }

    return false;
  }
}
