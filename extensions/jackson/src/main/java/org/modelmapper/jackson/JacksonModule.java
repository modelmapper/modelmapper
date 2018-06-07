package org.modelmapper.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.Module;

/**
 * ModelMapper module for Jackson
 *
 * @author Chun Han Hsiao
 */
public class JacksonModule implements Module {
  private ObjectMapper objectMapper;

  public JacksonModule() {
    this(new ObjectMapper());
  }

  public JacksonModule(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void setupModule(ModelMapper modelMapper) {
    modelMapper.getConfiguration().addValueReader(new JsonNodeValueReader());
    modelMapper.getConfiguration().getConverters().add(0, new PrimitiveJsonNodeConverter());
    modelMapper.getConfiguration().getConverters().add(0, new ArrayNodeToCollectionConverter());
    modelMapper.getConfiguration().getConverters().add(0, new CollectionToArrayNodeConverter(objectMapper));
  }
}
