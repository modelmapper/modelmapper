/*
 * Copyright 2017 the original author or authors.
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

import static java.util.stream.Collectors.toList;
import static org.modelmapper.internal.ExplicitMappingBuilder.MappingOptions;
import static org.modelmapper.internal.util.Assert.notNull;

import java.util.List;
import net.jodah.typetools.TypeResolver;
import org.modelmapper.builder.ReferenceMapExpression;
import org.modelmapper.internal.util.Primitives;
import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.SourceGetter;

/**
 * {@link ReferenceMapExpression} implementation
 *
 * @param <S> source type
 * @param <D> destination type
 *
 * @author Chun Han Hsiao
 */
class ReferenceMapExpressionImpl<S, D> implements ReferenceMapExpression<S, D> {
  private final TypeMapImpl<S, D> typeMap;
  private final MappingOptions options;
  private final PropertyReferenceCollector collector;
  private final S source;
  private final D destination;

  ReferenceMapExpressionImpl(TypeMapImpl<S, D> typeMap) {
    this(typeMap, new MappingOptions());
  }

  ReferenceMapExpressionImpl(TypeMapImpl<S, D> typeMap, MappingOptions options) {
    this.typeMap = typeMap;
    this.options = options;
    this.collector = new PropertyReferenceCollector(typeMap.configuration, options);
    try {
      this.source = ProxyFactory.proxyFor(typeMap.getSourceType(), collector.newSourceInterceptor(),
          collector.getProxyErrors());
      this.destination = ProxyFactory
          .proxyFor(typeMap.getDestinationType(), collector.newDestinationInterceptor(),
              collector.getProxyErrors());
    } catch (ErrorsException e) {
      throw e.getErrors().toConfigurationException();
    }
  }

  @Override
  public <V> void map(SourceGetter<S> sourceGetter, DestinationSetter<D, V> destinationSetter) {
    visitSource(sourceGetter);
    visitDestination(destinationSetter);
    skipMapping(collector.collect());
    collector.reset();
  }

  @Override
  public <V> void skip(DestinationSetter<D, V> destinationSetter) {
    options.skipType = 1;
    visitDestination(destinationSetter);
    skipMapping(collector.collect());
    collector.reset();
  }

  private void skipMapping(MappingImpl skipMapping) {
    String prefix = skipMapping.getPath();
    List<String> conflictPaths = typeMap.getMappings().stream()
        .map(Mapping::getPath)
        .filter(path -> path.startsWith(prefix) && !path.equals(prefix))
        .collect(toList());
    if (conflictPaths.isEmpty()) {
      typeMap.addMapping(skipMapping);
    } else {
      collector.getErrors().skipConflict(skipMapping.getPath(), conflictPaths);
      collector.getErrors().throwConfigurationExceptionIfErrorsExist();
    }
  }

  @Override
  public <V> void skip(SourceGetter<S> sourceGetter, DestinationSetter<D, V> destinationSetter) {
    options.skipType = 1;
    visitSource(sourceGetter);
    visitDestination(destinationSetter);
    typeMap.addMapping(collector.collect());
    collector.reset();
  }

  private void visitSource(SourceGetter<S> sourceGetter) {
    notNull(sourceGetter, "sourceGetter");
    try {
      Object sourceProperty = sourceGetter.get(source);
      if (source == sourceProperty)
        collector.mapFromSource(typeMap.getSourceType());
      if (collector.isNoSourceGetter())
        collector.mapFromConstant(sourceProperty);
    } catch (NullPointerException e) {
      if (collector.getProxyErrors().hasErrors())
        throw collector.getProxyErrors().toException();
      throw e;
    } catch (ErrorsException e) {
      throw e.getErrors().toConfigurationException();
    }
  }

  private <V> void visitDestination(DestinationSetter<D, V> destinationSetter) {
    notNull(destinationSetter, "destinationSetter");
    try {
      destinationSetter.accept(destination, destinationValue(destinationSetter));
    } catch (NullPointerException e) {
      if (collector.getProxyErrors().hasErrors())
        throw collector.getProxyErrors().toException();
      throw e;
    } catch (ErrorsException e) {
      throw e.getErrors().toConfigurationException();
    }
  }

  private <V> V destinationValue(DestinationSetter<D, V> destinationSetter) {
    Class<?>[] typeArguments = TypeResolver.resolveRawArguments(DestinationSetter.class, destinationSetter.getClass());
    if (typeArguments != null) {
      Class<?> valueClass = typeArguments[1];
      if (Primitives.isPrimitive(valueClass)) {
        return Primitives.defaultValue(Primitives.primitiveFor(valueClass));
      }
    }
    return null;
  }
}
