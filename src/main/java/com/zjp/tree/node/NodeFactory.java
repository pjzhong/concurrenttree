package com.zjp.tree.node;

import java.util.List;

public interface NodeFactory {

  Node createNode(CharSequence eageCharacters, Object value, List<Node> childNodes, boolean isRoot);

}
