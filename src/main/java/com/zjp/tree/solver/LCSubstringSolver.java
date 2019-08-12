package com.zjp.tree.solver;

import com.zjp.tree.ConcurrentRadixTree;
import com.zjp.tree.common.CharSequences;
import com.zjp.tree.node.Node;
import com.zjp.tree.node.NodeFactory;
import com.zjp.tree.node.impl.bytearry.ByteArrayCharSequence;
import com.zjp.tree.node.util.PrettyPrintable;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LCSubstringSolver implements PrettyPrintable {

  class ConcurrentSuffixTreeImpl<V> extends ConcurrentRadixTree<V> {

    public ConcurrentSuffixTreeImpl(NodeFactory nodeFactory) {
      super(nodeFactory);
    }


    // Override to make accessible to outer class...
    @Override
    protected void acquireWriteLock() {
      super.acquireWriteLock();
    }

    @Override
    protected void releaseWriteLock() {
      super.releaseWriteLock();
    }

    @Override
    protected Iterable<NodeKeyPair> lazyTraverseDescendants(CharSequence startKey, Node startNode) {
      return super.lazyTraverseDescendants(startKey, startNode);
    }

    CharSequence getLongestCommonSubstring() {
      Node root = suffixTree.getNode();
      CharSequence longestSoFar = null;
      int longestSoFarLength = 0;

      for (NodeKeyPair pair : lazyTraverseDescendants("", root)) {
        if (pair.key.length() > longestSoFarLength && subTreeReferencesAllDocuments(pair.key,
            pair.node)) {
          longestSoFarLength = pair.key.length();
          longestSoFar = pair.key;
        }
      }

      return longestSoFar;
    }

    boolean subTreeReferencesAllDocuments(CharSequence startKey, Node startNode) {
      Set<String> encountered = new HashSet<>(documents.size());
      for (NodeKeyPair pair : lazyTraverseDescendants(startKey, startNode)) {
        Set<String> strs = (Set<String>) pair.node.getValue();
        if (strs != null) {
          encountered.addAll(strs);
          if (encountered.equals(documents)) {
            return true;
          }
        }
      }

      return false;
    }
  }

  final ConcurrentSuffixTreeImpl<Set<String>> suffixTree;
  final Set<String> documents;


  public LCSubstringSolver(NodeFactory nodeFactory) {
    this.suffixTree = new ConcurrentSuffixTreeImpl<>(nodeFactory);
    this.documents = Collections.newSetFromMap(new ConcurrentHashMap<>());
  }

  public boolean add(CharSequence doc) {
    Objects.requireNonNull(doc);
    if (doc.length() == 0) {
      throw new NullPointerException("document is empty");
    }
    String documentString = CharSequences.toString(doc);

    boolean added = documents.add(documentString);
    if (added) {
      addSuffixesToRadixTree(documentString);
    }
    return added;
  }

  void addSuffixesToRadixTree(String keyAsString) {
    ByteArrayCharSequence bacs = new ByteArrayCharSequence(
        keyAsString.getBytes(StandardCharsets.UTF_8), 0, keyAsString.length());
    Iterable<CharSequence> suffixes = CharSequences.generateSuffixes(bacs);
    for (CharSequence suffix : suffixes) {
      Set<String> originalKeyRefs = suffixTree.getValueForExactKey(suffix);
      if (originalKeyRefs == null) {
        originalKeyRefs = Collections.newSetFromMap(new ConcurrentHashMap<>());
        Set<String> res = suffixTree.putIfAbsent(suffix, originalKeyRefs);
        if (res != null) {
          originalKeyRefs = res;
        }
      }
      originalKeyRefs.add(keyAsString);
    }
  }

  @Override
  public Node getNode() {
    return suffixTree.getNode();
  }

  public CharSequence getLongestCommonSubstring() {
    return suffixTree.getLongestCommonSubstring();
  }

}
