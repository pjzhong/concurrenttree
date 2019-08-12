package com.zjp.tree.node;

import java.util.List;

public interface NodeFactory {

  Node createNode(CharSequence edgeCharacters, Object value, List<Node> childNodes, boolean isRoot);

}
