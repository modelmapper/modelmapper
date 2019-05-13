package org.modelmapper.functional.converter;

import org.modelmapper.AbstractTest;
import org.modelmapper.Converters;
import org.modelmapper.ExpressionMap;
import org.modelmapper.builder.ConfigurableConditionExpression;
import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.SourceGetter;
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
        .addMappings(new ExpressionMap<Source, Destination>() {
          @Override
          public void configure(ConfigurableConditionExpression<Source, Destination> mapping) {
            mapping.using(Converters.Collection.first().to(DItem.class)).map(
                getItems(), setItem());
          }
        });
    Destination dest = modelMapper.map(new Source(Collections.singletonList(new SItem("foo"))), Destination.class);
    assertEquals(dest.item.value, "foo");
  }

  public void shouldMapTo() {
    final org.modelmapper.Converter<SItem, DItem> converter = new org.modelmapper.Converter<SItem, DItem>() {
      @Override
      public DItem convert(MappingContext<SItem, DItem> context) {
        DItem dItem = new DItem();
        dItem.value = context.getSource().value.toUpperCase();
        return dItem;
      }
    };
    modelMapper.typeMap(Source.class, Destination.class)
        .addMappings(new ExpressionMap<Source, Destination>() {
          @Override
          public void configure(ConfigurableConditionExpression<Source, Destination> mapping) {
            mapping.using(Converters.Collection.<SItem>first().map(converter)).map(
                getItems(), setItem());
          }
        });
    Destination dest = modelMapper.map(new Source(Collections.singletonList(new SItem("foo"))), Destination.class);
    assertEquals(dest.item.value, "FOO");
  }

  private static SourceGetter<Source> getItems() {
    return new SourceGetter<Source>() {
      @Override
      public Object get(Source source) {
        return source.getItems();
      }
    };
  }

  private static DestinationSetter<Destination, DItem> setItem() {
    return new DestinationSetter<Destination, DItem>() {
      @Override
      public void accept(Destination destination, DItem value) {
        destination.setItem(value);
      }
    };
  }
}
