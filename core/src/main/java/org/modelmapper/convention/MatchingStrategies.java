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

/**
 * {@link MatchingStrategy} implementations. Matching strategies identify source to destination
 * property matches by comparing source and destination type, property and property type names which
 * are converted and tokenized prior to matching.
 * 
 * @author Jonathan Halterman
 */
public class MatchingStrategies {
  /**
   * A matching strategy that allows for source properties to be loosely matched to destination
   * properties by requiring that <i>only</i> the last destination property in a hierarchy be
   * matched. The following rules apply:
   * 
   * <ul>
   * <li>Tokens can be matched in <i>any</i> order</li>
   * <li>The last destination property name must have all tokens matched</li>
   * <li>The last source property name must have at least one token matched</li>
   * </ul>
   */
  public static final MatchingStrategy LOOSE = new LooseMatchingStrategy();

  /**
   * A matching strategy that allows for source properties to be intelligently matched to
   * destination properties, requiring that <i>all</i> destination properties be matched and all
   * source property names have at least one token matched. The following rules apply:
   * 
   * <ul>
   * <li>Tokens can be matched in <i>any</i> order</li>
   * <li>All destination property name tokens must be matched</li>
   * <li>All source property names must have at least one token matched</li>
   * <li>Tokens can be combined and matched in order</li>
   * </ul>
   */
  public static final MatchingStrategy STANDARD = new StandardMatchingStrategy();

  /**
   * A matching strategy that allows for source properties to be strictly matched to destination
   * properties. This strategy allows for complete matching accuracy, ensuring that no mismatches or
   * ambiguity occurs. But it requires that property name tokens on the source and destination side
   * match each other precisely. The following rules apply:
   * 
   * <ul>
   * <li>Tokens are matched in <i>strict</i> order</li>
   * <li>All destination property name tokens must be matched</li>
   * <li>All source property names must have all tokens matched</li>
   * </ul>
   */
  public static final MatchingStrategy STRICT = new StrictMatchingStrategy();
}
