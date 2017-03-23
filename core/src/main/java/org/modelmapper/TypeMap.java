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

import java.util.List;

import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.PropertyInfo;
import org.modelmapper.spi.SourceGetter;

/**
 * Encapsulates mapping configuration for a source and destination type pair.
 * 
 * @param <S> source type
 * @param <D> destination type
 * 
 * @author Jonathan Halterman
 */
public interface TypeMap<S, D> {
  /**
   * Loads mappings from the {@code propertyMap} into the TypeMap. Mappings defined in the
   * {@code propertyMap} will override any implicit mappings for the same properties.
   * 
   * @param propertyMap from which mappings should be loaded
   * @throws IllegalArgumentException if {@code propertyMap} is null
   * @throws ConfigurationException if a configuration error occurs while adding mappings for the
   *           {@code propertyMap}
   */
  void addMappings(PropertyMap<S, D> propertyMap);

  /**
   * Returns the Condition that must apply for the source and destination in order for mapping to
   * take place, else {@code null} if no condition has been configured.
   * 
   * @see #setCondition(Condition)
   */
  Condition<?, ?> getCondition();

  /**
   * Returns the Converter configured for this TypeMap, else {@code null} if no Converter has been
   * configured.
   * 
   * @see #setConverter(Converter)
   */
  Converter<S, D> getConverter();

  /**
   * Returns the destination type for the TypeMap.
   */
  Class<D> getDestinationType();

  /**
   * Returns a snapshot of the TypeMap's mappings.
   * 
   * <p>
   * This method is part of the ModelMapper SPI.
   */
  List<Mapping> getMappings();

  /**
   * Returns the name of the TypeMap, else {@code null} if the TypeMap has no name.
   */
  String getName();

  /**
   * Returns the converter to be used after mapping between the source and destination types, else
   * {@code null} if no post-Converter has been configured.
   * 
   * @see #setPostConverter(Converter)
   */
  Converter<S, D> getPostConverter();

  /**
   * Returns the converter to be used before mapping between the source and destination types, else
   * {@code null} if no post-Converter has been configured.
   * 
   * @see #setPreConverter(Converter)
   */
  Converter<S, D> getPreConverter();

  /**
   * Returns the Condition that must apply in properties in this TypeMap to be mapped, else
   * {@code null} if no condition has been configured.
   * 
   * @see #setCondition(Condition)
   */
  Condition<?, ?> getPropertyCondition();

  /**
   * Returns the Converter used for converting properties in the TypeMap, else {@code null} if no
   * Converter has been configured.
   * 
   * @see #setPropertyConverter(Converter)
   */
  Converter<?, ?> getPropertyConverter();

  /**
   * Returns the Provider configured for this TypeMap, else {@code null} if no Provider has been
   * configured.
   * 
   * @see #setPropertyProvider(Provider)
   */
  Provider<?> getPropertyProvider();

  /**
   * Returns the Provider configured for this TypeMap, else {@code null} if no Provider has been
   * configured.
   * 
   * @see #setProvider(Provider)
   */
  Provider<D> getProvider();

  /**
   * Returns the source type for the TypeMap.
   */
  Class<S> getSourceType();

  /**
   * Returns a snapshot list of destination properties that do not have mappings defined, else empty
   * list if all destination properties are mapped.
   * 
   * <p>
   * This method is part of the ModelMapper SPI.
   */
  List<PropertyInfo> getUnmappedProperties();

  /**
   * Maps {@code source} to an instance of type {@code D}.
   * 
   * @param source object to map from
   * @return fully mapped instance of type {@code D}
   * @throws IllegalArgumentException if {@code source} is null
   * @throws MappingException if an error occurs while mapping
   */
  D map(S source);

  /**
   * Maps {@code source} to {@code destination}.
   * 
   * @param source object to map from
   * @param destination object to map to
   * @throws IllegalArgumentException if {@code source} or {@code destination} are null
   * @throws MappingException if an error occurs while mapping
   */
  void map(S source, D destination);

  /**
   * Sets the {@code condition} that must apply for the source and destination in order for mapping
   * to take place.
   * 
   * @throws IllegalArgumentException if {@code condition} is null
   */
  TypeMap<S, D> setCondition(Condition<?, ?> condition);

  /**
   * Sets the {@code converter} to be used for any conversion requests for the TypeMap's source to
   * destination type. This converter will be used in place of any mappings that have been added to
   * the TypeMap.
   * 
   * @throws IllegalArgumentException if {@code converter} is null
   */
  TypeMap<S, D> setConverter(Converter<S, D> converter);

  /**
   * Sets the {@code converter} to be used after mapping between the source and destination types.
   * 
   * @throws IllegalArgumentException if {@code converter} is null
   */
  TypeMap<S, D> setPostConverter(Converter<S, D> converter);

