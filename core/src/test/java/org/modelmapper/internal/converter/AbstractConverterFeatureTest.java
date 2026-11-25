package org.modelmapper.internal.converter;


import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.InheritingConfiguration;
import org.modelmapper.internal.MappingEngineImpl;
import org.modelmapper.internal.converter.dto.ChildDTO;
import org.modelmapper.internal.converter.dto.ChildEntity;
import org.modelmapper.internal.converter.dto.ParentDTO;
import org.modelmapper.internal.converter.dto.ParentEntity;
import org.modelmapper.spi.MappingEngine;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AbstractConverterFeatureTest {

  protected ModelMapper modelMapper;
  protected InheritingConfiguration config = new InheritingConfiguration();
  private MappingEngine engine = new MappingEngineImpl(config);

  @BeforeMethod
  protected void init() {
    modelMapper = new ModelMapper();
    modelMapper.addConverter(new ParentConverter());
    modelMapper.addConverter(new ChildConverter());
  }


  @Test
  public void testConverterFeature() {
    ParentEntity entity = getParentTest();
    ParentDTO dto = modelMapper.map(entity, ParentDTO.class);
    Assert.assertEquals(dto.getName(), entity.getName());
    Assert.assertEquals(dto.getSecondName(), entity.getSecondName());
    Assert.assertEquals(dto.getChild().getChildName(), entity.getChild().getName());

  }


  private ParentEntity getParentTest() {
    return new ParentEntity("parent", "second", new ChildEntity("child"));
  }

  private class ParentConverter extends AbstractConverter<ParentEntity, ParentDTO> {

    @Override
    protected ParentDTO convert(ParentEntity source) {
      ParentDTO dto = new ParentDTO();
      dto.setSecondName(source.getSecondName());
      dto.setName(source.getName());
      dto.setChild(map(source.getChild(), ChildDTO.class));
      return dto;
    }
  }

  private class ChildConverter extends AbstractConverter<ChildEntity, ChildDTO> {
    @Override
    protected ChildDTO convert(ChildEntity source) {
      ChildDTO dto = new ChildDTO();
      dto.setChildName(source.getName());
      return dto;
    }
  }


}
