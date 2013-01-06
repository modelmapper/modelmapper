package org.modelmapper.functional.shading;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * Test for <a href=
 * "http://stackoverflow.com/questions/11033780/is-mapping-beans-with-list-of-bean-properties-supported-by-model-mapper"
 * >so post</a> and <a href="https://github.com/jhalterman/modelmapper/pull/5">gh issue</a>
 * 
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class NullMapping2 extends AbstractTest {
  static class Container {
    List<Item> items;
  }

  static class Item {
    Message message;

    Item(Message message) {
      this.message = message;
    }
  }

  static class Message {
    String value1;
    String value2;

    Message(String value1, String value2) {
      this.value1 = value1;
      this.value2 = value2;
    }
  }

  static class ContainerDTO {
    List<ItemDTO> items;
  }

  static class ItemDTO {
    MessageDTO message;
  }

  static class MessageDTO {
    String value1;
    String value2;
  }

  /** Asserts that a single shaded item does not effect other mapped items in a collection. */
  public void shouldShadeWhenNullEncountered() {
    Container c = new Container();
    List<Item> items = new ArrayList<Item>(10);
    for (int i = 0; i < 5; i++)
      items.add(new Item(i == 2 ? null : new Message("value1", "value2")));
    c.items = items;

    ContainerDTO dto = modelMapper.map(c, ContainerDTO.class);
    for (int i = 0; i < dto.items.size(); i++)
      if (i == 2) {
        assertNull(dto.items.get(i).message);
      } else {
        assertNotNull(dto.items.get(i).message.value1);
        assertNotNull(dto.items.get(i).message.value2);
      }
  }
}