package org.modelmapper.jackson;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.Collection;
import org.modelmapper.internal.util.MappingContextHelper;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;

/**
 * Converts {@link ArrayNode} instances to {@link Collection} instances.
 *
 * @author Chun Han Hsiao
 */
public class ArrayNodeToCollectionConverter implements ConditionalConverter<ArrayNode, Collection<Object>> {
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

    Collection<Object> destination = context.getDestination() == null
        ? MappingContextHelper.createCollection(context)
        : context.getDestination();
    Class<?> elementType = MappingContextHelper.resolveDestinationGenericType(context);

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
}
