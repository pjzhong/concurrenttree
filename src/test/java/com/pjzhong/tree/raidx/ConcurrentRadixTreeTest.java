package com.pjzhong.tree.raidx;

import static org.junit.Assert.assertEquals;

import com.zjp.tree.ConcurrentRadixTree;
import org.junit.Test;

public class ConcurrentRadixTreeTest {

  @Test
  public void testPut_AddToRoot() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>();
    tree.put("A", 1);
    String expected =
        "○\n" +
            "└── ○ A (1)\n";
    String actual = "";
    assertEquals(expected, actual);
  }
}