  /**
   * Sets the {@code converter} to be used before mapping between the source and destination types.
   * 
   * @throws IllegalArgumentException if {@code converter} is null
   */
  TypeMap<S, D> setPreConverter(Converter<S, D> converter);

  /**
   * Sets the {@code condition} that must apply in order for properties in this TypeMap to be
   * mapped. This is overridden by any conditions defined in a PropertyMap.
   * 
   * @throws IllegalArgumentException if {@code condition} is null
   */
  TypeMap<S, D> setPropertyCondition(Condition<?, ?> condition);

  /**
   * Sets the {@code converter} to be used for converting properties in the TypeMap. This is
   * overridden by any converters defined in a PropertyMap.
   * 
   * @throws IllegalArgumentException if {@code converter} is null
   */
  TypeMap<S, D> setPropertyConverter(Converter<?, ?> converter);

  /**
   * Sets the {@code provider} to be used for providing instances of properties during mapping. This
   * is overriden by any providers defined in a PropertyMap.
   * 
   * @throws IllegalArgumentException if {@code provider} is null
   */
  TypeMap<S, D> setPropertyProvider(Provider<?> provider);

  /**
   * Sets the {@code provider} to be used for providing instances of destination type {@code D}
   * during mapping.
   * 
   * @throws IllegalArgumentException if {@code provider} is null
   */
  TypeMap<S, D> setProvider(Provider<D> provider);

  /**
   * Validates that <b>every</b> top level destination property is mapped to one and only one source
   * property, or that a {@code Converter} was {@link #setConverter(Converter) set}. If not, a
   * ConfigurationException is thrown detailing any missing mappings.
   * 
   * @throws ValidationException if any TypeMaps contain unmapped properties
   */
  void validate();

  /**
   * Adds a mapping into {@code TypeMap} by defining {@code sourceGetter} -> {@code destinationSetter}
   *
   * <pre>
   * {@code
   *   typeMap.addMapping(Src::getA, Dest::setB);
   *   typeMap.<String>addMapping(src -> src.getC().getD(), (dest, value) -> dest.getE().setF(value))
   * }
   * </pre>
   *
   * @param sourceGetter source property getter
   * @param destinationSetter destination property setter
   * @param <V> type of destination property wants to be set
   */
  <V> TypeMap<S, D> addMapping(SourceGetter<S> sourceGetter, DestinationSetter<D, V> destinationSetter);

  /**
   * Add a mapping into {@code TypeMap} by defining a {@code mapper} action
   *
   * You can chain {@code addMappings} contains only one property mapping like
   * <pre>
   * {@code
   *   typeMap.addMappings(mapper -> mapper.<String>map(Src::getA, Dest::setB))
   *          .addMappings(mapper -> mapper.<String>skip(Dest::setB))
   *          .addMappings(mapper -> mapper.when(condition).<String>map(Src::getA, Dest::setB))
   *          .addMappings(mapper -> mapper.when(condition).<String>skip(Dest::setB))
   *          .addMappings(mapper -> mapper.using(converter).<String>map(Src::getA, Dest::setB))
   *          .addMappings(mapper -> mapper.with(provider).<String>map(Src::getA, Dest::setB));
   * }
   * </pre>
   *
   * Or you can define all property mappings in one {@code addMappings} like
   * <pre>
   * {@code
   *   typeMap.addMappings(mapper -> {
   *     mapper.<String>map(Src::getA, Dest::setB);
   *     mapper.<String>skip(Dest::setB);
   *     mapper.when(condition).<String>map(Src::getA, Dest::setB);
   *   });
   * }
   * </pre>
   *
   * @param mapper a mapper defined a mapping action
   * @return this typeMap
   */
  TypeMap<S, D> addMappings(ExpressionMap<S, D> mapper);

  /**
   * Constructs a new TypeMap derived from {@code this}. The derived TypeMap will includes
   * all {@code mappings} from the base {@link TypeMap}, but will NOT include {@code converter},
   * {@code condition}, and {@code provider} from the base {@link TypeMap}.
   *
   * @param sourceType source type
   * @param destinationType destination type
   * @param <DS> derived type of source class
   * @param <DD> derived type of destination class
   * @return this type map
   *
   * @throws IllegalArgumentException if {@code TypePair.of(sourceType, destinationType)} already defined
   *         in {@code modelMapper.getTypeMaps()}
   */
  <DS extends S, DD extends D> TypeMap<S, D> include(Class<DS> sourceType, Class<DD> destinationType);

  /**
   * Includes {@code mappings} from a base {@link TypeMap}.
   *
   * @param sourceType source type
   * @param destinationType destination type
   * @return this type map
   *
   * @throws IllegalArgumentException if {@code TypePair.of(sourceType, destinationType)} already defined
   *         in {@code modelMapper.getTypeMaps()}
   */
  TypeMap<S, D> includeBase(Class<? super S> sourceType, Class<? super D> destinationType);
}
