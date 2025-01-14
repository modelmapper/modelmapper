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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.Provider;
import org.modelmapper.Provider.ProvisionRequest;
import org.modelmapper.TypeMap;
import org.modelmapper.TypeToken;
import org.modelmapper.internal.util.Assert;
import org.modelmapper.internal.util.Callable;
import org.modelmapper.internal.util.Objects;
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
  final Map<String, Object> intermediateDestinations;
  final Errors errors;
  private final MappingContextImpl<?, ?> parent;
  private D destination;
  /** Absolute path to destination. */
  final String destinationPath;
  private final Class<D> destinationType;
  private final Type genericDestinationType;
  private final String typeMapName;
  /** Whether requested mapping is to a provided destination object */
  private boolean providedDestination;
  private MappingImpl mapping;
  private final MappingEngineImpl mappingEngine;
  private final S source;
  private final Class<S> sourceType;
  private final SourceChain parentSource;
  private TypeMap<S, D> typeMap;
  /** Tracks destination hierarchy paths that were shaded by a condition */
  private final List<String> shadedPaths;

  /**
   * Create initial MappingContext.
   */
  public MappingContextImpl(S source, Class<S> sourceType, D destination, Class<D> destinationType,
      Type genericDestinationType, String typeMapName, MappingEngineImpl mappingEngine) {
    parent = null;
    this.source = source;
    this.sourceType = sourceType;
    this.parentSource = new SourceChain();
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
    intermediateDestinations = new HashMap<String, Object>();
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
    intermediateDestinations = new HashMap<String, Object>();
  }


  @Override
  public <CS, CD> MappingContext<CS, CD> create(CS source, CD destination) {
    Assert.notNull(source, "source");
    Assert.notNull(destination, "destination");

    return new MappingContextImpl<CS, CD>(this, source, Types.<CS>deProxiedClass(source),
        destination, Types.<CD>deProxiedClass(destination), null, mapping, false);
  }

  /** Creates a child MappingContext for an element of a destination collection. */
  @Override
  public <CS, CD> MappingContext<CS, CD> create(CS source, Class<CD> destinationType) {
    Assert.notNull(source, "source");
    Assert.notNull(destinationType, "destinationType");

    return new MappingContextImpl<CS, CD>(this, source, Types.<CS>deProxiedClass(source), null,
        destinationType, null, null, false);
  }

  /** Creates a child MappingContext for an element of a destination collection. */
  @SuppressWarnings("unchecked")
  @Override
  public <CS, CD> MappingContext<CS, CD> create(CS source, Type destinationType) {
    if (destinationType instanceof Class) {
      return create(source, (Class<CD>) destinationType);
    }
    Assert.notNull(source, "source");
    Assert.notNull(destinationType, "destinationType");
    TypeToken<CD> destinationTypeToken = TypeToken.of(destinationType);

    return new MappingContextImpl<CS, CD>(this, source, Types.<CS>deProxiedClass(source), null,
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

  @Override
  public D getDestination() {
    return destination;
  }

  @Override
  public Class<D> getDestinationType() {
    return destinationType;
  }

  @Override
  public Type getGenericDestinationType() {
    return genericDestinationType;
  }

  @Override
  public Mapping getMapping() {
    return mapping;
  }

  @Override
  public MappingEngine getMappingEngine() {
    return mappingEngine;
  }

  @Override
  public MappingContext<?, ?> getParent() {
    return parent;
  }

  @Override
  public Class<D> getRequestedType() {
    return destinationType;
  }

  @Override
  public S getSource() {
    return source;
  }

  @Override
  public Class<S> getSourceType() {
    return sourceType;
  }

  @Override
  public TypeMap<S, D> getTypeMap() {
    return typeMap;
  }

  @Override
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
   * Determines whether the {@code subPath} is shaded.
   */
  boolean isShaded(String subPath) {
    for (String shadedPath : shadedPaths)
      if (subPath.startsWith(shadedPath))
        return true;
    return false;
  }

  TypeMap<?, ?> parentTypeMap() {
    return parent == null ? null : parent.typeMap;
  }

  void setDestination(D destination, boolean trackForSource) {
    this.destination = destination;
    if (trackForSource && !Primitives.isPrimitiveWrapper(sourceType))
      sourceToDestination.put(source, destination);
  }

  void addParentSource(String path, Object parentSource) {
    this.parentSource.addSource(path, parentSource);
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

  Type genericDestinationPropertyType(Type type) {
    if (type == null
        || !(type instanceof ParameterizedType)
        || genericDestinationType == null
        || destinationType.getTypeParameters().length == 0)
      return null;

    ParameterizedType parameterizedType = (ParameterizedType) type;
    if (parameterizedType.getActualTypeArguments().length == 0)
      return null;

    if (destinationType.getTypeParameters()[0] == parameterizedType.getActualTypeArguments()[0])
      return genericDestinationType;
    return null;
  }

  @SuppressWarnings("all")
  <S, D> Object getParentDestination() {
    List<Mutator> mutatorChain = (List<Mutator>) mapping.getDestinationProperties();
    StringBuilder destPathBuilder = new StringBuilder().append(parent.destinationPath);
    Object current = parent.destination;
    for (int i = 0; i < mutatorChain.size() - 1; i++) {
      if (current == null)
        break;
      Mutator mutator = mutatorChain.get(i);
      String destPath = destPathBuilder.append(mutator.getName()).append('.').toString();
      Object source = parent.parentSource.getSource(destPath);
      Object next = Objects.firstNonNull(
          Objects.callable(parent.destinationCache.get(destPath)),
          parent.getCyclicReferenceByPath(destPath),
          parent.getDestinationValueByMemberName(current, mutator.getName()));
      if (next == null && source != null)
        next = mappingEngine.createDestinationViaGlobalProvider(
            parent.parentSource.getSource(destPath), mutator.getType(), parent.errors);

      if (next != null) {
        mutator.setValue(current, next);
        parent.destinationCache.put(destPath, next);
      }
      current = next;
    }
    return current;
  }

  private Callable<Object> getDestinationValueByMemberName(final Object current, final String memberName) {
    return new Callable<Object>() {
      @Override
      public Object call() {
        if (providedDestination) {
          Accessor accessor = TypeInfoRegistry
              .typeInfoFor(current.getClass(), mappingEngine.getConfiguration())
              .getAccessors()
              .get(memberName);
          if (accessor != null)
            return accessor.getValue(current);
        }
        return null;
      }
    };
  }

  Callable<Object> getCyclicReferenceByPath(final String destinationPath) {
    return new Callable<Object>() {
      @Override
      public Object call() {
        return intermediateDestinations.get(destinationPath);
      }
    };
  }

  /**
   * Returns a new MappingContext  with destination object  creating via a provider . The provider will
   * be Mapping's provider used first, else the TypeMap's property provider, else the TypeMap's provider,
   * else the configuration's  provider. Returns {@code this} if there is no provider.
   */
  @SuppressWarnings("unchecked")
  D createDestinationViaProvider() {
    Provider<D> provider = null;
    if (getMapping() != null) {
      provider = (Provider<D>) getMapping().getProvider();
      if (provider == null && parentTypeMap() != null)
        provider = (Provider<D>) parentTypeMap().getPropertyProvider();
    }
    if (provider == null && getTypeMap() != null)
      provider = getTypeMap().getProvider();
    if (provider == null && mappingEngine.getConfiguration().getProvider() != null)
      provider = (Provider<D>) mappingEngine.getConfiguration().getProvider();
    if (provider == null)
      return null;

    D destination = provider.get(this);
    mappingEngine.validateDestination(destinationType, destination, errors);
    setDestination(destination, false);
    return destination;
  }

  public boolean isProvidedDestination() {
    return providedDestination;
  }

  private static class SourceChain {
    private final Map<String, Object> sources = new HashMap<String, Object>();
    private Object lastSource;

    public void addSource(String path, Object source) {
      sources.put(path, source);
      lastSource = source;
    }

    public Object getSource(String path) {
      Object source = sources.get(path);
      if (source == null)
        source = lastSource;
      return source;
    }
  }
}
