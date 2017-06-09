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
package org.modelmapper;

import net.jodah.typetools.TypeResolver;

import org.modelmapper.builder.ConverterExpression;
import org.modelmapper.builder.MapExpression;
import org.modelmapper.builder.ProviderExpression;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.internal.ExplicitMappingBuilder;
import org.modelmapper.internal.util.Assert;

/**
 * A PropertyMap defines mappings between properties for a particular source and destination type.
 * <p>
 * To create a PropertyMap simply extend {@code PropertyMap}, supplying type arguments to represent
 * the source type {@code <S>} and destination type {@code <D>}, then override the
 * {@link #configure()} method.
 * 
 * <pre>
 *   public class OrderMap extends PropertyMap&lt;Order, OrderDTO&gt;() {
 *     protected void configure() {
 *       map().setCustomer(source.getCustomerName());
 *       map().address.setStreet(source.address.streetName);
 *     }
 *   };
 * </pre>
 * 
 * <h2 id=0>Mapping EDSL</h2>
 * <p>
 * PropertyMap uses an Embedded Domain Specific Language (EDSL) to define how source and destination
 * methods and values map to each other. The Mapping EDSL allows you to define mappings using actual
 * code that references the source and destination properties you wish to map. Usage of the EDSL is
 * demonstrated in the examples below.
 * 
 * <h3 id=1>Mapping</h3>
 * <p>
 * This example maps the destination type's {@code setName} method to the source type's
 * {@code getFirstName} method.
 * 
 * <pre>    map().setName(source.getFirstName());</pre>
 * 
 * This example maps the destination type's {@code setLastName} method to the source type's
 * {@code surName} field.
 * 
 * <pre>    map().setLastName(source.surName);</pre>
 * 
 * Alternatively we can map the source type's {@code surName} field directly destination type's
 * {@code lastName} field.
 * 
 * <pre>    map(source.surName, destination.lastName);</pre>
 * 
 * This example maps the destination type's {@code setEmployer} method to the constant
 * {@code "Initech"}.
 * 
 * <pre>    map().setEmployer(&quot;Initech&quot;);</pre>
 * 
 * This example maps the constant {@code "Initech"} to destination type's {@code employer} field.
 * 
 * <pre>    map("Initech", destination.employer);</pre>
 * 
 * Map statements can also be written to map methods whose types do not match:
 * 
 * <pre>    map(source.getAge()).setAgeString(null);</pre>
 * 
 * Similar for constant values:
 * 
 * <pre>    map(21).setAgeString(null);</pre>
 * 
 * <b>Note</b>: When a value is provided on the left-hand side of a {@code map} method, any value
 * provided on the right-hand side in a setter is not used.
 * 
 * <h3 id=2>Deep mapping</h3>
 * <p>
 * This example Maps the destination type's {@code setAge} method to the source type's
 * {@code getCustomer().getAge()} method hierarchy, allowing deep mapping to occur between the
 * source and destination methods.
 * 
 * <pre>    map().setAge(source.getCustomer().getAge());</pre>
 * 
 * This example maps the destination type's {@code getCustomer().setName()} method hierarchy to the
 * source type's {@code getPerson().getFirstName()} property hierarchy.
 * 
 * <pre>    map().getCustomer().setName(source.person.getFirstName());</pre>
 * 
 * <b>Note</b>: In order to populate the destination object, deep mapping requires the
 * {@code getCustomer} method to have a corresponding mutator, such as a {@code setCustomer} method
 * or an {@link org.modelmapper.config.Configuration#setFieldAccessLevel(AccessLevel) accessible}
 * {@code customer} field.
 * <p>
 * We can also mix field references into either the source or destination when deep mapping.
 * 
 * <pre>
 *    map(source.customer.age, destination);
 *    map().customer.setName(source.getPerson().firstName);
 * </pre>
 * 
 * Deep mapping can also be performed for source properties or values whose types do not match the
 * destination property’s type.
 * 
 * <pre>    map(source.person.getAge()).setAgeString(null);</pre>
 * 
 * <h3 id=3>Skipping properties</h3>
 * <p>
 * This example specifies that the destination type's {@code setName} method should be skipped
 * during the mapping process.
 * 
 * <pre>    skip().setName(null);</pre>
 * 
 * <b>Note</b>: Since the {@code setName} method is skipped the {@code null} value is unused.
 * <p>
 * We can also skip the mapping of fields.
 * 
 * <pre>    skip(destination.address);</pre>
 * 
 * <h3 id=4>Converters</h3>
 * <p>
 * This example specifies that the {@code toUppercase} {@link Converter} be used when mapping the
 * source type's {@code getName} method to the destination type's {@code setName} method:
 * 
 * <pre>    using(toUppercase).map().setName(source.getName());</pre>
 * 
 * We can also use a Converter to map fields:
 * 
 * <pre>    using(toUppercase).map(source.name, destination.name);</pre>
 * 
 * This example specifies that the {@code personToNameConverter} {@link Converter} be used when
 * mapping the source <i>object</i> to the destination type's {@code setName} method:
 * 
 * <pre>    using(personToNameConverter).map(source).setName(null);</pre>
 * 
 * <b>Note</b>: Since a {@code source} object is given the {@code null} value passed to
 * {@code setName()} is unused.
 * 
 * <h3 id=5>Conditional mapping</h3>
 * <p>
 * This example specifies that the {@code isLocalAddress} {@link Condition} must apply in order for
 * mapping to occur between the the the source type's {@code getAddress} method and the destination
 * type's {@code setAddress} method. If the condition does not apply, mapping to the
 * {@code setAddress} method will be skipped.
 * 
 * <pre>    when(isLocalAddress).map().setAddress(source.getAddress());</pre>
 * 
 * We can also conditionally skip the mapping of fields.
 * 
 * <pre>    when(notNull).skip(source.name, destination.name);</pre>
 * 
 * This example specifies that the {@code Conditions.isNull} {@link Condition} must apply in order
 * for mapping to the destination type's {@code setAge} method to be <i>skipped</i>. If the
 * condition does not apply, mapping will occur from the the source type's {@code getAge} method.
 * 
 * <pre>    when(Conditions.isNull).skip().setAge(source.getAge());</pre>
 * 
 * <h3 id=6>Providers</h3>
 * <p>
 * This example specifies that the {@code nameProvider} {@link Provider} be used to provide
 * destination name instances when mapping the source type's {@code getName} method to the
 * destination type's {@code setName}.
 * 
 * <pre>    with(nameProvider).map().setName(source.getName());</pre>
 * 
 * <h3 id=7>String based mappings</h3>
 * <p>
 * As an alternative to mapping properties via their setters and getters, you can also map
 * properties using string references. While String based mappings are not refactoring-safe, they
 * allow flexibility when dealing with models that do not have getters or setters.
 * 
 * <pre>    map().getCustomer().setName(this.<String>source("person.name"));</pre>
 * 
 * Or alternatively:
 * 
 * <pre>    map(source("person.name")).getCustomer().setName(null);</pre>
 * 
 * @param <S> source type
 * @param <D> destination type
 * 
 * @author Jonathan Halterman
 */
