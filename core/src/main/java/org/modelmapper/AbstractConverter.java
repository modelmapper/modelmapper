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
import org.modelmapper.internal.MappingEngineImpl;
import org.modelmapper.internal.util.Assert;
import org.modelmapper.internal.util.Types;
import org.modelmapper.spi.MappingContext;

/**
 * Converter support class. Allows for simpler Converter implementations.
 *
 * @param <S> source type
 * @param <D> destination type
 *
 * @author Jonathan Halterman
 */
public abstract class AbstractConverter<S, D> implements Converter<S, D> {

  private static MappingEngineImpl mappingEngine;

  /**
   * Delegates conversion to {@link #convert(Object)}.
   */
  public D convert(MappingContext<S, D> context) {
    if (mappingEngine == null) {
      mappingEngine = (MappingEngineImpl) context.getMappingEngine();
    }
    return convert(context.getSource());
  }

  @Override
  public String toString() {
    return String.format("Converter<%s, %s>",
            (Object[]) TypeResolver.resolveRawArguments(Converter.class, getClass()));
  }

  /**
   * Converts {@code source} to an instance of type {@code D}.
   */
  protected abstract D convert(S source);

  /**
   * @param source          - source object
   * @param destinationType - destination class
   */
  protected <Destination> Destination map(Object source, Class<Destination> destinationType) {
    Assert.notNull(source, "source");
    Assert.notNull(destinationType, "destinationType");
    Assert.notNull(mappingEngine, "mappingEngine");
    return mappingEngine.map(source, Types.deProxy(source.getClass()), null, TypeToken.<Destination>of(destinationType), null);
  }
}
