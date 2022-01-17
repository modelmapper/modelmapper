package org.modelmapper.internal.converter;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.util.Optional;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

@Test
public class OptionalConverterTest {
  private ModelMapper modelMapper;

  private static class Field {
    private String value;

    public Field(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }

  static class FieldDto {
    private String value;

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }

  @SuppressWarnings("all")
  static class Source {
    private Optional<Field> field;

    public Source(Optional<Field> field) {
      this.field = field;
    }

    public Optional<Field> getField() {
      return field;
    }

    public void setField(Optional<Field> field) {
      this.field = field;
    }
  }

  @SuppressWarnings("all")
  static class Destination {
    private Optional<FieldDto> field;

    public Optional<FieldDto> getField() {
      return field;
    }

    public void setField(Optional<FieldDto> field) {
      this.field = field;
    }
  }

  @BeforeMethod
  public void setUp() {
    modelMapper = new ModelMapper();

    Converter<String, String> upperCase = ctx -> ctx.getSource().toUpperCase();
    modelMapper.emptyTypeMap(Field.class, FieldDto.class).addMappings(
        mapper -> mapper.using(upperCase).map(Field::getValue, FieldDto::setValue));
  }


  public void shouldMapOptionalStringToOptionalString() {
    assertEquals(modelMapper.map(Optional.of("foo"), Optional.class), Optional.of("foo"));
    assertEquals(modelMapper.map(Optional.empty(), Optional.class), Optional.empty());
  }

  public void shouldMapOptionalStringToOptionalInteger() {
    Type type = new TypeToken<Optional<Integer>>() {}.getType();
    assertEquals(modelMapper.map(Optional.of("100"), type), Optional.of(100));
  }

  @SuppressWarnings("all")
  public void shouldMapOptionalPropertyToOtherOptionalProperty() {
    Destination destination = modelMapper.map(new Source(Optional.of(new Field("foo"))), Destination.class);
    assertEquals(destination.getField().get().getValue(), "FOO");
  }

  public void shouldMapNullToNull() {
    Destination destination = modelMapper.map(new Source(null), Destination.class);
    assertNull(destination.getField());
  }
}
