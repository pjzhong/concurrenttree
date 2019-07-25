package com.zjp.tree.node;

import java.util.List;

public interface Node {

  Character getIncomingEdgeFirstCharacter();

  Node getOutgoingEdge(Character firstCharacter);

  void updateOutgoingEdge(Node childNode);

  List<Node> getOutgoingEdges();

  CharSequence getIncomingEdge();

  Object getValue();
}
