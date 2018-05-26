package org.modelmapper.jackson;

import org.modelmapper.ModelMapper;
import org.modelmapper.Module;

/**
 * ModelMapper module for Jackson
 *
 * @author Chun Han Hsiao
 */
public class JacksonModule implements Module {
  @Override
  public void setupModule(ModelMapper modelMapper) {
    modelMapper.getConfiguration().addValueReader(new JsonNodeValueReader());
    modelMapper.getConfiguration().getConverters().add(new ArrayNodeConverter());
  }
}
