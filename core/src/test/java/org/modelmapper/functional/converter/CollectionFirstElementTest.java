package org.modelmapper.functional.converter;

import org.modelmapper.AbstractTest;
import org.modelmapper.Converters;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;

@Test
public class CollectionFirstElementTest extends AbstractTest {
  static class Source {
    List<SItem> items;

    public Source(List<SItem> items) {
      this.items = items;
    }

    public List<SItem> getItems() {
      return items;
    }

    public void setItems(List<SItem> items) {
      this.items = items;
    }
  }

  static class SItem {
    String value;

    public SItem(String value) {
      this.value = value;
    }
  }

  static class Destination {
    DItem item;

    public DItem getItem() {
      return item;
    }

    public void setItem(DItem item) {
      this.item = item;
    }
  }

  static class DItem {
    String value;
  }

  public void shouldConvertTo() {
    modelMapper.typeMap(Source.class, Destination.class)
        .addMappings(mapping -> mapping.using(Converters.Collection.first().to(DItem.class)).map(
            Source::getItems, Destination::setItem));
    Destination dest = modelMapper.map(new Source(Collections.singletonList(new SItem("foo"))), Destination.class);
    assertEquals(dest.item.value, "foo");
  }

  public void shouldMapTo() {
    final org.modelmapper.Converter<SItem, DItem> converter = context -> {
      DItem dItem = new DItem();
      dItem.value = context.getSource().value.toUpperCase();
      return dItem;
    };
    modelMapper.typeMap(Source.class, Destination.class)
        .addMappings(mapping -> mapping.using(Converters.Collection.<SItem>first().map(converter))
            .map(Source::getItems, Destination::setItem));
    Destination dest = modelMapper.map(new Source(Collections.singletonList(new SItem("foo"))), Destination.class);
    assertEquals(dest.item.value, "FOO");
  }
}
