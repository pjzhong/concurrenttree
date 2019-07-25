package com.zjp.tree.node.impl;

import com.zjp.tree.common.CharSequences;
import com.zjp.tree.node.Node;
import com.zjp.tree.node.util.AtomicReferenceArrayListAdapter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class CharArrayNodeDefault implements Node {

  private final char[] incomingEdgeCharArray;

  private final AtomicReferenceArray<Node> outgoingEdges;

  private final List<Node> outgoingEdgesList;

  private final Object value;

  public CharArrayNodeDefault(CharSequence sequence,
      Object value, List<Node> outgoingEdges) {

    Node[] childArray = outgoingEdges.toArray(new Node[0]);
    Arrays.sort(childArray, Comparator.comparingInt(Node::getIncomingEdgeFirstCharacter));

    this.outgoingEdges = new AtomicReferenceArray<>(childArray);
    this.outgoingEdgesList = new AtomicReferenceArrayListAdapter<>(this.outgoingEdges);
    this.incomingEdgeCharArray = CharSequences.toCharArray(sequence);
    this.value = value;
  }

  @Override
  public Character getIncomingEdgeFirstCharacter() {
    return null;
  }

  @Override
  public Node getOutgoingEdge(Character firstCharacter) {
    return null;
  }

  @Override
  public void updateOutgoingEdge(Node childNode) {

  }

  @Override
  public List<Node> getOutgoingEdges() {
    return null;
  }

  @Override
  public CharSequence getIncomingEdge() {
    return null;
  }

  @Override
  public Object getValue() {
    return null;
  }
}
