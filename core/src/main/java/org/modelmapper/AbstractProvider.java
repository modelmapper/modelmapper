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
  public T get(ProvisionRequest<T> request) {
    return get();
  }

  @Override
  public String toString() {
    return String.format("Provider<%s>", TypeResolver.resolveRawArgument(Provider.class, getClass()));
  }

  /**
   * Provides an instance of type {@code T}.
   */
  protected abstract T get();
}