public abstract class PropertyMap<S, D> {
  /**
   * The source instance to be used in a mapping declaration. See the <a href="#1">EDSL
   * examples</a>.
   * <p>
   * <b>Throws:</b> NullPointerException if dereferenced from outside the context of
   * {@link #configure()} .
   */
  public S source;
  /**
   * The destination instance to be used in a mapping declaration. See the <a href="#1">EDSL
   * examples</a>.
   * <p>
   * <b>Throws:</b> NullPointerException if dereferenced from outside the context of
   * {@link #configure()} .
   */
  public D destination;
  Class<D> destinationType;
  Class<S> sourceType;
  private ExplicitMappingBuilder<S, D> builder;

  /**
   * Creates a new PropertyMap for the source and destination types {@code S} and {@code D}.
   * 
   * @throws IllegalArgumentException if {@code S} and {@code D} are not declared
   */
  @SuppressWarnings("unchecked")
  protected PropertyMap() {
    Class<?>[] typeArguments = TypeResolver.resolveRawArguments(PropertyMap.class, getClass());
    Assert.notNull(typeArguments,
        "Must declare source type argument <S> and destination type argument <D> for PropertyMap");

    sourceType = (Class<S>) typeArguments[0];
    destinationType = (Class<D>) typeArguments[1];
  }

