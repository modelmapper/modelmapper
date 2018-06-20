package org.modelmapper.protobuf;

import com.google.protobuf.BoolValue;
import org.modelmapper.ModelMapper;
import org.modelmapper.protobuf.pojo.ProtoCommon;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
}
