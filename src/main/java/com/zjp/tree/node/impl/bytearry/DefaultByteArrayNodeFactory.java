package com.zjp.tree.node.impl.bytearry;

import com.zjp.tree.common.CharSequences;
import com.zjp.tree.node.Node;
import com.zjp.tree.node.NodeFactory;
import com.zjp.tree.node.util.NodeUtil;
import java.util.List;

public class DefaultByteArrayNodeFactory implements NodeFactory {

  @Override
  public Node createNode(CharSequence edgeCharacters, Object value, List<Node> childNodes,
      boolean isRoot) {
    if (edgeCharacters == null) {
      throw new IllegalStateException("The edgeCharacters argument was null");
    }
    if (!isRoot && edgeCharacters.length() == 0) {
      throw new IllegalStateException(
          "Invalid edge characters for non-root node: " + CharSequences.toString(edgeCharacters));
    }
    if (childNodes == null) {
      throw new IllegalStateException("The childNodes argument was null");
    }
    NodeUtil.ensureNoDuplicateEdges(childNodes);
    if (childNodes.isEmpty()) {
      if (value != null) {
        return new ByteArrayNodeLeafWithValue(edgeCharacters, value);
      } else {
        return new ByteArrayNodeLeafNullValue(edgeCharacters);
      }
    } else {
      if (value == null) {
        return new ByteArrayNodeNonLeafNullValue(edgeCharacters, childNodes);
      } else {
        return new ByteArrayNodeDefault(edgeCharacters, value, childNodes);
      }
    }
  }
}
