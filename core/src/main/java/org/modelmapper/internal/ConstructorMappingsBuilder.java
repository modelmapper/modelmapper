package org.modelmapper.internal;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.modelmapper.internal.PropertyInfoImpl.ConstructorMutator;
import org.modelmapper.internal.util.Assert;
import org.modelmapper.spi.Mapping;

class ConstructorMappingsBuilder {

  private final Map<Constructor<?>, Destination> destinations = new HashMap<>();

  public void update(final Mapping mapping, final Object value) {
    Assert.isTrue(mapping.isConstructor(), "Mapping must be for a constructor");
    ConstructorMutator mutator = (ConstructorMutator) mapping.getLastDestinationProperty();
    Destination destination = destinations.computeIfAbsent(mutator.member, Destination::new);
    destination.params[mutator.getParam().getIndex()] = value;
  }

  @SuppressWarnings("unchecked")
  public <T> T instantiate(Class<T> type, Errors errors) {
    Assert.isTrue(destinations.size() == 1, "Only one destination expected");
    try {
      Destination destination = destinations.values().iterator().next();
      if (!destination.constructor.isAccessible())
        destination.constructor.setAccessible(true);
      return (T) destination.constructor.newInstance(destination.params);
    } catch (Exception e) {
      errors.errorInstantiatingDestination(type, e);
      return null;
    }
  }

  private static class Destination {
    private final Constructor<?> constructor;
    private final Object[] params;

    public Destination(final Constructor<?> constructor) {
      this.constructor = constructor;
      this.params = new Object[constructor.getParameterCount()];
    }
  }
}
