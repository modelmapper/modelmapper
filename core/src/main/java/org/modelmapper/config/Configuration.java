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
import org.modelmapper.PropertyMap;
import org.modelmapper.Provider;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.ConditionalConverter.MatchResult;
import org.modelmapper.spi.MatchingStrategy;
import org.modelmapper.spi.NameTokenizer;
import org.modelmapper.spi.NameTransformer;
import org.modelmapper.spi.NamingConvention;
import org.modelmapper.spi.ValueReader;

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
   * Registers the {@code valueReader} to use when mapping from instances of types {@code T}.
   * 
   * <p>
   * This method is part of the ModelMapper SPI.
   * 
   * @param <T> source type
   * @param valueReader to register
   * @throws IllegalArgumentException if {@code valueReader} is null or if type argument {@code T}
   *           is not declared for the {@code valueReader}
   */
  <T> Configuration addValueReader(ValueReader<T> valueReader);

  /**
   * Returns a copy of the Configuration.
   */
  Configuration copy();

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
   * Gets a thread-safe, mutable, ordered list of internal and user-defined ValueReaders that are
   * used to read source object values during mapping. This list is may be modified to control which
   * ValueReaders are used to along with the order in which ValueReaders are selected for a source
   * type.
   * 
   * <p>
   * The returned List throws an IllegalArgumentException when attempting to add or set a
   * ValueReader for which the type argument {@code T} has not been defined.
   * 
   * <p>
   * This method is part of the ModelMapper SPI.
   */
  List<ValueReader<?>> getValueReaders();

  /**
   * Returns {@code true} if ambiguous properties are ignored or {@code false} if they will result
   * in an exception.
   * 
   * @see #setAmbiguityIgnored(boolean)
   */
  boolean isAmbiguityIgnored();

  /**
   * Returns whether field matching is enabled.
   * 
   * @see #setFieldMatchingEnabled(boolean)
   */
  boolean isFieldMatchingEnabled();

  /**
   * Returns {@code true} if {@link ConditionalConverter}s must define a {@link MatchResult#FULL
   * full} match in order to be applied. Otherwise conditional converters may also be applied for a
   * {@link MatchResult#PARTIAL partial} match.
   * <p>
   * Default is {@code false}.
   * 
   * @see #setFullTypeMatchingRequired(boolean)
   */
  boolean isFullTypeMatchingRequired();

  /**
   * Returns whether implicit mapping should be enabled. When {@code true} (default), ModelMapper
   * will implicitly map source to destination properties based on configured conventions. When
   * {@code false}, only explicit mappings defined in {@link PropertyMap property maps} will be
   * used.
   * 
   * @see #setImplicitMappingEnabled(boolean)
   */
  boolean isImplicitMappingEnabled();

  /**
   * Sets whether destination properties that match more than one source property should be ignored.
   * When true, ambiguous destination properties are skipped during the matching process. When
   * false, a ConfigurationException is thrown when ambiguous properties are encountered.
   * 
   * @param ignore whether ambiguity is to be ignored
   * @see #isAmbiguityIgnored()
   */
  Configuration setAmbiguityIgnored(boolean ignore);

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
   * <b>Note</b>: Field access is only used when {@link #setFieldMatchingEnabled(boolean) field
   * matching} is enabled.
   * 
   * @throws IllegalArgumentException if {@code accessLevel} is null
   * @see #setFieldMatchingEnabled(boolean)
   */
  Configuration setFieldAccessLevel(AccessLevel accessLevel);

  /**
   * Sets whether field matching should be enabled. When true, mapping may take place between
   * accessible fields. Default is {@code false}.
   * 
   * @param enabled whether field matching is enabled
   * @see #isFieldMatchingEnabled()
   * @see #setFieldAccessLevel(AccessLevel)
   */
  Configuration setFieldMatchingEnabled(boolean enabled);

  /**
   * Set whether {@link ConditionalConverter}s must define a {@link MatchResult#FULL full} match in
   * order to be applied. If {@code false}, conditional converters may also be applied for a
   * {@link MatchResult#PARTIAL partial} match.
   * 
   * @param required whether full type matching is required for conditional converters.
   * @see #isFullTypeMatchingRequired()
   */
  Configuration setFullTypeMatchingRequired(boolean required);

  /**
   * Sets whether implicit mapping should be enabled. When {@code true} (default), ModelMapper will
   * implicitly map source to destination properties based on configured conventions. When
   * {@code false}, only explicit mappings defined in {@link PropertyMap property maps} will be
   * used.
   * 
   * @param enabled whether implicit matching is enabled
   * @see #isImplicitMappingEnabled()
   */
  Configuration setImplicitMappingEnabled(boolean enabled);

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
