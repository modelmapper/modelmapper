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

    @Override
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
