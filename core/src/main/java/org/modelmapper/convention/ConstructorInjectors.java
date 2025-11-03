package org.modelmapper.convention;

import org.modelmapper.ConstructorParam;
import org.modelmapper.spi.ConstructorInjector;

import java.util.ArrayList;
import java.util.List;

public class ConstructorInjectors {
  public static final ConstructorInjector NO_CONSTRUCTOR_INJECTORS = new ConstructorInjector() {
    @Override
    public List<ConstructorParam> getParameters(Class<?> destinationType) {
      return new ArrayList<>();
    }

    @Override
    public boolean isApplicable(Class<?> destinationType) {
      return false;
    }
  };
}
