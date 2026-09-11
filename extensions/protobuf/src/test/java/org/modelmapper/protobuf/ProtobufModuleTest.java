package org.modelmapper.protobuf;

import com.google.protobuf.BoolValue;
import java.util.HashMap;
import java.util.Map;
import org.modelmapper.protobuf.pojo.MapMessageProto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.protobuf.pojo.ProtoCommon;
import org.modelmapper.protobuf.pojo.TestMessageProto.TestMessage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

@Test
public class ProtobufModuleTest {
  static class BooleanDto {
    private Boolean someBoolValue;

    public Boolean getSomeBoolValue() {
      return someBoolValue;
    }

    public void setSomeBoolValue(final Boolean someBoolValue) {
      this.someBoolValue = someBoolValue;
    }
  }

  static class MessageDto {
    private String messageContent;

    public String getMessageContent() {
      return messageContent;
    }

    public void setMessageContent(String messageContent) {
      this.messageContent = messageContent;
    }
  }


  static class MapMessageDto {
    private String name;
    private Map<Integer, String> ids;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Map<Integer, String> getIds() {
      return ids;
    }

    public void setIds(Map<Integer, String> ids) {
      this.ids = ids;
    }
  }

  private ModelMapper modelMapper;

  @BeforeMethod
  public void setUp() {
    modelMapper = new ModelMapper();
    modelMapper.registerModule(new ProtobufModule());
  }

  public void shouldMapMessageToBuilder() {
    ProtoCommon.Property property = ProtoCommon.Property.newBuilder()
        .setPropertyId("propertyId")
        .setPropertyDetails(ProtoCommon.PropertyDetails.newBuilder()
            .setAddress("address")
            .setUtilities(ProtoCommon.PropertyDetails.Utilities.newBuilder()
                .setElectric(true)
                .setGas(true)
                .setWater(true)
                .setSewer(true)
                .setTrash(true)
                .setInternet(true)
                .setCable(true)))
        .build();

    ProtoCommon.AddPropertyRequest builder = modelMapper.map(property, ProtoCommon.AddPropertyRequest.Builder.class).build();

    assertTrue(builder.getPropertyDetails().getUtilities().getElectric());
    assertTrue(builder.getPropertyDetails().getUtilities().getGas());
    assertTrue(builder.getPropertyDetails().getUtilities().getSewer());
    assertTrue(builder.getPropertyDetails().getUtilities().getTrash());
    assertTrue(builder.getPropertyDetails().getUtilities().getInternet());
    assertTrue(builder.getPropertyDetails().getUtilities().getCable());
    assertTrue(builder.getPropertyDetails().getUtilities().getWater());
  }

  public void shouldNotMapBoolValueWithNull() {
    ProtoCommon.TestBoolValue testBoolValue = ProtoCommon.TestBoolValue.newBuilder().build();
    assertFalse(testBoolValue.hasSomeBoolValue());

    BooleanDto dto = modelMapper.map(testBoolValue, BooleanDto.class);
    assertNull(dto.getSomeBoolValue());
  }

  public void shouldNotMapToBoolValueWithNull() {
    BooleanDto booleanDto = new BooleanDto();
    booleanDto.setSomeBoolValue(null);

    ProtoCommon.TestBoolValue boolValueDto = modelMapper.map(booleanDto, ProtoCommon.TestBoolValue.Builder.class).build();
    assertFalse(boolValueDto.hasSomeBoolValue());
  }
  public void shouldMapTrueToDto() {
    ProtoCommon.TestBoolValue testBoolValue = ProtoCommon.TestBoolValue.newBuilder()
        .setSomeBoolValue(BoolValue.newBuilder().setValue(true).build())
        .build();

    BooleanDto booleanDto = modelMapper.map(testBoolValue, BooleanDto.class);

    assertTrue(booleanDto.getSomeBoolValue());
  }
  public void shouldMapTrueToBoolValue() {
    BooleanDto booleanDto = new BooleanDto();
    booleanDto.setSomeBoolValue(true);

    ProtoCommon.TestBoolValue testBoolValue = modelMapper.map(booleanDto, ProtoCommon.TestBoolValue.Builder.class).build();

    assertTrue(testBoolValue.getSomeBoolValue().getValue());
  }

  public void shouldMapJavaMapToProtobufMapField() {
    MapMessageDto dto = new MapMessageDto();
    dto.setName("test");
    Map<Integer, String> ids = new HashMap<Integer, String>();
    ids.put(1, "one");
    ids.put(2, "two");
    dto.setIds(ids);

    MapMessageProto.MapMessage message =
        modelMapper.map(dto, MapMessageProto.MapMessage.Builder.class).build();

    assertEquals(message.getName(), "test");
    assertEquals(message.getIdsMap().get(1), "one");
    assertEquals(message.getIdsMap().get(2), "two");
  }

  public void shouldMapNameWithUnderScore() {
    ModelMapper mapper = new ModelMapper().registerModule(new ProtobufModule());
    mapper.getConfiguration().setDestinationNameTokenizer(NameTokenizers.UNDERSCORE);

    MessageDto messageDto = new MessageDto();
    messageDto.setMessageContent("value");

    TestMessage message = mapper.map(messageDto, TestMessage.Builder.class).build();

    assertEquals(message.getMessageContent().getValue(), messageDto.getMessageContent());
  }
}
