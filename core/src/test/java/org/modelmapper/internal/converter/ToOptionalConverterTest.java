package org.modelmapper.internal.converter;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.testng.Assert.assertEquals;

@Test
public class ToOptionalConverterTest {
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

  private static class FieldDto {
    private String value;

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }

  private static class Source {
    private Field field;

    public Source(Field field) {
      this.field = field;
    }

    public Field getField() {
      return field;
    }

    public void setField(Field field) {
      this.field = field;
    }
  }

  @SuppressWarnings("all")
  private static class Destination {
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


  @SuppressWarnings("all")
  public void shouldMapStringToOptional() {
    assertEquals(modelMapper.map("foo", Optional.class).get(), "foo");
  }

  public void shouldMapStringToOptionalInteger() {
    assertEquals(modelMapper.map("100", new TypeToken<Optional<Integer>>() {}.getType()), Optional.of(100));
  }

  @SuppressWarnings("all")
  public void shouldMapToOptionalProperty() {
    Destination destination = modelMapper.map(new Source(new Field("foo")), Destination.class);
    assertEquals(destination.getField().get().getValue(), "FOO");
  }
}
