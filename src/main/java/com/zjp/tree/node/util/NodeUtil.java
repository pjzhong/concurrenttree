package com.zjp.tree.node.util;

import com.zjp.tree.node.Node;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class NodeUtil {

  public static int binarySearch(AtomicReferenceArray<Node> array,
      Character target) {
    int low = 0;
    int height = array.length() - 1;

    while (low <= height) {
      int mid = (low + height) >>> 1;
      Node midVal = array.get(mid);
      int cmp = midVal.getFirstCharacter().compareTo(target);

      if (cmp < 0) {
        low = mid + 1;
      } else if (cmp > 0) {
        height = mid - 1;
      } else {
        return mid;
      }
    }

    return -(low + 1);
  }

  public static void ensureNoDuplicateEdges(List<Node> nodes) {
    Set<Character> uniqueChars = new HashSet<>();
    for (Node node : nodes) {
      uniqueChars.add(node.getFirstCharacter());
    }
    if (nodes.size() != uniqueChars.size()) {
      throw new IllegalStateException(
          "Duplicate edge detected in list of nodes supplied: " + nodes);
    }
  }

}
