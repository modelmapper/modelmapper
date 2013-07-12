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
package org.modelmapper.config;

import java.util.List;

import org.modelmapper.Condition;
import org.modelmapper.Provider;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MatchingStrategy;
import org.modelmapper.spi.NameTokenizer;
import org.modelmapper.spi.NameTransformer;
import org.modelmapper.spi.NamingConvention;

/**
 * Configures conventions used during the matching process.
 * 
 * @author Jonathan Halterman
 */
public interface Configuration {
  /**
   * The level at and below which properties can be accessed.
   */
  public enum AccessLevel {
    /** Only public properties are accessible. */
    PUBLIC,
    /** All public and protected properties are accessible. */
    PROTECTED,
    /** All public, protected and package private properties are accessible. */
    PACKAGE_PRIVATE,
    /** All properties are accessible. */
    PRIVATE;
  }

  /**
   * Returns a copy of the Configuration.
   */
  Configuration copy();

  /**
   * Sets whether field matching should be enabled. When true, mapping may take place between
   * accessible fields. Default is {@code false}.
   * 
   * @param enabled whether field matching is enabled
   * @see #isFieldMatchingEnabled()
   * @see #setFieldAccessLevel(AccessLevel)
   */
  Configuration enableFieldMatching(boolean enabled);

  /**
   * Gets the ordered list of internal conditional converters that are used to perform type
   * conversion. This list is mutable and may be modified to control which converters are used to
   * perform type conversion along with the order in which converters are selected.
   * 
   * <p>
   * This method is part of the ModelMapper SPI.
   */
  List<ConditionalConverter<?, ?>> getConverters();

  /**
   * Returns the destination name tokenizer.
   * 
   * @see #setDestinationNameTokenizer(NameTokenizer)
   */
  NameTokenizer getDestinationNameTokenizer();

  /**
   * Returns the destination name transformer.
   * 
   * @see #setDestinationNameTransformer(NameTransformer)
   */
  NameTransformer getDestinationNameTransformer();

  /**
   * Returns the destination naming convention.
   * 
   * @see #setDestinationNamingConvention(NamingConvention)
   */
  NamingConvention getDestinationNamingConvention();

  /**
   * Returns the field access level.
   * 
   * @see #setFieldAccessLevel(AccessLevel)
   */
  AccessLevel getFieldAccessLevel();

  /**
   * Gets the matching strategy.
   * 
   * @see #setMatchingStrategy(MatchingStrategy)
   */
  MatchingStrategy getMatchingStrategy();

  /**
   * Returns the method access level.
   * 
   * @see #setMethodAccessLevel(AccessLevel)
   */
  AccessLevel getMethodAccessLevel();

  /**
   * Returns the Condition that must apply for a property in order for mapping to take place, else
   * {@code null} if no condition has been configured.
   * 
   * @see #setPropertyCondition(Condition)
   */
  Condition<?, ?> getPropertyCondition();

  /**
   * Returns the Provider used for provisioning destination object instances, else {@code null} if
   * no Provider has been configured.
   * 
   * @see #setProvider(Provider)
   */
  Provider<?> getProvider();

  /**
   * Returns the source name tokenizer.
   * 
   * @see #setSourceNameTokenizer(NameTokenizer)
   */
  NameTokenizer getSourceNameTokenizer();

  /**
   * Returns the source name transformer.
   * 
   * @see #setSourceNameTransformer(NameTransformer)
   */
  NameTransformer getSourceNameTransformer();

  /**
   * Gets the source naming convention.
   * 
   * @see #setSourceNamingConvention(NamingConvention)
   */
  NamingConvention getSourceNamingConvention();

  /**
   * Sets whether destination properties that match more than one source property should be ignored.
   * When true ambiguous destination properties are skipped during the matching process. When false
   * a ConfigurationException is thrown when ambiguous properties are encountered.
   * 
   * @param ignore whether ambiguity is to be ignored
   * @see #isAmbiguityIgnored()
   */
  Configuration ignoreAmbiguity(boolean ignore);

  /**
   * Returns {@code true} if ambiguous properties are ignored or {@code false} if they will result
   * in an exception.
   * 
   * @see #ignoreAmbiguity(boolean)
   */
  boolean isAmbiguityIgnored();

  /**
   * Returns whether field matching is enabled.
   * 
   * @see #enableFieldMatching(boolean)
   */
  boolean isFieldMatchingEnabled();

  /**
   * Sets the tokenizer to be applied to destination property and class names during the matching
   * process.
   * 
   * @throws IllegalArgumentException if {@code nameTokenizer} is null
   */
  Configuration setDestinationNameTokenizer(NameTokenizer nameTokenizer);

  /**
   * Sets the name transformer used to transform destination property and class names during the
   * matching process.
   * 
   * @throws IllegalArgumentException if {@code nameTransformer} is null
   */
  Configuration setDestinationNameTransformer(NameTransformer nameTransformer);

  /**
   * Sets the convention used to identify destination property names during the matching process.
   * 
   * @throws IllegalArgumentException if {@code namingConvention} is null
   */
  Configuration setDestinationNamingConvention(NamingConvention namingConvention);

  /**
   * Indicates that fields should be eligible for matching at the given {@code accessLevel}.
   * 
   * <p>
   * <b>Note</b>: Field access is only used when {@link #enableFieldMatching(boolean) field
   * matching} is enabled.
   * 
   * @throws IllegalArgumentException if {@code accessLevel} is null
   * @see AccessLevel
   * @see #enableFieldMatching(boolean)
   */
  Configuration setFieldAccessLevel(AccessLevel accessLevel);

  /**
   * Sets the strategy used to match source properties to destination properties.
   * 
   * @throws IllegalArgumentException if {@code matchingStrategy} is null
   */
  Configuration setMatchingStrategy(MatchingStrategy matchingStrategy);

  /**
   * Indicates that methods should be eligible for matching at the given {@code accessLevel}.
   * 
   * @throws IllegalArgumentException if {@code accessLevel} is null
   * @see AccessLevel
   */
  Configuration setMethodAccessLevel(AccessLevel accessLevel);

  /**
   * Sets the {@code condition} that must apply for a property in order for mapping to take place.
   * This is overridden by any property conditions defined in a TypeMap or PropertyMap.
   * 
   * @throws IllegalArgumentException if {@code condition} is null
   */
  Configuration setPropertyCondition(Condition<?, ?> condition);

  /**
   * Sets the {@code provider} to use for providing destination object instances.
   * 
   * @param provider to register
   * @throws IllegalArgumentException if {@code provider} is null
   */
  Configuration setProvider(Provider<?> provider);

  /**
   * Sets the tokenizer to be applied to source property and class names during the matching
   * process.
   * 
   * @throws IllegalArgumentException if {@code nameTokenizer} is null
   */
  Configuration setSourceNameTokenizer(NameTokenizer nameTokenizer);

  /**
   * Sets the name transformer used to transform source property and class names during the matching
   * process.
   * 
   * @throws IllegalArgumentException if {@code nameTransformer} is null
   */
  Configuration setSourceNameTransformer(NameTransformer nameTransformer);

  /**
   * Sets the convention used to identify source property names during the matching process.
   * 
   * @throws IllegalArgumentException if {@code namingConvention} is null
   */
  Configuration setSourceNamingConvention(NamingConvention namingConvention);
}
