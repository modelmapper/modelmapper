package org.modelmapper.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;

public class PrimitiveJsonNodeConverter implements ConditionalConverter<JsonNode, Object> {
  @Override
  public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
    return (JsonNode.class.isAssignableFrom(sourceType)
        && !ContainerNode.class.isAssignableFrom(sourceType))
        ? MatchResult.FULL : MatchResult.NONE;
  }

  @Override
  public Object convert(MappingContext<JsonNode, Object> context) {
    JsonNode source = context.getSource();
    if (source == null)
      return null;

    if (source.isNumber()) {
      MappingContext<?, ?> mappingContext = context.create(source.numberValue(), context.getDestinationType());
      return context.getMappingEngine().map(mappingContext);
    } else {
      MappingContext<?, ?> mappingContext = context.create(source.textValue(), context.getDestinationType());
      return context.getMappingEngine().map(mappingContext);
    }
  }
}
