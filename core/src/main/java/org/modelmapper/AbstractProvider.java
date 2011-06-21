package org.modelmapper;

/**
 * Provider support class. Allows for simpler Provider implementations.
 * 
 * @param <T> type to provide
 * 
 * @author Jonathan Halterman
 */
public abstract class AbstractProvider<T> implements Provider<T> {
  /**
   * Delegates provisioning to {@link #get()}.
   */
  @Override
  public T get(ProvisionRequest<T> request) {
    return get();
  }

  /**
   * Provides an instance of type {@code T}.
   */
  protected abstract T get();
}
