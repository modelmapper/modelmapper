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

    @Override
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
