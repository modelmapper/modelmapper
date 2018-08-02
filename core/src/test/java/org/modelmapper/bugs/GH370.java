package org.modelmapper.bugs;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Test
public class GH370 extends AbstractTest {
  static class Field {
  }

  static class SourceField extends Field {
    String value;

    public SourceField(String value) {
      this.value = value;
    }
  }

  static class DestField extends Field {
    String value;
  }

  static class BaseClass<T extends Field> {
    List<T> fields;
  }

  static class Destination extends BaseClass<DestField> {
    String name;
  }

  public void shouldMap() {
    List<SourceField> sourceFields = Collections.singletonList(new SourceField("foo"));
    Map<String, Object> source = new HashMap<String, Object>();
    source.put("fields", sourceFields);
    source.put("name", "bar");

    Destination destination = modelMapper.map(source, Destination.class);
    assertEquals(destination.name, "bar");
    assertNotNull(destination.fields);
    assertEquals(destination.fields.size(), 1);
    assertEquals(destination.fields.get(0).getClass(), DestField.class);
    assertEquals(destination.fields.get(0).value, "foo");
  }

  public void shouldMapWithWorkaround() {
    List<SourceField> sourceFields = Collections.singletonList(new SourceField("foo"));
    Map<String, Object> source = new HashMap<String, Object>();
    source.put("fields", sourceFields);
    source.put("name", "bar");

    modelMapper.createTypeMap(source, Destination.class)
        .addMappings(new PropertyMap<Map<String, Object>, Destination>() {
          @Override
          protected void configure() {
            map(source("fields"), destination("fields"));
          }
        });
    Destination destination = modelMapper.map(source, Destination.class);
    assertEquals(destination.name, "bar");
    assertNotNull(destination.fields);
    assertEquals(destination.fields.size(), 1);
    assertEquals(destination.fields.get(0).getClass(), DestField.class);
    assertEquals(destination.fields.get(0).value, "foo");
  }
}
