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
package org.modelmapper.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.modelmapper.Provider.ProvisionRequest;
import org.modelmapper.TypeMap;
import org.modelmapper.internal.util.Assert;
import org.modelmapper.internal.util.Types;
import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.MappingEngine;

/**
 * MappingContext implementation that caches destination values for an object graph by their
 * corresponding Mutator.
 * 
 * @author Jonathan Halterman
 */
public class MappingContextImpl<S, D> implements MappingContext<S, D>, ProvisionRequest<D> {
  final Map<Mutator, Object> destinationCache;
  final Errors errors;
  private Set<Class<?>> currentlyMapping;
  private D destination;
  private final Class<D> destinationType;
  private Mapping mapping;
  private final MappingEngine mappingEngine;
  private final S source;
  private final Class<S> sourceType;
  private Object parentSource;
  private TypeMap<S, D> typeMap;
  private TypeMap<?, ?> parentTypeMap;
  /** Tracks destination hierarchy paths that were shaded by a condition */
  private final List<String> shadedPaths;

  /**
   * Create initial MappingContext.
   */
  public MappingContextImpl(S source, Class<S> sourceType, D destination, Class<D> destinationType,
      MappingEngine mappingEngine) {
    this.source = source;
    this.sourceType = sourceType;
    this.destination = destination;
    this.destinationType = destinationType;
    this.mappingEngine = mappingEngine;
    currentlyMapping = new HashSet<Class<?>>();
    errors = new Errors();
    destinationCache = new HashMap<Mutator, Object>();
    shadedPaths = new ArrayList<String>();
  }

  /**
   * Create child MappingContext. The mapping is no longer mapped to S and D in this scope.
   * 
   * @param isProperty indicates whether the context is being created for a property of
   *          {@code context.source}, which will copy destinationCache and shadedPaths from the
   *          given {@code context} to the new context
   */
  MappingContextImpl(MappingContextImpl<?, ?> context, S source, Class<S> sourceType,
      D destination, Class<D> destinationType, Mapping mapping, boolean isProperty) {
    this.source = source;
    this.sourceType = sourceType;
    this.destination = destination;
    this.destinationType = destinationType;
    this.typeMap = null;
    this.parentTypeMap = isProperty ? context.typeMap : null;
    this.mapping = mapping;
    parentSource = context.parentSource;
    mappingEngine = context.mappingEngine;
    currentlyMapping = context.currentlyMapping;
    errors = context.errors;
    destinationCache = isProperty ? context.destinationCache : new HashMap<Mutator, Object>();
    shadedPaths = isProperty ? context.shadedPaths : new ArrayList<String>();
  }

  public <CS, CD> MappingContext<CS, CD> create(CS source, Class<CD> destinationType) {
    Assert.notNull(source, "source");
    Assert.notNull(destinationType, "destinationType");
    return new MappingContextImpl<CS, CD>(this, source, Types.<CS>deProxy(source.getClass()), null,
        destinationType, mapping, false);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;

    MappingContextImpl<?, ?> other = (MappingContextImpl<?, ?>) obj;
    if (!source.equals(other.source))
      return false;
    if (!sourceType.equals(other.sourceType))
      return false;
    if (!destinationType.equals(other.destinationType))
      return false;
    return true;
  }

  public void finishedMapping(Class<?> type) {
    currentlyMapping.remove(type);
  }

  public D getDestination() {
    return destination;
  }

  public Class<D> getDestinationType() {
    return destinationType;
  }

  public Mapping getMapping() {
    return mapping;
  }

  public MappingEngine getMappingEngine() {
    return mappingEngine;
  }

  public Class<D> getRequestedType() {
    return destinationType;
  }

  public S getSource() {
    return source;
  }

  public Class<S> getSourceType() {
    return sourceType;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + source.hashCode();
    result = prime * result + sourceType.hashCode();
    result = prime * result + destinationType.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return String.format("MappingContext[%s -> %s]", source, Types.toString(destinationType));
  }

  public TypeMap<S, D> typeMap() {
    return typeMap;
  }

  /**
   * Marks {@code type} as currently being mapped.
   * 
   * @param type to mark as being mapped
   * @return boolean true if {@code type} is currently being mapped
   */
  boolean currentlyMapping(Class<?> type) {
    return !currentlyMapping.add(type);
  }

  Object parentSource() {
    return parentSource;
  }

  TypeMap<?, ?> parentTypeMap() {
    return parentTypeMap;
  }

  /**
   * Determines whether the {@code subpath} is shaded.
   */
  boolean isShaded(String subpath) {
    for (String shadedPath : shadedPaths)
      if (subpath.startsWith(shadedPath))
        return true;
    return false;
  }

  void setDestination(D destination) {
    this.destination = destination;
  }

  void setParentSource(Object parentSource) {
    this.parentSource = parentSource;
  }

  void setTypeMap(TypeMap<S, D> typeMap) {
    this.typeMap = typeMap;
  }

  /**
   * Shades the {@code path} such that subsequent subpaths can be skipped during the mapping
   * process.
   */
  void shadePath(String path) {
    shadedPaths.add(path);
  }
}
