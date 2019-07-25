package com.pjzhong.tree.raidx.util;

import com.zjp.tree.node.Node;
import com.zjp.tree.node.NodeFactory;
import com.zjp.tree.node.impl.DefaultCharArrayNodeFactory;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class NodeUtilTest {

  public void testBinarySearchForEdge() {
    NodeFactory nodeFactory = new DefaultCharArrayNodeFactory();
    Node[] nodes = new Node[] {
      nodeFactory.createNode("A", null, Collections.<Node>emptyList(), false),
      nodeFactory.createNode("B", null, Collections.<Node>emptyList(), false),
      nodeFactory.createNode("C", null, Collections.<Node>emptyList(), false),
    };
    AtomicReferenceArray<Node> atomicReferenceArray = new AtomicReferenceArray<Node>(nodes);
  }

}
