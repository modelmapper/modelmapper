package org.modelmapper.internal.util;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.PropertyInfo;
import org.modelmapper.spi.PropertyMapping;

import net.jodah.typetools.TypeResolver;

/**
 *  Utility class for creating destinations
 */
public final class MappingContextHelper {
  private MappingContextHelper() {
  }

  /**
   * Creates a collection based on the destination type.
   *
   * <ul>
   *   <li>Creates {@code TreeSet} for {@code SortedSet}</li>
   *   <li>Creates {@code HashSet} for {@code Set}</li>
   *   <li>Creates {@code ArrayList} for {@code List}</li>
   * </ul>
   *
   * @param context the mapping context
   * @param <T> the element type of the collection
   * @return an empty collection
   */
  public static <T> Collection<T> createCollection(MappingContext<?, Collection<T>> context) {
      if (context.getDestinationType().isInterface())
        if (SortedSet.class.isAssignableFrom(context.getDestinationType()))
          return new TreeSet<T>();
        else if (Set.class.isAssignableFrom(context.getDestinationType()))
          return new HashSet<T>();
        else
          return new ArrayList<T>();
      return context.getMappingEngine().createDestination(context);
  }

  public static Class<?> resolveDestinationGenericType(MappingContext<?, ?> context) {
    Mapping mapping = context.getMapping();

    if (mapping instanceof PropertyMapping) {
      PropertyInfo destInfo = mapping.getLastDestinationProperty();
      Class<?> elementType = TypeResolver.resolveRawArgument(destInfo.getGenericType(),
          destInfo.getInitialType());
      if (elementType != TypeResolver.Unknown.class)
        return elementType;
    }

    if (context.getGenericDestinationType() instanceof ParameterizedType)
      return Types.rawTypeFor(((ParameterizedType) context.getGenericDestinationType()).getActualTypeArguments()[0]);

    return Object.class;
  }
}
