package org.modelmapper;

/**
 * Provides {@link PropertyMap} instances
 *
 * @param <PS> parent class or interface for source type
 * @param <PD> parent class or interface for destination type
 */
public interface PropertyMapProvider<PS, PD> {

  <S extends PS, D extends PD> PropertyMap<S, D> provide(
      Class<S> sourceType, Class<D> destinationType);
}
