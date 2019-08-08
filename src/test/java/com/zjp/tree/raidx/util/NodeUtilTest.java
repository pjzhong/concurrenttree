package com.zjp.tree.raidx.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicReferenceArray;

import com.zjp.tree.node.Node;
import com.zjp.tree.node.NodeFactory;
import com.zjp.tree.node.impl.DefaultCharSequenceNodeFactory;
import com.zjp.tree.node.util.NodeUtil;
import org.junit.Test;

public class NodeUtilTest {

  @Test
	public void testBinarySearchForEdge() {
		NodeFactory nodeFactory = new DefaultCharSequenceNodeFactory();
		Node[] nodes = new Node[] {
				nodeFactory.createNode("A", null, Collections.emptyList(),
						false),
				nodeFactory.createNode("B", null, Collections.emptyList(),
						false),
				nodeFactory.createNode("C", null, Collections.emptyList(),
						false), };
		AtomicReferenceArray<Node> array = new AtomicReferenceArray<>(nodes);
		assertEquals(0, NodeUtil.binarySearch(array, 'A'));
		assertEquals(1, NodeUtil.binarySearch(array, 'B'));
		assertEquals(2, NodeUtil.binarySearch(array, 'C'));
		assertTrue(NodeUtil.binarySearch(array, 'D') < 0);
	}

}
