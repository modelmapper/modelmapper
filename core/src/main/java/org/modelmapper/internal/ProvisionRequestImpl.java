package org.modelmapper.internal;

import org.modelmapper.Provider.ProvisionRequest;

/**
 * @param <T> requested type
 * 
 * @author Jonathan Halterman
 */
class ProvisionRequestImpl<T extends Object> implements ProvisionRequest<T> {
  private final Class<T> requestedType;

  ProvisionRequestImpl(Class<T> requestedType) {
    this.requestedType = requestedType;
  }

  @Override
  public Class<T> getRequestedType() {
    return requestedType;
  }
}