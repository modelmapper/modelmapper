package org.modelmapper.spi;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.stream.IntStream;
import org.modelmapper.ConstructorParam;

import java.util.List;
import org.modelmapper.internal.util.Assert;

/**
 * Interface to implement the constructor injection
 */
public interface ConstructorInjector {
  /**
   * Get the constructor parameters for the destination type.
   *
   * @param destinationType the destination type
   * @return the constructor parameters
   */
  List<ConstructorParam> getParameters(Class<?> destinationType);

  /**
   * Check if the passed class support the injection.
   *
   * @param destinationType the destination type
   * @return true if the destination type is applicable, false otherwise
   */
  boolean isApplicable(Class<?> destinationType);

  static <T> ConstructorInjector forClass(Class<T> type, String... argNames) {
    return new DefaultConstructorInjector<>(type, argNames);
  }

  class DefaultConstructorInjector<T> implements ConstructorInjector {
    private final Class<T> type;
    private final List<ConstructorParam> params;

    private DefaultConstructorInjector(Class<T> type, String... argNames) {
      this.type = type;
      this.params = lookupParams(type, argNames);
    }

    @Override
    public List<ConstructorParam> getParameters(Class<?> destinationType) {
      Assert.isTrue(destinationType.equals(type),
          "Destination type %s is not applicable for this injector", destinationType.getName());
      return params;
    }

    @Override
    public boolean isApplicable(Class<?> destinationType) {
      return destinationType.equals(type);
    }

    private static List<ConstructorParam> lookupParams(Class<?> type, String... argNames) {
      Constructor<?>[] constructors = type.getConstructors();
      Assert.isTrue(constructors.length == 1,
          "Type %s must have exactly one public constructor", type.getName());
      Assert.isTrue(constructors[0].getParameterCount() > 0,
          "Type %s must have at least one constructor parameter", type.getName());

      Parameter[] params = constructors[0].getParameters();
      Assert.isTrue(argNames.length == 0 || argNames.length == params.length,
          "Number of provided argument names does not match number of constructor parameters for type %s",
          type.getName());

      return IntStream.range(0, params.length)
          .mapToObj(index -> new ConstructorParam(
              constructors[0],
              index,
              argNames.length == 0 ? params[index].getName() : argNames[index],
              params[index].getType()))
          .collect(toList());
    }
  }
}
