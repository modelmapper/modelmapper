package org.modelmapper.bugs;

import org.modelmapper.AbstractTest;
import org.modelmapper.Converters;
import org.modelmapper.ExpressionMap;
import org.modelmapper.builder.ConfigurableConditionExpression;
import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.spi.SourceGetter;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;

@Test
public class GH389 extends AbstractTest {
  static class WrapperDTO {
    List<ItemDTO> items;

    public WrapperDTO(List<ItemDTO> items) {
      this.items = items;
    }

    public List<ItemDTO> getItems() {
      return items;
    }

    public void setItems(List<ItemDTO> items) {
      this.items = items;
    }
  }

  static class ItemDTO {
    String text;

    public ItemDTO(String text) {
      this.text = text;
    }
  }

  static class Wrapper {
    Item item;

    public Item getItem() {
      return item;
    }

    public void setItem(Item item) {
      this.item = item;
    }
  }

  static class Item {
    String text;
  }

  public void shouldMap() {
    modelMapper.typeMap(WrapperDTO.class, Wrapper.class)
        .addMappings(new ExpressionMap<WrapperDTO, Wrapper>() {
          @Override
          public void configure(ConfigurableConditionExpression<WrapperDTO, Wrapper> mapping) {
            mapping.using(Converters.Collection.first().to(Wrapper.class)).map(
                new SourceGetter<WrapperDTO>() {
                  @Override
                  public Object get(WrapperDTO source) {
                    return source.getItems();
                  }
                },
                new DestinationSetter<Wrapper, Item>() {
                  @Override
                  public void accept(Wrapper destination, Item value) {
                    destination.setItem(value);
                  }
                });
          }
        });
    Wrapper wrapper = modelMapper.map(new WrapperDTO(Collections.singletonList(new ItemDTO("foo"))), Wrapper.class);
    assertEquals(wrapper.item.text, "foo");
  }
}
