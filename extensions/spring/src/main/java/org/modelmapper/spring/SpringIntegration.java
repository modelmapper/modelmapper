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
package org.modelmapper.spring;

import org.modelmapper.Provider;
import org.modelmapper.internal.util.Assert;
import org.springframework.beans.factory.BeanFactory;

/**
 * Integrates ModelMapper with Spring.
 * 
 * @author Jonathan Halterman
 */
public class SpringIntegration {
  private SpringIntegration() {
  }

  private static class SpringProvider implements Provider<Object> {
    BeanFactory beanFactory;

    SpringProvider(BeanFactory beanFactory) {
      this.beanFactory = beanFactory;
    }

    public Object get(ProvisionRequest<Object> request) {
      return beanFactory.getBean(request.getRequestedType());
    }
  }

  /**
   * Creates a Provider which gets delegates object provisioning to the {@code beanFactory}.
   * 
   * @throws IllegalArgumentExceptions if {@code beanFactory} is null
   */
  public static Provider<?> fromSpring(BeanFactory beanFactory) {
    Assert.notNull(beanFactory);
    return new SpringProvider(beanFactory);
  }
}
