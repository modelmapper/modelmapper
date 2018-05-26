package org.modelmapper.jackson;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.modelmapper.internal.util.Types;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.PropertyInfo;
import org.modelmapper.spi.PropertyMapping;

import com.fasterxml.jackson.databind.node.ArrayNode;

import net.jodah.typetools.TypeResolver;

/**
 * Converts {@link ArrayNode} instances to {@link Collection} instances.
 *
 * @author Chun Han Hsiao
 */
public class ArrayNodeConverter implements ConditionalConverter<ArrayNode, Collection<Object>> {
  @Override
  public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
    return Collection.class.isAssignableFrom(destinationType) && sourceType.isAssignableFrom(ArrayNode.class)
        ? MatchResult.FULL : MatchResult.NONE;
  }

  @Override
  public Collection<Object> convert(MappingContext<ArrayNode, Collection<Object>> context) {
    ArrayNode source = context.getSource();
    if (source == null)
      return null;

    int sourceLength = source.size();
    Collection<Object> destination = context.getDestination() == null ? createDestination(context, sourceLength)
        : context.getDestination();
    Class<?> elementType = getElementType(context);

    int index = 0;
    for (Object sourceElement : source) {
      Object element = null;
      if (sourceElement != null) {
        MappingContext<?, ?> elementContext = context.create(sourceElement, elementType);
        element = context.getMappingEngine().map(elementContext);
      }

      setElement(destination, element, index);
    }

    return destination;
  }

  private Collection<Object> createDestination(
      MappingContext<ArrayNode, Collection<Object>> context, int length) {
    if (context.getDestinationType().isInterface())
      if (SortedSet.class.isAssignableFrom(context.getDestinationType()))
        return new TreeSet<Object>();
      else if (Set.class.isAssignableFrom(context.getDestinationType()))
        return new HashSet<Object>();
      else
        return new ArrayList<Object>(length);

    return context.getMappingEngine().createDestination(context);
  }

  private Class<?> getElementType(MappingContext<ArrayNode, Collection<Object>> context) {
    Mapping mapping = context.getMapping();
    if (mapping instanceof PropertyMapping) {
      PropertyInfo destInfo = mapping.getLastDestinationProperty();
      Class<?> elementType = TypeResolver.resolveRawArgument(destInfo.getGenericType(),
          destInfo.getInitialType());
      return elementType == TypeResolver.Unknown.class ? Object.class : elementType;
    } else if (context.getGenericDestinationType() instanceof ParameterizedType)
      return Types.rawTypeFor(((ParameterizedType) context.getGenericDestinationType()).getActualTypeArguments()[0]);

    return Object.class;
  }

  private void setElement(Collection<Object> destination, Object element, int index) {
    destination.add(element);
  }
}
