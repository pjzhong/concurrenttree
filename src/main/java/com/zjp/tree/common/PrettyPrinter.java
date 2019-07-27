package com.zjp.tree.common;

import com.zjp.tree.ConcurrentRadixTree;
import com.zjp.tree.node.Node;
import java.util.List;

public class PrettyPrinter {

  PrettyPrinter() {
  }

  public static String prettyPrint(ConcurrentRadixTree tree) {
    StringBuilder sb = new StringBuilder();
    prettyPrint(tree.getRoot(), sb, "", true, true);
    return sb.toString();
  }

  static void prettyPrint(Node node, StringBuilder ap, String prefix, boolean isTail,
      boolean isRoot) {

    StringBuilder label = new StringBuilder();
    if (isRoot) {
      label.append("○");
      if (node.getIncomingEdge().length() > 0) {
        label.append(" ");
      }
    }
    label.append(node.getIncomingEdge());
    if (node.getValue() != null) {
      label.append(" (").append(node.getValue()).append(")");
    }
    ap.append(prefix).append(isTail ? isRoot ? "" : "└── ○ " : "├── ○ ")
        .append(label).append("\n");

    List<Node> children = node.getOutgoingEdges();
    for (int i = 0, size = children.size() - 1; i < size; i++) {
      prettyPrint(children.get(i), ap, prefix + (isTail ? isRoot ? "" : "     " : "│    "), false,
          false);
    }
    if (!children.isEmpty()) {
      prettyPrint(children.get(children.size() - 1), ap,
          prefix + (isTail ? isRoot ? "" : "     " : "│    "), true, false);
    }

  }
}
