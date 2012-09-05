package org.modelmapper.functional.circular;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

@Test(groups = "functional")
public class CircularDependencies3 extends AbstractTest {
  public static class Node {
    Node parent;
    List<Node> children = new ArrayList<Node>();

    Node(Node parent) {
      this.parent = parent;
      parent.children.add(this);
    }

    Node() {
    }
  }

  public static class DestNode {
    DestNode parent;
    List<DestNode> children;
  }

  public void shouldMapCircularlReferences() {
    Node tree = new Node();
    buildTree(tree, 0, 3);

    DestNode destNode = modelMapper.map(tree, DestNode.class);
    assertTree(destNode, null);
  }

  private void buildTree(Node parent, int depth, int maxDepth) {
    Node left = new Node(parent);
    Node right = new Node(parent);
    if (depth + 1 < maxDepth) {
      buildTree(left, depth + 1, maxDepth);
      buildTree(right, depth + 2, maxDepth);
    }
  }

  private void assertTree(DestNode currentNode, DestNode parent) {
    assertEquals(currentNode.parent, parent);
    for (DestNode child : currentNode.children)
      assertTree(child, currentNode);
  }
}
