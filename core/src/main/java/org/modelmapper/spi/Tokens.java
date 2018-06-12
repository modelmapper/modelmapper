package org.modelmapper.spi;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Represents tokens of a property
 *
 * @author  Chun Han Hsiao
 */
public class Tokens implements Iterable<String> {
  public static Tokens of(String... tokens) {
    return new Tokens(tokens);
  }

  private String[] tokens;

  private Tokens(String[] tokens) {
    this.tokens = tokens;
  }

  public String token(int pos) {
    return tokens[pos];
  }

  public int size() {
    return tokens.length;
  }

  @Override
  public Iterator<String> iterator() {
    return Arrays.asList(tokens).iterator();
  }

  @Override
  public String toString() {
    return Arrays.toString(tokens);
  }
}
