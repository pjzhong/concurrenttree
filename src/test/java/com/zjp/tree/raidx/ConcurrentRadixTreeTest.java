package com.zjp.tree.raidx;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.zjp.tree.ConcurrentRadixTree;
import com.zjp.tree.common.Iterables;
import com.zjp.tree.common.PrettyPrinter;
import com.zjp.tree.node.Node;
import com.zjp.tree.node.NodeFactory;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public abstract class ConcurrentRadixTreeTest {

  public abstract NodeFactory getNodeFactory();

  @Test
  public void testBuildTreeBuHand() {
    final Node root, n1, n2, n3, n4, n5, n6;

    String expected =
        "○\n"
            + "└──  B (1)\n"
            + "     └──  A (2)\n"
            + "          └──  N (3)\n"
            + "               ├──  AN (5)\n"
            + "               │    └──  A (6)\n"
            + "               └──  DANA (4)\n";

    n6 = getNodeFactory().createNode("A", 6, Collections.emptyList(), false);
    n5 = getNodeFactory().createNode("AN", 5, Collections.singletonList(n6), false);
    n4 = getNodeFactory().createNode("DANA", 4, Collections.emptyList(), false);
    n3 = getNodeFactory().createNode("N", 3, Arrays.asList(n5, n4), false);
    n2 = getNodeFactory().createNode("A", 2, Collections.singletonList(n3), false);
    n1 = getNodeFactory().createNode("B", 1, Collections.singletonList(n2), false);

    root = getNodeFactory().createNode("", null, Collections.singletonList(n1), true);

    String actual = PrettyPrinter.prettyPrint(() -> root);
    assertEquals(expected, actual);
  }

  @Test
  public void testPut_AddToRoot() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(
        getNodeFactory());
    String expected =
        "○\n" +
            "└──  A (1)\n";
    assertNull(tree.put("A", 1));
    assertThat(1, is(tree.getValueForExactKey("A")));
    String actual = PrettyPrinter.prettyPrint(tree);
    assertEquals(expected, actual);
  }

  @Test
  public void test_ChildNodeSorting() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("B", 1);
    tree.put("A", 2);

    String expected =
        "○\n"
            + "├──  A (2)\n"
            + "└──  B (1)\n";
    String actual = PrettyPrinter.prettyPrint(tree);
    assertEquals(expected, actual);
  }

  @Test
  public void testPut_AppendChild() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("FOO", 1);
    tree.put("FOOBAR", 2);

    String expected =
        "○\n"
            + "└──  FOO (1)\n"
            + "     └──  BAR (2)\n";
    assertEquals(expected, PrettyPrinter.prettyPrint(tree));
  }

  @Test
  public void testPut_SplitEdge() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("FOOBAR", 1);
    tree.put("FOO", 2);

    String expected =
        "○\n"
            + "└──  FOO (2)\n"
            + "     └──  BAR (1)\n";
    assertEquals(expected, PrettyPrinter.prettyPrint(tree));
  }

  @Test
  public void testPut_SplitWithImplicitNode() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("FOOBAR", 1);
    tree.put("FOOD", 2);

    String expected =
        "○\n"
            + "└──  FOO\n"
            + "     ├──  BAR (1)\n"
            + "     └──  D (2)\n";
    assertThat(1, is(tree.getValueForExactKey("FOOBAR")));
    assertThat(2, is(tree.getValueForExactKey("FOOD")));
    assertEquals(expected, PrettyPrinter.prettyPrint(tree));
  }

  @Test
  public void testPut_SplitWithImplicitNodes() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("FOOBAR", 1);
    tree.put("FOOD", 2);
    tree.put("F", 3);

    assertThat(1, is(tree.getValueForExactKey("FOOBAR")));
    assertThat(2, is(tree.getValueForExactKey("FOOD")));
    assertThat(3, is(tree.getValueForExactKey("F")));
    String expected =
        "○\n"
            + "└──  F (3)\n"
            + "     └──  OO\n"
            + "          ├──  BAR (1)\n"
            + "          └──  D (2)\n";
    assertEquals(expected, PrettyPrinter.prettyPrint(tree));
  }

  @Test
  public void testPut_SplitWithImplicitNodesReverse() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("F", 3);
    tree.put("FOOBAR", 1);
    tree.put("FOOD", 2);

    assertThat(1, is(tree.getValueForExactKey("FOOBAR")));
    assertThat(2, is(tree.getValueForExactKey("FOOD")));
    assertThat(3, is(tree.getValueForExactKey("F")));

    String expected =
        "○\n"
            + "└──  F (3)\n"
            + "     └──  OO\n"
            + "          ├──  BAR (1)\n"
            + "          └──  D (2)\n";
    assertEquals(expected, PrettyPrinter.prettyPrint(tree));
  }

  @Test
  public void testPut_SplitAndMove() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("TEST", 1);
    tree.put("TEAM", 2);
    tree.put("TOAST", 3);
    String expected =
        "○\n"
            + "└──  T\n"
            + "     ├──  E\n"
            + "     │    ├──  AM (2)\n"
            + "     │    └──  ST (1)\n"
            + "     └──  OAST (3)\n";
    assertEquals(expected, PrettyPrinter.prettyPrint(tree));
  }

  @Test
  public void testPut_OverwriteValue() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    Integer existing = tree.put("FOO", 1);
    assertNull(existing);
    Integer oldValue = tree.put("FOO", 2);
    assertNotNull(oldValue);

    assertThat(1, is(oldValue));
    assertThat(2, is(tree.getValueForExactKey("FOO")));
  }

  @Test
  public void testPut_DoNotOverwriteValue() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());

    Integer existing = tree.putIfAbsent("FOO", 1);
    assertNull(existing);

    Integer nowExisting = tree.putIfAbsent("FOO", 2);
    assertNotNull(nowExisting);

    assertThat(1, is(nowExisting));
    assertThat(1, is(tree.getValueForExactKey("FOO")));
  }

  @Test
  public void testPutIfAbsent_SplitNode() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());

    Integer p1 = tree.putIfAbsent("FOOBAR", 1);
    assertNull(p1);
    Integer p2 = tree.putIfAbsent("FOOD", 1);
    assertNull(p2);

    Integer p3 = tree.putIfAbsent("FOO", 2);
    assertNull(p3);

    assertThat(1, is(tree.getValueForExactKey("FOOBAR")));
    assertThat(1, is(tree.getValueForExactKey("FOOD")));
    assertThat(2, is(tree.getValueForExactKey("FOO")));
  }

  @Test(expected = NullPointerException.class)
  public void testPutInternal_KeyNullValidation() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put(null, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPutInternal_KeyEmptyValidation() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("", 1);
  }

  @Test(expected = NullPointerException.class)
  public void testPutInternal_ValueNullValidation() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("foo", null);
  }

  @Test
  public void testGet() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("TEST", 1);
    tree.put("TEAM", 2);
    tree.put("TOAST", 3);

    assertThat(1, is(tree.getValueForExactKey("TEST")));
    assertThat(2, is(tree.getValueForExactKey("TEAM")));
    assertThat(3, is(tree.getValueForExactKey("TOAST")));
    assertNull(tree.getValueForExactKey("T"));
    assertNull(tree.getValueForExactKey("TE"));
    assertNull(tree.getValueForExactKey("E"));
    assertNull(tree.getValueForExactKey(""));
  }

  @Test
  public void testSize() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    assertEquals(0, tree.size());
    tree.put("TEST", 1);
    assertEquals(1, tree.size());
    tree.put("TOAST", 2);
    assertEquals(2, tree.size());
    tree.put("TEAM", 3);
    assertEquals(3, tree.size());
    tree.put("TEST", 4);
    assertEquals(3, tree.size());

    assertFalse(tree.remove("FOO"));
    assertEquals(3, tree.size());

    assertTrue(tree.remove("TOAST"));
    assertEquals(2, tree.size());
    assertTrue(tree.remove("TEAM"));
    assertEquals(1, tree.size());
    assertTrue(tree.remove("TEST"));
    assertEquals(0, tree.size());

    assertFalse(tree.remove("NOT_EXISTS"));
    assertEquals(0, tree.size());
  }

  @Test
  public void testRemove_MoreThanOneChildEdge() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("FOO", 1);
    tree.put("FOOBAR", 2);
    tree.put("FOOD", 3);

    String expected =
        "○\n"
            + "└──  FOO (1)\n"
            + "     ├──  BAR (2)\n"
            + "     └──  D (3)\n";
    assertEquals(expected, PrettyPrinter.prettyPrint(tree));

    assertTrue(tree.remove("FOO"));
    String afterRemove =
        "○\n"
            + "└──  FOO\n"
            + "     ├──  BAR (2)\n"
            + "     └──  D (3)\n";
    assertEquals(afterRemove, PrettyPrinter.prettyPrint(tree));
  }

  @Test
  public void testRemove_ExactlyOneChildEdge() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("FOO", 1);
    tree.put("FOOBAR", 2);
    tree.put("FOOBARBAZ", 3);

    String expected =
        "○\n"
            + "└──  FOO (1)\n"
            + "     └──  BAR (2)\n"
            + "          └──  BAZ (3)\n";
    assertEquals(expected, PrettyPrinter.prettyPrint(tree));

    assertTrue(tree.remove("FOO"));
    String afterRemove =
        "○\n"
            + "└──  FOOBAR (2)\n"
            + "     └──  BAZ (3)\n";
    assertEquals(afterRemove, PrettyPrinter.prettyPrint(tree));
  }

  @Test
  public void testRemove_ZeroChildEdges_DirectChildOfRoot() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("FOO", 1);
    tree.put("BAR", 2);

    String expected =
        "○\n"
            + "├──  BAR (2)\n"
            + "└──  FOO (1)\n";
    assertEquals(expected, PrettyPrinter.prettyPrint(tree));

    assertTrue(tree.remove("FOO"));
    String afterRemove =
        "○\n"
            + "└──  BAR (2)\n";
    assertEquals(afterRemove, PrettyPrinter.prettyPrint(tree));
  }

  @Test
  public void testRemove_LastRemainingKey() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("FOO", 1);

    String expected = "○\n"
        + "└──  FOO (1)\n";
    assertEquals(expected, PrettyPrinter.prettyPrint(tree));

    assertTrue(tree.remove("FOO"));
    String afterRemove = "○\n";
    assertEquals(afterRemove, PrettyPrinter.prettyPrint(tree));
  }

  @Test
  public void testRemove_ZeroChildEdges_OneStepFromRoot() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("FOO", 1);
    tree.put("FOOBAR", 2);

    String expected =
        "○\n"
            + "└──  FOO (1)\n"
            + "     └──  BAR (2)\n";
    assertEquals(expected, PrettyPrinter.prettyPrint(tree));

    assertTrue(tree.remove("FOOBAR"));
    String afterRemove =
        "○\n"
            + "└──  FOO (1)\n";
    assertEquals(afterRemove, PrettyPrinter.prettyPrint(tree));
  }

  @Test
  public void testRemove_ZeroChildEdges_SeveralStepsFromRoot() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("FOO", 1);
    tree.put("FOOBAR", 2);
    tree.put("FOOBARBAZ", 3);

    String expected =
        "○\n"
            + "└──  FOO (1)\n"
            + "     └──  BAR (2)\n"
            + "          └──  BAZ (3)\n";
    assertEquals(expected, PrettyPrinter.prettyPrint(tree));

    assertTrue(tree.remove("FOOBARBAZ"));
    String afterRemove =
        "○\n"
            + "└──  FOO (1)\n"
            + "     └──  BAR (2)\n";
    assertEquals(afterRemove, PrettyPrinter.prettyPrint(tree));
  }

  @Test
  public void testRemove_DoNotRemoveSplitNode() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("FOOBAR", 1);
    tree.put("FOOD", 2);

    String expected = "○\n"
        + "└──  FOO\n"
        + "     ├──  BAR (1)\n"
        + "     └──  D (2)\n";
    assertEquals(expected, PrettyPrinter.prettyPrint(tree));

    assertFalse(tree.remove("FOO"));
    assertEquals(expected, PrettyPrinter.prettyPrint(tree));
  }

  @Test
  public void testRemove_MergeSplitNode() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("TEST", 1);
    tree.put("TEAM", 2);
    tree.put("TOAST", 3);
    String expected =
        "○\n"
            + "└──  T\n"
            + "     ├──  E\n"
            + "     │    ├──  AM (2)\n"
            + "     │    └──  ST (1)\n"
            + "     └──  OAST (3)\n";
    assertEquals(expected, PrettyPrinter.prettyPrint(tree));

    assertTrue(tree.remove("TEST"));
    String afterRemove =
        "○\n"
            + "└──  T\n"
            + "     ├──  EAM (2)\n"
            + "     └──  OAST (3)\n";
    assertEquals(afterRemove, PrettyPrinter.prettyPrint(tree));
  }

  @Test
  public void testRemove_DoNotMergeSplitNodeWithValue() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("TEST", 1);
    tree.put("TEAM", 2);
    tree.put("TOAST", 3);
    tree.put("TE", 4);
    String expected =
        "○\n"
            + "└──  T\n"
            + "     ├──  E (4)\n"
            + "     │    ├──  AM (2)\n"
            + "     │    └──  ST (1)\n"
            + "     └──  OAST (3)\n";
    assertEquals(expected, PrettyPrinter.prettyPrint(tree));

    assertTrue(tree.remove("TEST"));
    String afterRemove =
        "○\n"
            + "└──  T\n"
            + "     ├──  E (4)\n"
            + "     │    └──  AM (2)\n"
            + "     └──  OAST (3)\n";
    assertEquals(afterRemove, PrettyPrinter.prettyPrint(tree));
  }

  @Test
  public void testRemove_noSuchKey() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("FOO", 1);
    tree.put("BAR", 2);

    String expected =
        "○\n"
            + "├──  BAR (2)\n"
            + "└──  FOO (1)\n";
    assertEquals(expected, PrettyPrinter.prettyPrint(tree));

    assertFalse(tree.remove("TEST"));
    assertEquals(expected, PrettyPrinter.prettyPrint(tree));
  }

  @Test
  public void testGetKeysForPrefix() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("TEST", 1);
    tree.put("TEAM", 2);
    tree.put("TOAST", 3);
    tree.put("TEA", 4);
    tree.put("COFFEE", 5);

    assertEquals("[COFFEE, TEA, TEAM, TEST, TOAST]",
        Iterables.toString(tree.getKeysStartingWith("")));
    assertEquals("[COFFEE]", Iterables.toString(tree.getKeysStartingWith("C")));
    assertEquals("[COFFEE]", Iterables.toString(tree.getKeysStartingWith("COFFEE")));
    assertEquals("[]", Iterables.toString(tree.getKeysStartingWith("COFFEES")));
    assertEquals("[TEA, TEAM, TEST, TOAST]", Iterables.toString(tree.getKeysStartingWith("T")));
    assertEquals("[TEA, TEAM, TEST]", Iterables.toString(tree.getKeysStartingWith("TE")));
    assertEquals("[TEA, TEAM]", Iterables.toString(tree.getKeysStartingWith("TEA")));
    assertEquals("[TOAST]", Iterables.toString(tree.getKeysStartingWith("TO")));
  }

  @Test
  public void testGetValuesForPrefix() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("TEST", 1);
    tree.put("TEAM", 2);
    tree.put("TOAST", 3);
    tree.put("TEA", 4);
    tree.put("COFFEE", 5);

    assertEquals("[5, 4, 2, 1, 3]", Iterables.toString(tree.getValuesForKeyStartingWith("")));
    assertEquals("[5]", Iterables.toString(tree.getValuesForKeyStartingWith("C")));
    assertEquals("[5]", Iterables.toString(tree.getValuesForKeyStartingWith("COFFEE")));
    assertEquals("[]", Iterables.toString(tree.getValuesForKeyStartingWith("COFFEES")));
    assertEquals("[4, 2, 1, 3]", Iterables.toString(tree.getValuesForKeyStartingWith("T")));
    assertEquals("[4, 2, 1]", Iterables.toString(tree.getValuesForKeyStartingWith("TE")));
    assertEquals("[4, 2]", Iterables.toString(tree.getValuesForKeyStartingWith("TEA")));
    assertEquals("[3]", Iterables.toString(tree.getValuesForKeyStartingWith("TO")));
  }

  @Test
  public void testGetClosetKeys() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("COD", 1);
    tree.put("CODFISH", 2);
    tree.put("COFFEE", 3);

    assertEquals("[COD, CODFISH, COFFEE]", Iterables.toString(tree.getClosestKeys("COW")));
    assertEquals("[COD, CODFISH, COFFEE]", Iterables.toString(tree.getClosestKeys("CX")));
    assertEquals("[COD, CODFISH]", Iterables.toString(tree.getClosestKeys("COD")));
    assertEquals("[COFFEE]", Iterables.toString(tree.getClosestKeys("COF")));
    assertEquals("[CODFISH]", Iterables.toString(tree.getClosestKeys("CODF")));
    assertEquals("[CODFISH]", Iterables.toString(tree.getClosestKeys("CODFISHASDASD")));
  }

  @Test
  public void testGetClosetValues() {
    ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<>(getNodeFactory());
    tree.put("COD", 1);
    tree.put("CODFISH", 2);
    tree.put("COFFEE", 3);

    assertEquals("[1, 2, 3]", Iterables.toString(tree.getClosestValues("COW")));
    assertEquals("[1, 2, 3]", Iterables.toString(tree.getClosestValues("CX")));
    assertEquals("[1, 2]", Iterables.toString(tree.getClosestValues("COD")));
    assertEquals("[3]", Iterables.toString(tree.getClosestValues("COF")));
    assertEquals("[2]", Iterables.toString(tree.getClosestValues("CODF")));
    assertEquals("[2]", Iterables.toString(tree.getClosestValues("CODFISHASDASD")));
  }
}
