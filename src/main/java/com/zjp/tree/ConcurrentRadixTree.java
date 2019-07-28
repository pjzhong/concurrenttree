package com.zjp.tree;

import com.zjp.tree.ConcurrentRadixTree.SearchResult.Classification;
import com.zjp.tree.common.CharSequences;
import com.zjp.tree.common.PrettyPrinter;
import com.zjp.tree.node.Node;
import com.zjp.tree.node.NodeFactory;
import com.zjp.tree.node.util.PrettyPrintable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ConcurrentRadixTree<O> implements PrettyPrintable {

  private final NodeFactory nodeFactory;

  protected volatile Node root;

  public Node getRoot() {
    return root;
  }

  public ConcurrentRadixTree(NodeFactory nodeFactory) {
    this.nodeFactory = nodeFactory;
    this.root = nodeFactory.createNode("", null, Collections.emptyList(), true);
  }

  public O getValueForExactKey(CharSequence key) {
    SearchResult searchResult = searchTree(key);
    if (searchResult.classification.equals(Classification.EXACT_MATCH)) {
      @SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
      O value = (O) searchResult.node.getValue();
      return value;
    }
    return null;
  }

  public O put(CharSequence key, O value) {
    @SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
    O existing = (O) putInternal(key, value, false);
    return existing;
  }

  Object putInternal(CharSequence key, Object value, boolean overwrite) {
    Objects.requireNonNull(key);
    Objects.requireNonNull(value);
    if (key.length() == 0) {
      throw new NullPointerException("key value is Empty");
    }

    SearchResult searchResult = searchTree(key);
    Classification classification = searchResult.classification;

    switch (classification) {
      case EXACT_MATCH: {
        Object existing = searchResult.node.getValue();
        if (!overwrite && existing != null) {
          return existing;
        }

        Node replacement = nodeFactory.createNode(key, value,
            searchResult.node.getOutgoingEdges(), false);
        searchResult.parent.updateOutgoingEdge(replacement);
        return existing;
      }
      case KEY_ENDS_MID_EDGE: {
        Node node = searchResult.node;
        CharSequence keyCharsFromStartOfNodeFound = key
            .subSequence(searchResult.matched - searchResult.found, key.length());
        CharSequence commonPrefix = CharSequences
            .getCommonPrefix(keyCharsFromStartOfNodeFound, node.getIncomingEdge());
        CharSequence suffixFromExistingEdge = CharSequences
            .subtractPrefix(node.getIncomingEdge(), commonPrefix);

        Node newChild = nodeFactory
            .createNode(suffixFromExistingEdge, node.getValue(), node.getOutgoingEdges(), false);
        Node newParent = nodeFactory
            .createNode(commonPrefix, value, Collections.singletonList(newChild), false);

        searchResult.parent.updateOutgoingEdge(newParent);

        return null;
      }
      case INCOMPLETE_MATCH_TO_END_OF_EDGE: {
        CharSequence keySuffix = key.subSequence(searchResult.matched, key.length());
        Node newChild = nodeFactory.createNode(keySuffix, value, Collections.emptyList(), false);

        Node node = searchResult.node;

        // Clone the current node adding the new child
        List<Node> edges = new ArrayList<>(node.getOutgoingEdges().size() + 1);
        edges.addAll(node.getOutgoingEdges());
        edges.add(newChild);
        Node clonedNode = nodeFactory
            .createNode(node.getIncomingEdge(), node.getValue(), edges, node == root);

        if (node == root) {
          this.root = clonedNode;
        } else {
          searchResult.parent.updateOutgoingEdge(clonedNode);
        }

        return null;
      }
      case INCOMPLETE_MATCH_TO_MIDDLE_OF_EDGE: {
        //Search found a difference in characters between the kye and the characters in the middle
        // of the edge in the current node, adn the key still has trailing unmatched characters.
        // -> Split the node in three:
        // Let's call node found:NF
        // 1.Create a new node N1 containing the unmatched characters from the rest of the day,
        // 2.Create a new node N2 containing the unmatched characters from the rest of the edge in NF,
        // and copy the original edges and the value from NF unmodified into N2
        // 3.Create a new node N3, which will be the split node, containing the matched characters from
        // the key and the edge, and add N1 and N2 as child node of N3
        // Re-add N3 to the parent node of NF, effectively replacing NF in the tree

        Node node = searchResult.node;
        CharSequence keyCharsFromStartOfNodeFound = key
            .subSequence(searchResult.matched - searchResult.found, key.length());
        CharSequence commonPrefix = CharSequences
            .getCommonPrefix(keyCharsFromStartOfNodeFound, node.getIncomingEdge());
        CharSequence suffixFromExistingEdge = CharSequences
            .subtractPrefix(node.getIncomingEdge(), commonPrefix);
        CharSequence suffixFromKey = key.subSequence(searchResult.matched, key.length());

        // Create new nodes...
        Node n1 = nodeFactory.createNode(suffixFromKey, value, Collections.emptyList(), false);
        Node n2 = nodeFactory
            .createNode(suffixFromExistingEdge, node.getValue(), node.getOutgoingEdges(), false);
        Node n3 = nodeFactory.createNode(commonPrefix, null, Arrays.asList(n1, n2), false);

        searchResult.parent.updateOutgoingEdge(n3);

        return null;
      }
      default: {
        throw new IllegalStateException(
            "Unexpected classification for search result: "
                + searchResult);
      }
    }
  }

  SearchResult searchTree(CharSequence key) {
    Node grandParent = null;
    Node parent = null;
    Node current = root;

    int matched = 0, found = 0;
    final int keyLength = key.length();
    outer_loop:
    while (matched < keyLength) {
      Node nextNode = current.getOutgoingEdge(key.charAt(matched));
      if (nextNode == null) {
        break;
      }

      grandParent = parent;
      parent = current;
      current = nextNode;
      found = 0;
      CharSequence characters = current.getIncomingEdge();
      for (int i = 0, size = characters.length(); i < size
          && matched < keyLength; i++) {
        if (characters.charAt(i) != key.charAt(matched)) {
          break outer_loop;
        }
        matched++;
        found++;
      }
    }

    return new SearchResult(key, current, matched, found, parent,
        grandParent);
  }

  @Override
  public Node getNode() {
    return root;
  }

  static class SearchResult {

    final CharSequence key;
    final int matched;
    final int found;
    final Node node;
    final Node parent;
    final Node grandParent;
    final Classification classification;

    enum Classification {
      EXACT_MATCH,
      INCOMPLETE_MATCH_TO_END_OF_EDGE,
      INCOMPLETE_MATCH_TO_MIDDLE_OF_EDGE,
      KEY_ENDS_MID_EDGE,
      INVALID // INVALID
    }

    SearchResult(CharSequence key, Node node, int matched, int found,
        Node parent, Node grandParent) {
      this.key = key;
      this.node = node;
      this.matched = matched;
      this.found = found;
      this.parent = parent;
      this.grandParent = grandParent;

      // Classify this search result...
      this.classification = classify(key, node, matched, found);
    }

    protected Classification classify(CharSequence key, Node nodeFound,
        int charsMatched, int charsMatchedInNodeFound) {
      if (charsMatched == key.length()) {
        if (charsMatchedInNodeFound == nodeFound.getIncomingEdge()
            .length()) {
          return Classification.EXACT_MATCH;
        } else if (charsMatchedInNodeFound < nodeFound.getIncomingEdge()
            .length()) {
          return Classification.KEY_ENDS_MID_EDGE;
        }
      } else if (charsMatched < key.length()) {
        if (charsMatchedInNodeFound == nodeFound.getIncomingEdge()
            .length()) {
          return Classification.INCOMPLETE_MATCH_TO_END_OF_EDGE;
        } else if (charsMatchedInNodeFound < nodeFound.getIncomingEdge()
            .length()) {
          return Classification.INCOMPLETE_MATCH_TO_MIDDLE_OF_EDGE;
        }
      }
      throw new IllegalStateException(
          "Unexpected failure to classify SearchResult: " + this);
    }

    @Override
    public String toString() {
      return "SearchResult{" + "key=" + key + ", node=" + node
          + ", matched=" + matched + ", found=" + found + ", parent="
          + parent + ", grandParent=" + grandParent
          + ", classification=" + classification + '}';
    }
  }
}
