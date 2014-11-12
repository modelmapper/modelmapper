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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.Provider.ProvisionRequest;
import org.modelmapper.TypeMap;
import org.modelmapper.TypeToken;
import org.modelmapper.internal.util.Assert;
import org.modelmapper.internal.util.Primitives;
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
  /** Caches previously mapped destination objects by path. */
  final Map<String, Object> destinationCache;
  /** Tracks destination objects for each source. Used for circular mapping. */
  final Map<Object, Object> sourceToDestination;
  /** Tracks intermediate destination objects on the path to the destination */
  final List<Object> intermediateDestinations;
  final Errors errors;
  private final MappingContextImpl<?, ?> parent;
  private D destination;
  /** Absolute path to destination. */
  final String destinationPath;
  private final Class<D> destinationType;
  private final Type genericDestinationType;
  private final String typeMapName;
  /** Whether requested mapping is to a provided destination object */
  final boolean providedDestination;
  private MappingImpl mapping;
  private final MappingEngine mappingEngine;
  private final S source;
  private final Class<S> sourceType;
  private Object parentSource;
  private TypeMap<S, D> typeMap;
  /** Tracks destination hierarchy paths that were shaded by a condition */
  private final List<String> shadedPaths;

  /**
   * Create initial MappingContext.
   */
  public MappingContextImpl(S source, Class<S> sourceType, D destination, Class<D> destinationType,
      Type genericDestinationType, String typeMapName, MappingEngine mappingEngine) {
    parent = null;
    this.source = source;
    this.sourceType = sourceType;
    this.destination = destination;
    this.destinationPath = "";
    this.destinationType = destinationType;
    this.genericDestinationType = genericDestinationType == null ? destinationType
        : genericDestinationType;
    this.typeMapName = typeMapName;
    providedDestination = destination != null;
    this.mappingEngine = mappingEngine;
    errors = new Errors();
    destinationCache = new HashMap<String, Object>();
    shadedPaths = new ArrayList<String>();
    sourceToDestination = new IdentityHashMap<Object, Object>();
    intermediateDestinations = new ArrayList<Object>();
  }

  /**
   * Create derived MappingContext.
   * 
   * @param inheritValues whether values from the source {@code context} should be inherited
   */
  MappingContextImpl(MappingContextImpl<?, ?> context, S source, Class<S> sourceType,
      D destination, Class<D> destinationType, Type genericDestinationType, MappingImpl mapping,
      boolean inheritValues) {
    this.parent = context;
    this.source = source;
    this.sourceType = sourceType;
    this.destination = destination;
    this.destinationPath = mapping == null ? context.destinationPath : context.destinationPath
        + mapping.getPath();
    this.destinationType = destinationType;
    this.genericDestinationType = genericDestinationType == null ? destinationType
        : genericDestinationType;
    this.providedDestination = context.providedDestination;
    this.typeMap = null;
    this.typeMapName = null;
    this.mapping = mapping;
    parentSource = context.parentSource;
    mappingEngine = context.mappingEngine;
    errors = context.errors;
    destinationCache = inheritValues ? context.destinationCache : new HashMap<String, Object>();
    shadedPaths = inheritValues ? context.shadedPaths : new ArrayList<String>();
    sourceToDestination = context.sourceToDestination;
    intermediateDestinations = new ArrayList<Object>();
  }

  public <CS, CD> MappingContext<CS, CD> create(CS source, CD destination) {
    Assert.notNull(source, "source");
    Assert.notNull(destination, "destination");

    return new MappingContextImpl<CS, CD>(this, source, Types.<CS>deProxy(source.getClass()),
        destination, Types.<CD>deProxy(destination.getClass()), null, mapping, false);
  }

  /** Creates a child MappingContext for an element of a destination collection. */
  public <CS, CD> MappingContext<CS, CD> create(CS source, Class<CD> destinationType) {
    Assert.notNull(source, "source");
    Assert.notNull(destinationType, "destinationType");

    return new MappingContextImpl<CS, CD>(this, source, Types.<CS>deProxy(source.getClass()), null,
        destinationType, null, null, false);
  }

  /** Creates a child MappingContext for an element of a destination collection. */
  public <CS, CD> MappingContext<CS, CD> create(CS source, Type destinationType) {
    Assert.notNull(source, "source");
    Assert.notNull(destinationType, "destinationType");
    TypeToken<CD> destinationTypeToken = TypeToken.<CD>of(destinationType);

    return new MappingContextImpl<CS, CD>(this, source, Types.<CS>deProxy(source.getClass()), null,
        destinationTypeToken.getRawType(), destinationTypeToken.getType(), mapping, false);
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

  public D getDestination() {
    return destination;
  }

  public Class<D> getDestinationType() {
    return destinationType;
  }

  public Type getGenericDestinationType() {
    return genericDestinationType;
  }

  public Mapping getMapping() {
    return mapping;
  }

  public MappingEngine getMappingEngine() {
    return mappingEngine;
  }

  public MappingContext<?, ?> getParent() {
    return parent;
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

  public TypeMap<S, D> getTypeMap() {
    return typeMap;
  }

  public String getTypeMapName() {
    return typeMapName;
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
    return String.format("MappingContext[%s -> %s]", sourceType.getSimpleName(),
        destinationType.getSimpleName());
  }

  @SuppressWarnings("unchecked")
  D destinationForSource() {
    return (D) sourceToDestination.get(source);
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

  Object parentSource() {
    return parentSource;
  }

  TypeMap<?, ?> parentTypeMap() {
    return parent == null ? null : parent.typeMap;
  }

  void setDestination(D destination, boolean trackForSource) {
    this.destination = destination;
    if (trackForSource && !Primitives.isPrimitiveWrapper(sourceType))
      sourceToDestination.put(source, destination);
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
