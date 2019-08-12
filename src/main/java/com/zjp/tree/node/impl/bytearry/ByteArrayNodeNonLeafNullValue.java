package com.zjp.tree.node.impl.bytearry;

import com.zjp.tree.node.Node;
import com.zjp.tree.node.util.AtomicReferenceArrayListAdapter;
import com.zjp.tree.node.util.NodeUtil;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class ByteArrayNodeNonLeafNullValue implements Node {

  private final byte[] incomingEdge;
  private final AtomicReferenceArray<Node> outgoings;
  private final List<Node> outgoingList;

  public ByteArrayNodeNonLeafNullValue(CharSequence edge, List<Node> outgoings) {
    Node[] childNodeArray = outgoings.toArray(new Node[0]);

    Arrays.sort(childNodeArray, Comparator.comparingInt(Node::getFirstCharacter));
    this.outgoings = new AtomicReferenceArray<>(childNodeArray);
    this.incomingEdge = ByteArrayCharSequence.toSingleByteUtf8Encoding(edge);
    this.outgoingList = new AtomicReferenceArrayListAdapter<>(this.outgoings);
  }

  @Override
  public Character getFirstCharacter() {
    return (char) (incomingEdge[0] & 0xFF);
  }

  @Override
  public Node getOutgoingEdge(Character firstCharacter) {
    int index = NodeUtil.binarySearch(outgoings, firstCharacter);
    if (index < 0) {
      return null;
    }
    return outgoings.get(index);
  }

  @Override
  public void updateOutgoingEdge(Node childNode) {
    int index = NodeUtil.binarySearch(outgoings, childNode.getFirstCharacter());
    if (index < 0) {
      throw new IllegalStateException(String.format(
          "Cannot update the reference to the following child node for the dge starting with '%s', no such edge already exists:%s%n",
          childNode.getFirstCharacter(), childNode));
    }
    outgoings.set(index, childNode);
  }

  @Override
  public List<Node> getOutgoingEdges() {
    return outgoingList;
  }

  @Override
  public CharSequence getIncomingEdge() {
    return new ByteArrayCharSequence(incomingEdge, 0, incomingEdge.length);
  }

  @Override
  public Object getValue() {
    return null;
  }
}
