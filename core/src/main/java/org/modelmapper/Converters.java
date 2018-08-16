/*
 * Copyright 2018 the original author or authors.
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

import org.modelmapper.internal.util.MappingContextHelper;
import org.modelmapper.spi.MappingContext;

/**
 * Provides helper methods to create converters.
 *
 * @see org.modelmapper.Converter
 */
public class Converters {
  private Converters() {
  }

  /**
   * Converts source to destination.
   *
   * @param <S> the source type
   * @param <D> the destination type
   */
  public interface Converter<S, D> {

    /**
     * Converts source to destination.
     *
     * @param source the source
     * @return the destination
     */
    D convert(S source);
  }

  /**
   * Provides helper methods to create converters for collection.
   */
  public static class Collection {
    /**
     * Provides a helper method to create a collection converter that will map each element based on the {@code elementConverter}.
     *
     * <pre>
     *   using(Collection.map(String::toUpperCase)).map();
     *   using(Collection.map(ele -> new Destination(ele)).map();
     * </pre>
     *
     * @param elementConverter the converter to map the source element to destination element
     * @param <S> the source element type
     * @param <D> the destination element type
     * @return a Converter
     */
    public static <S, D> org.modelmapper.Converter<java.util.Collection<S>, java.util.Collection<D>> map(Converter<S, D> elementConverter) {
      return new CollectionConverter<S, D>(elementConverter);
    }

    private Collection() {
    }
  }

  private static class CollectionConverter<S, D> implements org.modelmapper.Converter<java.util.Collection<S>, java.util.Collection<D>> {
    private Converter<S, D> converter;

    private CollectionConverter(Converter<S, D> converter) {
      this.converter = converter;
    }

    @Override
    public java.util.Collection<D> convert(MappingContext<java.util.Collection<S>, java.util.Collection<D>> context) {
      if (context.getSource() == null)
        return null;

      java.util.Collection<D> destination = MappingContextHelper.createCollection(context);
      for (S element : context.getSource()) {
        destination.add(converter.convert(element));
      }
      return destination;
    }
  }
}
