package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * From https://groups.google.com/forum/#!topic/modelmapper/kz4fPVBcB5s
 */
@Test
public class GH60 extends AbstractTest {
  public void shouldMapCollectionsWithNullValues() {
    String[] initial = new String[] { "I", "will", "allow", null };

    assertEquals(modelMapper.map(initial, List.class), Arrays.asList(initial));
  }

  public void shouldMapMapsWithNullValues() {
    Map<String, String> initial = new HashMap<String, String>();
    initial.put("foo", "bar");
    initial.put("null", null);

    assertEquals(modelMapper.map(initial, Map.class), initial);
  }

  public void shouldMapMapsWithNullKeys() {
    Map<String, String> initial = new HashMap<String, String>();
    initial.put("foo", "bar");
    initial.put(null, "foo");

    assertEquals(modelMapper.map(initial, Map.class), initial);
  }
}
