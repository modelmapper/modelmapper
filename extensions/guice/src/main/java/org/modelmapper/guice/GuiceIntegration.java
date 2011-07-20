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
package org.modelmapper.guice;

import org.modelmapper.Provider;
import org.modelmapper.internal.util.Assert;

import com.google.inject.Injector;

/**
 * Integrates ModelMapper with Guice.
 * 
 * @author Jonathan Halterman
 */
public class GuiceIntegration {
  private GuiceIntegration() {
  }

  private static class GuiceProvider implements Provider<Object> {
    private final Injector injector;

    GuiceProvider(Injector injector) {
      this.injector = injector;
    }

    public Object get(ProvisionRequest<Object> request) {
      return injector.getInstance(request.getRequestedType());
    }
  }

  /**
   * Creates a Provider which gets delegates object provisioning to the {@code injector}.
   * 
   * @throws IllegalArgumentExceptions if {@code injector} is null
   */
  public static Provider<?> fromGuice(Injector injector) {
    Assert.notNull(injector);
    return new GuiceProvider(injector);
  }
}
