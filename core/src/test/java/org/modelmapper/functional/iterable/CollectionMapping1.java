package org.modelmapper.functional.iterable;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.AbstractTest;
import org.modelmapper.TypeToken;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class CollectionMapping1 extends AbstractTest {
  private List<Source> sources;

  public static class Source {
    String value;

    Source() {
    }

    Source(String value) {
      this.value = value;
    }
  }

  public static class Dest {
    Integer value;
  }

  @BeforeMethod
  protected void setup() {
    sources = new ArrayList<Source>();
    for (int i = 0; i < 5; i++)
      sources.add(new Source("" + i));
  }

  public void shouldMapToArray() {
    Dest[] dests = modelMapper.map(sources, Dest[].class);

    for (int i = 0; i < 5; i++)
      assertEquals(dests[i].value.toString(), sources.get(i).value);
  }

  public void shouldMapToTypeTokenizedArray() {
    Dest[] dests = modelMapper.map(sources, new TypeToken<Dest[]>() {
    }.getType());

    for (int i = 0; i < 5; i++)
      assertEquals(dests[i].value.toString(), sources.get(i).value);
  }

  public void shouldMapTypeTokenizedCollection() {
    List<Dest> dests = modelMapper.map(sources, new TypeToken<List<Dest>>() {
    }.getType());

    for (int i = 0; i < 5; i++)
      assertEquals(dests.get(i).value.toString(), sources.get(i).value);
  }
}
