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

import static org.modelmapper.internal.ExplicitMappingBuilder.MappingOptions;
import static org.modelmapper.internal.util.Assert.notNull;

import net.jodah.typetools.TypeResolver;
import org.modelmapper.builder.ReferenceMapExpression;
import org.modelmapper.internal.util.Primitives;
import org.modelmapper.internal.util.Stack;
import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.spi.PropertyInfo;
import org.modelmapper.spi.SourceGetter;

import java.util.ArrayList;
import java.util.Map;

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
    typeMap.addMapping(collector.collect());
    collector.reset();
  }

  @Override
  public <V> void skip(DestinationSetter<D, V> destinationSetter) {
    options.skipType = 1;
    visitDestination(destinationSetter);
    addMapper();
    collector.reset();
  }

  public <V> void addMapper(){
    Stack<Mutator> stack = new Stack<>();
    ArrayList<Mutator> mutators = new ArrayList<>();
    ArrayList<Mutator> visited = new ArrayList<>();
    for (PropertyInfo mutator : collector.collect().getDestinationProperties()) {
      stack.push((Mutator) mutator);
      mutators.add((Mutator) mutator);
      visited.add((Mutator) mutator);
    }
    while(!stack.isEmpty()){
      Mutator mutator = stack.peek();
      TypeInfo<?> typeInfo = mutator.getTypeInfo(this.typeMap.configuration);
      if (typeInfo.getMutators().size() != 0){
        boolean haveLeaf = false;
        for (Map.Entry<String, Mutator> entry : typeInfo.getMutators().entrySet()) {
          mutator = entry.getValue();
          if (!visited.contains(mutator)){
            stack.push(mutator);
            mutators.add(mutator);
            visited.add(mutator);
            haveLeaf = true;
            break;
          }
        }
        if (!haveLeaf){
          mutators.remove(stack.pop());
        }
      } else {
        typeMap.addMapping(new ConstantMappingImpl(null, new ArrayList<Mutator>(mutators), options));
        mutators.remove(stack.pop());
      }
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
