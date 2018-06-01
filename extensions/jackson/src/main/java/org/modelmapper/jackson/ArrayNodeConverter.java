package org.modelmapper.jackson;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import org.modelmapper.internal.util.MappingContextHelper;
import org.modelmapper.internal.util.Types;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.PropertyInfo;
import org.modelmapper.spi.PropertyMapping;

import com.fasterxml.jackson.databind.node.ArrayNode;

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
    Class<?> elementType = MappingContextHelper.resolveCollectionElementType(context);

    for (Object sourceElement : source) {
      Object element = null;
      if (sourceElement != null) {
        MappingContext<?, ?> elementContext = context.create(sourceElement, elementType);
        element = context.getMappingEngine().map(elementContext);
      }
      destination.add(element);
    }

    return destination;
  }

  private Collection<Object> createDestination(
      MappingContext<ArrayNode, Collection<Object>> context, int length) {
    return MappingContextHelper.createCollection(context, length);
  }
}
