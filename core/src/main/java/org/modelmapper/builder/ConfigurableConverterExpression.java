package org.modelmapper.builder;

import org.modelmapper.Converter;

public interface ConfigurableConverterExpression<S, D> extends ReferenceMapExpression<S, D> {

    /**
     * Uses {@code converter} to convert a source property to destination property
     *
     * <pre>
     * {@code
     *   using(converter).<String>map(Src::getCustomer, Dest::setCustomerId)
     *   using(ctx -> ctx.getSource().getName().toUpperCase()).<String>map(src -> src.getCustomer().getId(), Dest::setCustomerId)
     * }
     * </pre>
     *
     * @param converter a converter convert source property to destination property
     */
    ReferenceMapExpression<S, D> using(Converter<?, ?> converter);

}
