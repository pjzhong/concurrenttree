package com.zjp.tree.node.impl;

import com.zjp.tree.common.CharSequences;
import com.zjp.tree.node.Node;
import com.zjp.tree.node.NodeFactory;
import java.util.List;
import java.util.Objects;

public class DefaultCharSequenceNodeFactory implements NodeFactory {

  @Override
  public Node createNode(CharSequence edgeCharacters, Object value, List<Node> childNodes,
      boolean isRoot) {
    Objects.requireNonNull(edgeCharacters, "The edgeCharacter argument was null");
    Objects.requireNonNull(childNodes, "The childNodes argument was null");
    if (!isRoot && edgeCharacters.length() == 0) {
      throw new NullPointerException(
          "Invalid edge characters for non-root node: " + CharSequences.toString(edgeCharacters));
    }
    return new CharSequenceNodeDefault(edgeCharacters, value, childNodes);
  }
}
