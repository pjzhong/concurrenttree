package com.zjp.tree.solver;

import static org.junit.Assert.assertEquals;

import com.zjp.tree.common.PrettyPrinter;
import com.zjp.tree.node.NodeFactory;
import com.zjp.tree.node.impl.DefaultCharSequenceNodeFactory;
import com.zjp.tree.util.IOUtil;
import java.util.Arrays;
import java.util.List;
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

  @Test
  public void testAddSuffixesToRadixTree_DuplicateHandling() {
    LCSubstringSolver solver = new LCSubstringSolver(factory);
    solver.addSuffixesToRadixTree("FOO");
    solver.addSuffixesToRadixTree("FOO");
    solver.addSuffixesToRadixTree("ZJP");
    String expected = "○\n"
        + "├──  FOO ([FOO])\n"
        + "├──  JP ([ZJP])\n"
        + "├──  O ([FOO])\n"
        + "│    └──  O ([FOO])\n"
        + "├──  P ([ZJP])\n"
        + "└──  ZJP ([ZJP])\n";
    assertEquals(expected, PrettyPrinter.prettyPrint(solver));
  }


  @Test
  public void shakespeareTragedies() {
    List<String> files = Arrays.asList(
        "/shakespeare/tragedies/antony_and_cleopatra.txt",
        "/shakespeare/tragedies/coriolanus.txt",
        "/shakespeare/tragedies/hamlet.txt",
        "/shakespeare/tragedies/julius_caesar.txt",
        "/shakespeare/tragedies/king_lear.txt",
        "/shakespeare/tragedies/macbeth.txt",
        "/shakespeare/tragedies/othello.txt",
        "/shakespeare/tragedies/romeo_and_juliet.txt",
        "/shakespeare/tragedies/timon_of_athens.txt",
        "/shakespeare/tragedies/titus_andronicus.txt"
    );

    LCSubstringSolver solver = new LCSubstringSolver(factory);
    long start = System.currentTimeMillis();
    for (String file : files) {
      String manuscript = IOUtil.loadTextFileFromClasspath(file);
      solver.add(manuscript);
      System.out.format("Added manuscript:%s", manuscript);
    }
    System.out.format("Built suffix tree in %s ms%n", System.currentTimeMillis() - start);

    System.out.println("start searching");
    long search = System.currentTimeMillis();
    CharSequence longest = solver.getLongestCommonSubstring();
    System.out
        .format("Found longest common substring in %s ms%n", System.currentTimeMillis() - search);
    System.out.format("Longest common %s%n", longest);
  }
}
