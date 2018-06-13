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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.modelmapper.spi.PropertyNameInfo;
import org.modelmapper.spi.Tokens;

/**
 * Performs inexact matching of property tokens.
 * 
 * @author Jonathan Halterman
 */
class InexactMatcher {
  protected final PropertyNameInfo propertyNameInfo;
  protected final List<Tokens> sourceTokens;
  protected final List<Tokens> destTokens;

  InexactMatcher(PropertyNameInfo propertyNameInfo) {
    this.propertyNameInfo = propertyNameInfo;
    sourceTokens = propertyNameInfo.getSourcePropertyTokens();
    destTokens = propertyNameInfo.getDestinationPropertyTokens();
  }

  /**
   * Returns the number of {@code dst} elements that were matched to a source starting at
   * {@code dstStartIndex}. {@code src} and {@code dst} elements can be matched in combination.
   */
  static int matchTokens(Tokens src, Tokens dst, int dstStartIndex) {
    for (int srcStartIndex = 0; srcStartIndex < src.size(); srcStartIndex++) {
      TokensIterator srcTokensIterator = TokensIterator.of(src, srcStartIndex);
      TokensIterator dstTokensIterator = TokensIterator.of(dst, dstStartIndex);
      StringIterator srcStrIterator = StringIterator.of(srcTokensIterator.next());
      StringIterator dstStrIterator = StringIterator.of(dstTokensIterator.next());
      while (srcStrIterator.hasNext() || srcStrIterator.hasNext()) {
        if (!matchToken(srcStrIterator, dstStrIterator))
          break;
        if (!srcStrIterator.hasNext() && !dstStrIterator.hasNext())
          return dstTokensIterator.pos() - dstStartIndex + 1;
        if (!srcStrIterator.hasNext() && !srcTokensIterator.hasNext())
          break;
        if (!dstStrIterator.hasNext() && !dstTokensIterator.hasNext())
          break;
        if (!srcStrIterator.hasNext() && srcTokensIterator.hasNext())
          srcStrIterator = StringIterator.of(srcTokensIterator.next());
        if (!dstStrIterator.hasNext() && dstTokensIterator.hasNext())
          dstStrIterator = StringIterator.of(dstTokensIterator.next());
      }
    }

    return 0;
  }

  static boolean matchToken(StringIterator srcStrIterator, StringIterator destStrIterator) {
    while (srcStrIterator.hasNext() && destStrIterator.hasNext()) {
      char srcChar = srcStrIterator.next();
      char destChar = destStrIterator.next();

      if (Character.toUpperCase(srcChar) != Character.toUpperCase(destChar)
          || Character.toLowerCase(srcChar) != Character.toLowerCase(destChar))
        return false;
    }
    return true;
  }

  /**
   * Returns whether the source class token is an inexact to the {@code destination}.
   */
  boolean matchSourceClass(String destination) {
    for (String token: propertyNameInfo.getSourceClassTokens())
      if (token.equalsIgnoreCase(destination))
        return true;
    return false;
  }

  /**
   * Returns whether any source property type token is an inexact to the {@code destination}.
   */
  boolean matchSourcePropertyType(String destination) {
    for (Tokens tokens: propertyNameInfo.getSourcePropertyTypeTokens())
      for (String token: tokens)
        if (token.equalsIgnoreCase(destination))
          return true;
    return false;
  }

  /**
   * Returns the numbers of match counts for each {@code sourceTokens} that were matched to{@code destTokens} starting at
   * {@code destStartIndex}.
   */
  DestTokensMatcher matchSourcePropertyName(Tokens destTokens, int destStartIndex) {
    int[] matchedTokens = new int[sourceTokens.size()];
    for (int sourceIndex = 0; sourceIndex < sourceTokens.size(); sourceIndex++) {
      Tokens srcTokens = sourceTokens.get(sourceIndex);
      matchedTokens[sourceIndex] = matchTokens(srcTokens, destTokens, destStartIndex);
    }
    return new DestTokensMatcher(matchedTokens);
  }

  static class TokensIterator {
    private Tokens tokens;
    private int pos;

    static TokensIterator of(Tokens tokens, int pos) {
      return new TokensIterator(tokens, pos - 1);
    }

    TokensIterator(Tokens tokens, int pos) {
      this.tokens = tokens;
      this.pos = pos;
    }

    public boolean hasNext() {
      return pos < tokens.size() - 1;
    }

    public String next() {
      return tokens.token(++pos);
    }

    public int pos() {
      return pos;
    }
  }

  static class StringIterator {
    private String text;
    private int pos = -1;

    static StringIterator of(String text) {
      return new StringIterator(text);
    }

    StringIterator(String text) {
      this.text = text;
    }

    public boolean hasNext() {
      return pos < text.length() - 1;
    }

    public Character next() {
      return text.charAt(++pos);
    }
  }

  static class DestTokensMatcher {
    int[] counts;

    DestTokensMatcher(int[] counts) {
      this.counts = counts;
    }

    boolean match() {
      for (int count : counts)
        if (count > 0)
          return true;
      return false;
    }

    boolean match(int index) {
      return counts[index] > 0;
    }

    int maxMatchTokens() {
      int maxIndex = 0;
      for (int i = 1; i < counts.length; i++)
        if (counts[i] > counts[maxIndex])
          maxIndex = i;
      return counts[maxIndex];
    }

    Collection<Integer> matchSources() {
      List<Integer> indexes = new ArrayList<Integer>();
      for (int i = 0; i < counts.length; i++)
        if (counts[i] > 0)
          indexes.add(i);
      return indexes;
    }
  }
}
