package org.modelmapper.functional.circular;

import static org.testng.Assert.*;

import org.modelmapper.AbstractTest;
import org.modelmapper.ConfigurationException;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * Tests the handling of circular references.
 * 
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class CircularDependencies2 extends AbstractTest {
  static class Tree {
    Node node;
  }

  static class Node {
    Tree tree;
    String value;
  }

  static class DTree {
    DNode node;

    public DNode getNode() {
      return node;
    }
  }

  static class DNode {
    DTree tree;
    String value;

    public void setTree(DTree tree) {
      this.tree = tree;
    }
  }

  @Test(expectedExceptions = ConfigurationException.class)
  public void shouldThrowOnNonSkippedCircularReference() {
    modelMapper.map(new Tree(), DTree.class);
  }

  public void shouldAllowSkippedCircularReference() {
    modelMapper.addMappings(new PropertyMap<Tree, DTree>() {
      @Override
      protected void configure() {
        skip().getNode().setTree(null);
      }
    });

    modelMapper.map(new Tree(), DTree.class);
  }

  // Asserts that the Node->DNode mapping is merged into the Tree->DTree mapping, and skipped
  public void shouldAllowSkippedCircularReferenceForInnerProperty() {
    modelMapper.addMappings(new PropertyMap<Node, DNode>() {
      @Override
      protected void configure() {
        skip().setTree(null);
      }
    });

    Tree tree = new Tree();
    tree.node = new Node();
    tree.node.value = "test";
    DTree dt = modelMapper.map(tree, DTree.class);

    assertNull(dt.node.tree);
    assertEquals(dt.node.value, "test");
  }
}
