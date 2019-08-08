package com.zjp.tree.solver;

import static org.junit.Assert.assertEquals;

import com.zjp.tree.common.PrettyPrinter;
import com.zjp.tree.node.NodeFactory;
import com.zjp.tree.node.impl.DefaultCharSequenceNodeFactory;
import org.junit.Test;

public class LCSSubstringSolverTest {

  NodeFactory factory = new DefaultCharSequenceNodeFactory();

  final String document1 =
      "albert einstein, was a german theoretical physicist who developed the theory of general relativity";

  final String document2 =
      "near the beginning of his career, albert einstein thought that newtonian mechanics was no longer "
          +
          "enough to reconcile the laws of classical mechanics with the laws of the electromagnetic field";

  final String document3 =
      "in late summer 1895, at the age of sixteen, albert einstein sat the entrance examinations for "
          +
          "the swiss federal polytechnic in zurich";

  @Test
  public void testGetLongestCommonSubstring() {
    LCSubstringSolver solver = new LCSubstringSolver(new DefaultCharSequenceNodeFactory());

    solver.add(document1);
    solver.add(document2);
    solver.add(document3);

    String longestCommon = solver.getLongestCommonSubstring().toString();
    assertEquals("albert einstein", longestCommon);

    System.out.println(PrettyPrinter.prettyPrint(solver));
  }
}
