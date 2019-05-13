package org.modelmapper.builder;

import org.modelmapper.Provider;

public interface ConfigurableProviderExpression<S, D> extends ConfigurableConverterExpression<S, D> {

    /**
     * Uses {@code provider} to instantiate  an instance for destination property
     *
     * <pre>
     * {@code
     *   with(provider).<String>map(Src::getCustomer, Dest::setCustomer)
     *   with(req -> new Customer()).<Customer>map(Src::getCustomer, Dest::setCustomer)
     * }
     * </pre>
     *
     * @param provider a provider instantiate destination property
     */
    ConfigurableConverterExpression<S, D> with(Provider<?> provider);

}
