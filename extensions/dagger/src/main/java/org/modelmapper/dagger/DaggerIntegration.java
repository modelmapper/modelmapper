/*
 * Copyright 2013 the original author or authors.
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
package org.modelmapper.dagger;

import org.modelmapper.Provider;
import org.modelmapper.internal.util.Assert;

import dagger.ObjectGraph;

/**
 * Integrates ModelMapper with Dagger.
 * 
 * @author Jonathan Halterman
 */
public class DaggerIntegration {
  private DaggerIntegration() {
  }

  private static class DaggerProvider implements Provider<Object> {
    private final ObjectGraph objectGraph;

    DaggerProvider(ObjectGraph objectGraph) {
      this.objectGraph = objectGraph;
    }

    public Object get(ProvisionRequest<Object> request) {
      return objectGraph.get(request.getRequestedType());
    }
  }

  /**
   * Creates a Provider which gets delegates object provisioning to the {@code objectGraph}.
   * 
   * @throws IllegalArgumentExceptions if {@code objectGraph} is null
   */
  public static Provider<?> fromDagger(ObjectGraph objectGraph) {
    Assert.notNull(objectGraph);
    return new DaggerProvider(objectGraph);
  }
}
