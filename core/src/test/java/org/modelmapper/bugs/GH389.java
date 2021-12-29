package org.modelmapper.bugs;

import org.modelmapper.AbstractTest;
import org.modelmapper.Converters;
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
        .addMappings(mapping -> mapping.using(Converters.Collection.first().to(Wrapper.class))
            .map(WrapperDTO::getItems, Wrapper::setItem));
    Wrapper wrapper = modelMapper.map(new WrapperDTO(Collections.singletonList(new ItemDTO("foo"))), Wrapper.class);
    assertEquals(wrapper.item.text, "foo");
  }
}
