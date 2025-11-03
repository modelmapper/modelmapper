package org.modelmapper.spi;

import org.modelmapper.ConstructorParam;

import java.util.List;

/**
 * Interface to implement the constructor injection
 */
public interface ConstructorInjector {
  /**
   * Given a class retrieve the parameters with name
   * @param destinationType
   * @return
   */
  List<ConstructorParam> getParameters(Class<?> destinationType);

  /**
   * Check if the passed class support the injection
   * @param destinationType
   * @return
   */
  boolean isApplicable(Class<?> destinationType);
}
