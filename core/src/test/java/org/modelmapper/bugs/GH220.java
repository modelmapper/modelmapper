package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

@Test
public class GH220 extends AbstractTest {
  static class CustomObject {
    private String value;

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }

  static class Source {
    private List<CustomObject> objects;

    public List<CustomObject> getObjects() {
      return objects;
    }

    public void setObjects(List<CustomObject> objects) {
      this.objects = objects;
    }
  }

  static class Destination {
    private CustomObject[] objects;

    public CustomObject[] getObjects() {
      return objects;
    }

    public void setObjects(CustomObject[] objects) {
      this.objects = objects;
    }
  }

  public void shouldMapNonPrimitiveArrayWithPropertyMap() throws Exception {
    Source source = new Source();
    CustomObject obj = new CustomObject();
    obj.setValue("value");

    List<CustomObject> objs = new ArrayList<CustomObject>();
    objs.add(obj);
    objs.add(obj);
    objs.add(obj);
    source.setObjects(objs);

    modelMapper.addMappings(new PropertyMap<Source, Destination>() {
      @Override
      protected void configure() {
        map(source.getObjects()).setObjects(null);

      }
    });
    modelMapper.validate();

    Destination result = modelMapper.map(source, Destination.class);

    assertNotNull(result.objects);
    assertEquals(result.objects.length, 3);
    assertEquals(result.objects[0], obj);
    assertEquals(result.objects[1], obj);
    assertEquals(result.objects[2], obj);
  }
}