  /**
   * Creates a new PropertyMap for the {@code sourceType} and {@code destinationType}.
   */
  protected PropertyMap(Class<S> sourceType, Class<D> destinationType) {
    this.sourceType = sourceType;
    this.destinationType = destinationType;
  }

  /**
   * Called by ModelMapper to configure mappings as defined in the PropertyMap.
   */
  protected abstract void configure();

  /**
   * Defines a mapping to a destination. See the <a href="#0">EDSL examples</a>.
   * 
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  protected final D map() {
    assertBuilder();
    return builder.map();
  }

  /**
   * Defines a mapping for the {@code subject}. See the See the <a href="#0">EDSL examples</a>.
   * 
   * @param subject to map
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  protected final D map(Object subject) {
    assertBuilder();
    return builder.map(subject);
  }

  /**
   * Defines a mapping from the {@code source} to the {@code destination}. See the See the <a
   * href="#0">EDSL examples</a>.
   * 
   * @param source to map from
   * @param destination to map to
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  protected final void map(Object source, Object destination) {
    assertBuilder();
    builder.map(source, destination);
  }

  /**
   * Specifies that mapping for the destination property be skipped during the mapping process. See
   * the <a href="#3">EDSL examples</a>.
   * 
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  protected final D skip() {
    assertBuilder();
    return builder.skip();
  }

  /**
   * Specifies that mapping to the {@code destination} be skipped during the mapping process. See
   * the <a href="#3">EDSL examples</a>.
   * 
   * @param destination to skip
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  protected final void skip(Object destination) {
    assertBuilder();
    builder.skip(destination);
  }

  /**
   * Specifies that mapping from the {@code source} to the {@code destination} be skipped during the
   * mapping process. See the EDSL examples at {@link PropertyMap}. See the <a href="#3">EDSL
   * examples</a>.
   * 
   * @param source to skip
   * @param destination to skip
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  protected final void skip(Object source, Object destination) {
    assertBuilder();
    builder.skip(source, destination);
  }

  /**
   * Used for mapping a {@code sourcePropertyPath} to a destination. See the <a href="#7">EDSL
   * examples</a>.
   * 
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  protected <T> T source(String sourcePropertyPath) {
    assertBuilder();
    return builder.source(sourcePropertyPath);
  }

  /**
   * Specifies the {@code converter} to use for converting to the destination property hierarchy.
   * When used with deep mapping the {@code converter} should convert to an instance of the
   * <b>last</b> destination property. See the <a href="#4">EDSL examples</a>.
   * 
   * @param converter to use when mapping the property
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  protected final MapExpression<D> using(Converter<?, ?> converter) {
    assertBuilder();
    return builder.using(converter);
  }

  /**
   * Specifies the {@code condition} that must apply in order for mapping to take place for a
   * particular destination property hierarchy. See the <a href="#5">EDSL examples</a>.
   * 
   * @param condition that must apply when mapping the property
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  protected final ProviderExpression<S, D> when(Condition<?, ?> condition) {
    assertBuilder();
    return builder.when(condition);
  }

  /**
   * Specifies a provider to be used for providing instances of the mapped property. When used with
   * deep mapping the {@code provider} should provide an instance of the <b>last</b> destination
   * property. See the <a href="#6">EDSL examples</a>.
   * 
   * @param provider to use for providing the destination property
   * @throws IllegalStateException if called from outside the context of
   *           {@link PropertyMap#configure()}.
   */
  protected final ConverterExpression<S, D> with(Provider<?> provider) {
    assertBuilder();
    return builder.with(provider);
  }

  private void assertBuilder() {
    Assert.state(builder != null,
        "PropertyMap should not be used outside the context of PropertyMap.configure().");
  }

  @SuppressWarnings("unused")
  private synchronized void configure(ExplicitMappingBuilder<S, D> builder) {
    this.builder = builder;

    try {
      builder.visitPropertyMap(this);
      source = builder.source;
      destination = builder.destination;
      configure();
    } finally {
      this.builder = null;
      this.source = null;
    }
  }
}
