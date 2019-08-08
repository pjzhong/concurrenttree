package com.zjp.tree.common;

import static org.junit.Assert.assertEquals;

import com.zjp.tree.node.Node;
import com.zjp.tree.node.NodeFactory;
import com.zjp.tree.node.impl.DefaultCharSequenceNodeFactory;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class PrettyPrinterTest {

  private NodeFactory nodeFactory = new DefaultCharSequenceNodeFactory();

  @Test
  public void testPrettyPrint_ToString() {
    Node root = buildTreeByHand();
    String expected =
        "○\n"
            + "└──  B (1)\n"
            + "     └──  A (2)\n"
            + "          └──  N (3)\n"
            + "               ├──  AN (5)\n"
            + "               │    └──  A (6)\n"
            + "               └──  DANA (4)\n";
    assertEquals(expected, PrettyPrinter.prettyPrint(() -> root));

    String expected2 =
        "○ B (1)\n"
            + "└──  A (2)\n"
            + "     └──  N (3)\n"
            + "          ├──  AN (5)\n"
            + "          │    └──  A (6)\n"
            + "          └──  DANA (4)\n";
    assertEquals(expected2, PrettyPrinter.prettyPrint(() -> root.getOutgoingEdge('B')));
  }

  private Node buildTreeByHand() {
    final Node n1, n2, n3, n4, n5, n6;

    String expected =
        "\n"
            + "└──  B (1)\n"
            + "     └──  A (2)\n"
            + "          └──  N (3)\n"
            + "               ├──  AN (5)\n"
            + "               │    └──  A (6)\n"
            + "               └──  DANA (4)\n";

    n6 = nodeFactory.createNode("A", 6, Collections.emptyList(), false);
    n5 = nodeFactory.createNode("AN", 5, Collections.singletonList(n6), false);
    n4 = nodeFactory.createNode("DANA", 4, Collections.emptyList(), false);
    n3 = nodeFactory.createNode("N", 3, Arrays.asList(n5, n4), false);
    n2 = nodeFactory.createNode("A", 2, Collections.singletonList(n3), false);
    n1 = nodeFactory.createNode("B", 1, Collections.singletonList(n2), false);

    return nodeFactory.createNode("", null, Collections.singletonList(n1), true);
  }

}
