package org.modelmapper.functional;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test(groups = "functional")
public class CollectionNullElements {
  public static class Source {
    private List<String> values;

    public List<String> getValues() {
      return values;
    }
  }

  public static class Destination {
    private List<String> values;

    public List<String> getValues() {
      return values;
    }

    public void setValues(List<String> values) {
      this.values = values;
    }
  }

  @DataProvider
  public Object[][] configurations() {
    return new Object[][] { { false, false }, { false, true }, { true, false }, { true, true } };
  }

  @Test(dataProvider = "configurations")
  public void shouldMapNullElements(boolean merge, boolean skipNull) {
    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration().setCollectionsMergeEnabled(merge).setSkipNullEnabled(skipNull);
    List<List<String>> sources = Arrays.asList(Arrays.asList(null, "new", "last"),
        Arrays.asList("new", null, "last"), Arrays.asList("new", "last", null),
        Arrays.<String>asList(null, null, null), Arrays.asList("new", "last", "end"));
    for (List<String> values : sources) {
      Source source = new Source();
      source.values = values;
      assertEquals(mapper.map(source, Destination.class).values, values);
      for (List<String> existing : Arrays.asList(Arrays.<String>asList(),
          Arrays.asList("old"), Arrays.asList("old", "stale", "obsolete"),
          Arrays.asList("old", "stale", "obsolete", "tail"))) {
        Destination destination = new Destination();
        destination.values = new ArrayList<String>(existing);
        mapper.map(source, destination);
        List<String> expected = new ArrayList<String>(values);
        if (merge && existing.size() > values.size())
          expected.addAll(existing.subList(values.size(), existing.size()));
        assertEquals(destination.values, expected);
      }
    }
  }

  public static class SourceItem {
    public String getValue() {
      return "new";
    }
  }

  public static class DestinationItem {
    private String value = "old";

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }

  public static class SourceItems {
    public List<SourceItem> getValues() {
      return Arrays.asList(new SourceItem(), null);
    }
  }

  public static class DestinationItems {
    private List<DestinationItem> values;

    public List<DestinationItem> getValues() {
      return values;
    }

    public void setValues(List<DestinationItem> values) {
      this.values = values;
    }
  }

  @Test(dataProvider = "configurations")
  public void shouldReuseNonNullDestinationElements(boolean merge, boolean skipNull) {
    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration().setCollectionsMergeEnabled(merge).setSkipNullEnabled(skipNull);
    DestinationItem first = new DestinationItem();
    DestinationItem tail = new DestinationItem();
    DestinationItems destination = new DestinationItems();
    destination.values = Arrays.asList(first, new DestinationItem(), tail);
    mapper.map(new SourceItems(), destination);
    assertEquals(destination.values.size(), merge ? 3 : 2);
    assertSame(destination.values.get(0), first);
    assertEquals(first.value, "new");
    assertNull(destination.values.get(1));
    if (merge)
      assertSame(destination.values.get(2), tail);
  }
}
