package com.pjzhong.tree.raidx;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import com.zjp.tree.ConcurrentRadixTree;
import com.zjp.tree.node.impl.DefaultCharArrayNodeFactory;
import org.junit.Test;

public class ConcurrentRadixTreeTest {

  @Test
  public void testPut_AddToRoot() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(
        new DefaultCharArrayNodeFactory());
    String expected =
        "○\n" +
            "└── ○ A (1)\n";
    String actual = "";
    assertNull(tree.put("A", 1));
    assertThat(1, is(tree.getValueForExactKey("A")));
    assertEquals(expected, actual);//TODO PRETTY
  }
}
