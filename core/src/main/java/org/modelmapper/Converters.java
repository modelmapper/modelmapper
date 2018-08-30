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
   * Provides interface to chain another converter or convert to different destination type.
   *
   * @param <S> the source type
   * @param <D1> the intermediate destination type
   */
  public interface ChainableConverter<S, D1> {

    /**
     * Chains a converter of {@code S} -> {@code D1} to {@code S} -> {@code D2}.
     *
     * @param destinationType the final destination type
     * @param <D2> the final destination type
     * @return a converter
     */
    <D2> org.modelmapper.Converter<S, D2> to(Class<D2> destinationType);

    /**
     * Chains a converter of {@code S} -> {@code D1} to {@code S} -> {@code D2} with given converter.
     *
     * @param destinationType the final destination type
     * @param <D2> the final destination type
     * @return a converter
     */
    <D2> org.modelmapper.Converter<S, D2> map(org.modelmapper.Converter<D1, D2> converter);
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
    public static <S, D> org.modelmapper.Converter<java.util.Collection<S>, java.util.Collection<D>> map(final Converter<S, D> elementConverter) {
      return new org.modelmapper.Converter<java.util.Collection<S>, java.util.Collection<D>>() {
        @Override
        public java.util.Collection<D> convert(
            MappingContext<java.util.Collection<S>, java.util.Collection<D>> context) {
          if (context.getSource() == null)
            return null;

          java.util.Collection<D> destination = MappingContextHelper.createCollection(context);
          for (S element : context.getSource()) {
            destination.add(elementConverter.convert(element));
          }
          return destination;
        }
      };
    }

    /**
     * Provides a helper method to create a collection converter that will get the first element of source collect and convert it.
     *
     * <pre>
     *   using(Collection.first().to(Destination.class).map();
     * </pre>
     *
     * @param <S> the source type
     * @return a {@code ChainableConverter}
     */
    public static <S> ChainableConverter<java.util.Collection<S>, S> first() {
      return new ChainableConverter<java.util.Collection<S>, S>() {
        @Override
        public <D> org.modelmapper.Converter<java.util.Collection<S>, D> to(Class<D> destinationType) {
          return new org.modelmapper.Converter<java.util.Collection<S>, D>() {
            @Override
            public D convert(MappingContext<java.util.Collection<S>, D> context) {
              if (context.getSource() == null || context.getSource().isEmpty())
                return null;
              MappingContext<S, D> elementContext = context.create(context.getSource().iterator().next(),
                  context.getDestinationType());
              return context.getMappingEngine().map(elementContext);
            }
          };
        }

        @Override
        public <D> org.modelmapper.Converter<java.util.Collection<S>, D> map(
            final org.modelmapper.Converter<S, D> converter) {
          return new org.modelmapper.Converter<java.util.Collection<S>, D>() {
            @Override
            public D convert(MappingContext<java.util.Collection<S>, D> context) {
              if (context.getSource() == null || context.getSource().isEmpty())
                return null;
              MappingContext<S, D> elementContext = context.create(context.getSource().iterator().next(),
                  context.getDestinationType());
              return converter.convert(elementContext);
            }
          };
        }
      };
    }

    private Collection() {
    }
  }
}
