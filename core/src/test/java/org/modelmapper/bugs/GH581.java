package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;
import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

@Test
public class GH581 extends AbstractTest {
  public void shouldMap() {
    SourceItem item1 = new SourceItem("foo1");
    SourceItem item2 = new SourceItem("foo2");
    Source source = new Source(
        Collections.singleton(item1),
        new SourceNode(item1),
        new SourceNode(item2));
    Destination destination = modelMapper.map(source, Destination.class);
    assertEquals(destination.items.size(), 1);
    assertEquals(destination.items.iterator().next().name, "foo1");
    assertEquals(destination.node1.item.name, "foo1");
    assertEquals(destination.node2.item.name, "foo2");
  }

  private static class Source {
    private Collection<SourceItem> items;
    private SourceNode node1;
    private SourceNode node2;

    public Source(Collection<SourceItem> items, SourceNode node1, SourceNode node2) {
      this.items = items;
      this.node1 = node1;
      this.node2 = node2;
    }

    public Collection<SourceItem> getItems() {
      return items;
    }

    public SourceNode getNode1() {
      return node1;
    }

    public SourceNode getNode2() {
      return node2;
    }
  }

  private static class SourceNode {
    private SourceItem item;

    public SourceNode(SourceItem item) {
      this.item = item;
    }

    public SourceItem getItem() {
      return item;
    }
  }

  private static class SourceItem {
    private String name;

    public String getName() {
      return name;
    }

    public SourceItem(String name) {
      this.name = name;
    }
  }

  private static class Destination {
    private Collection<DestinationItem> items;
    private DestinationNode node1;
    private DestinationNode node2;

    public void setItems(Collection<DestinationItem> items) {
      this.items = items;
    }

    public void setNode1(DestinationNode node1) {
      this.node1 = node1;
    }

    public void setNode2(DestinationNode node2) {
      this.node2 = node2;
    }
  }

  private static class DestinationNode {
    private DestinationItem item;

    public void setItem(DestinationItem item) {
      this.item = item;
    }
  }

  private static class DestinationItem {
    private String name;

    public void setName(String name) {
      this.name = name;
    }
  }
}
