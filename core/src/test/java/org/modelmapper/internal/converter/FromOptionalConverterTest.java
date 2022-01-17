package org.modelmapper.internal.converter;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

@Test
public class FromOptionalConverterTest {
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

  @SuppressWarnings("all")
  private static class Source {
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

  private static class Destination {
    private FieldDto field;

    public FieldDto getField() {
      return field;
    }

    public void setField(FieldDto field) {
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


  public void shouldMapOptionalStringToString() {
    assertEquals(modelMapper.map(Optional.of("foo"), String.class), "foo");
    assertNull(modelMapper.map(Optional.empty(), String.class));
  }

  public void shouldMapOptionalStringToInteger() {
    assertEquals((int) modelMapper.map(Optional.of("100"), Integer.class), 100);
  }

  public void shouldMapOptionalProperty() {
    Destination destination = modelMapper.map(new Source(Optional.of(new Field("foo"))), Destination.class);
    assertEquals(destination.getField().getValue(), "FOO");
  }
}
