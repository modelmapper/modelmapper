package org.modelmapper.internal.valuemutate;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.modelmapper.Provider;
import org.modelmapper.TypeToken;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test
public class MapValueWriterTest extends AbstractTest {
  static class Source {
    String src;

    public Source(String src) {
      this.src = src;
    }
  }

  public void shouldMap() {
    modelMapper.addMappings(new PropertyMap<Source, Map<String, Object>>() {
      @Override
      protected void configure() {
        map(source("src"), destination("dest"));
      }
    }).setProvider(new Provider<Map<String, Object>>() {
      @Override
      public Map<String, Object> get(ProvisionRequest<Map<String, Object>> request) {
        return new HashMap<String, Object>();
      }
    });
    Map<String, Object> destination = modelMapper.map(new Source("test"),
        new TypeToken<Map<String, Object>>() {}.getType());
    assertEquals(destination.get("dest"), "test");
  }
}
