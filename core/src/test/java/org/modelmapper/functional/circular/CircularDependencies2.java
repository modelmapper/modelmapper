package org.modelmapper.functional.circular;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.modelmapper.AbstractTest;
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
    Child child;
    String nodeValue;
  }

  static class Child {
    Tree tree;
    Node node;
    String childValue;
  }

  static class DTree {
    DNode node;

    public DNode getNode() {
      return node;
    }
  }

  static class DNode {
    DTree tree;
    DChild child;
    String nodeValue;

    public void setTree(DTree tree) {
      this.tree = tree;
    }
  }

  static class DChild {
    DTree tree;
    DNode node;
    String childValue;
  }

  public void shouldMapGraphEdges() {
    Tree tree1 = new Tree();
    Tree tree2 = new Tree();
    Node node1 = new Node();
    Node node2 = new Node();
    Node node3 = new Node();
    Child child1 = new Child();
    Child child2 = new Child();

    tree1.node = node1;
    tree1.node.tree = tree2; // Link to second tree
    tree1.node.nodeValue = "tree1nodevalue";
    tree1.node.child = child1;
    tree1.node.child.childValue = "cv1";
    tree1.node.child.node = node2; // Link to second node

    tree2.node = node2; // Create graph edge
    tree2.node.nodeValue = "tree2nodevalue";
    tree2.node.child = child2;
    tree2.node.child.childValue = "cv2";
    tree2.node.child.node = node3;
    tree2.node.child.node.nodeValue = "tree2nodechildnodevalue";

    DTree d = modelMapper.map(tree1, DTree.class);
    assertEquals(d.node.nodeValue, "tree1nodevalue");
    assertEquals(d.node.child.childValue, "cv1");
    assertEquals(d.node.tree.node, d.node.child.node); // Assert graph edge
    assertEquals(d.node.tree.node.nodeValue, "tree2nodevalue");
    assertEquals(d.node.tree.node.child.childValue, "cv2");
    assertEquals(d.node.tree.node.child.node.nodeValue, "tree2nodechildnodevalue");
  }

  public void shouldMapCircularReferences() {
    Tree tree = new Tree();
    tree.node = new Node();
    tree.node.tree = tree;
    tree.node.child = new Child();
    tree.node.child.node = tree.node;
    tree.node.child.childValue = "cv";
    tree.node.nodeValue = "test";

    DTree d = modelMapper.map(tree, DTree.class);
    assertEquals(d.node.tree, d);
    assertEquals(d.node.tree.node, d.node);
    assertEquals(tree.node.child.childValue, d.node.tree.node.child.childValue);
    assertEquals(tree.node.nodeValue, d.node.nodeValue);
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
    tree.node.nodeValue = "test";
    DTree dt = modelMapper.map(tree, DTree.class);

    assertNull(dt.node.tree);
    assertEquals(dt.node.nodeValue, "test");
  }
}
