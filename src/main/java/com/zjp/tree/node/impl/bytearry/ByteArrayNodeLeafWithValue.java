package com.zjp.tree.node.impl.bytearry;

import com.zjp.tree.node.Node;
import java.util.Collections;
import java.util.List;

public class ByteArrayNodeLeafWithValue implements Node {

  private final byte[] incomingEdge;
  private final Object value;

  public ByteArrayNodeLeafWithValue(CharSequence edge, Object v) {
    this.incomingEdge = ByteArrayCharSequence.toSingleByteUtf8Encoding(edge);
    this.value = v;
  }

  @Override
  public Character getFirstCharacter() {
    return (char) (incomingEdge[0] & 0xFF);
  }

  @Override
  public Node getOutgoingEdge(Character firstCharacter) {
    return null;
  }

  @Override
  public void updateOutgoingEdge(Node childNode) {
    throw new IllegalStateException(String.format(
        "Cannot update the reference to the following child node for the dge starting with '%s', no such edge already exists:%s%n",
        childNode.getFirstCharacter(), childNode));

  }

  @Override
  public List<Node> getOutgoingEdges() {
    return Collections.emptyList();
  }

  @Override
  public CharSequence getIncomingEdge() {
    return new ByteArrayCharSequence(incomingEdge, 0, incomingEdge.length);
  }

  @Override
  public Object getValue() {
    return value;
  }
}
