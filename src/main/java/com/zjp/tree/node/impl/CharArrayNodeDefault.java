package com.zjp.tree.node.impl;

import com.zjp.tree.common.CharSequences;
import com.zjp.tree.node.Node;
import com.zjp.tree.node.util.AtomicReferenceArrayListAdapter;
import com.zjp.tree.node.util.NodeUtil;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class CharArrayNodeDefault implements Node {

  private final char[] incomingEdgeCharArray;

  private final AtomicReferenceArray<Node> outgoingEdges;

  private final List<Node> outgoingEdgesList;

  private final Object value;

  public CharArrayNodeDefault(CharSequence sequence, Object value,
      List<Node> outgoingEdges) {

    Node[] childArray = outgoingEdges.toArray(new Node[0]);
    Arrays.sort(childArray,
        Comparator.comparingInt(Node::getIncomingEdgeFirstCharacter));

    this.outgoingEdges = new AtomicReferenceArray<>(childArray);
    this.outgoingEdgesList = new AtomicReferenceArrayListAdapter<>(
        this.outgoingEdges);
    this.incomingEdgeCharArray = CharSequences.toCharArray(sequence);
    this.value = value;
  }

  @Override
  public Character getIncomingEdgeFirstCharacter() {
    return incomingEdgeCharArray[0];
  }

  @Override
  public Node getOutgoingEdge(Character firstCharacter) {
    int index = NodeUtil.binarySearch(outgoingEdges,
        firstCharacter);
    if (index < 0) {
      return null;
    }

    return outgoingEdges.get(index);
  }

  @Override
  public void updateOutgoingEdge(Node childNode) {
    int index = NodeUtil.binarySearch(outgoingEdges,
        childNode.getIncomingEdgeFirstCharacter());
    if (index < 0) {
      throw new IllegalStateException(String.format(
          "Cannot update the reference to the following child node for the edge starting with '%s', no such edge already exists: %s",
          childNode.getIncomingEdgeFirstCharacter(), childNode));
    }

    outgoingEdges.set(index, childNode);
  }

  @Override
  public List<Node> getOutgoingEdges() {
    return outgoingEdgesList;
  }

  @Override
  public CharSequence getIncomingEdge() {
    return new StringBuilder(incomingEdgeCharArray.length).append(incomingEdgeCharArray);
  }

  @Override
  public Object getValue() {
    return value;
  }
}
