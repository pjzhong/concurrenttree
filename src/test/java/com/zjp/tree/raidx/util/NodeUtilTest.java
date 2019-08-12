package com.zjp.tree.raidx.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.zjp.tree.node.Node;
import com.zjp.tree.node.NodeFactory;
import com.zjp.tree.node.impl.DefaultCharSequenceNodeFactory;
import com.zjp.tree.node.impl.bytearry.DefaultByteArrayNodeFactory;
import com.zjp.tree.node.util.NodeUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;
import org.junit.Test;

public class NodeUtilTest {

  @Test
  public void testBinarySearchForEdge() {
    NodeFactory nodeFactory = new DefaultCharSequenceNodeFactory();
    Node[] nodes = new Node[]{
        nodeFactory.createNode("A", null, Collections.emptyList(),
            false),
        nodeFactory.createNode("B", null, Collections.emptyList(),
            false),
        nodeFactory.createNode("C", null, Collections.emptyList(),
            false),};
    AtomicReferenceArray<Node> array = new AtomicReferenceArray<>(nodes);
    assertEquals(0, NodeUtil.binarySearch(array, 'A'));
    assertEquals(1, NodeUtil.binarySearch(array, 'B'));
    assertEquals(2, NodeUtil.binarySearch(array, 'C'));
    assertTrue(NodeUtil.binarySearch(array, 'D') < 0);
  }

  @Test(expected = IllegalStateException.class)
  public void testEnsureNoDuplicateEdges_dup() {
    NodeFactory nodeFactory = new DefaultByteArrayNodeFactory();
    List<Node> nodes = Arrays
        .asList(
            nodeFactory.createNode("A", null, Collections.emptyList(), false),
            nodeFactory.createNode("B", null, Collections.emptyList(), false),
            nodeFactory.createNode("B", null, Collections.emptyList(), false),
            nodeFactory.createNode("C", null, Collections.emptyList(), false)
        );
    NodeUtil.ensureNoDuplicateEdges(nodes);
  }

  @Test
  public void testEnsureNoDuplicateEdges_NoDup() {
    NodeFactory nodeFactory = new DefaultByteArrayNodeFactory();
    List<Node> nodes = Arrays
        .asList(
            nodeFactory.createNode("A", null, Collections.emptyList(), false),
            nodeFactory.createNode("B", null, Collections.emptyList(), false),
            nodeFactory.createNode("C", null, Collections.emptyList(), false),
            nodeFactory.createNode("D", null, Collections.emptyList(), false)
        );
    NodeUtil.ensureNoDuplicateEdges(nodes);
  }

}
