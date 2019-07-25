package com.zjp.tree;

import com.zjp.tree.ConcurrentRadixTree.SearchResult.Classification;
import com.zjp.tree.node.Node;
import com.zjp.tree.node.NodeFactory;
import java.util.Objects;

public class ConcurrentRadixTree<O> {

  private NodeFactory nodeFactory;

  protected volatile Node root;

  public O put(CharSequence key, O value) {
    return null;
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

        Node replacement = nodeFactory
            .createNode(key, value, searchResult.node.getOutgoingEdges(), false);
        searchResult.parent.updateOutgoingEdge(replacement);
        return existing;
      }
      default: {
        throw new IllegalStateException(
            "Unexpected classification for search result: " + searchResult);
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
        break outer_loop;
      }

      grandParent = parent;
      parent = current;
      current = nextNode;
      found = 0;
      CharSequence characters = current.getIncomingEdge();
      for (int i = 0, size = characters.length(); i < size && matched < keyLength;
          i++) {
        if (characters.charAt(i) != key.charAt(matched)) {
          break outer_loop;
        }
        matched++;
        found++;
      }
    }

    return new SearchResult(key, current, matched, found, parent, grandParent);
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
      INVALID // INVALID is never used, except in unit testing
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

    protected Classification classify(CharSequence key, Node nodeFound, int charsMatched,
        int charsMatchedInNodeFound) {
      if (charsMatched == key.length()) {
        if (charsMatchedInNodeFound == nodeFound.getIncomingEdge().length()) {
          return Classification.EXACT_MATCH;
        } else if (charsMatchedInNodeFound < nodeFound.getIncomingEdge().length()) {
          return Classification.KEY_ENDS_MID_EDGE;
        }
      } else if (charsMatched < key.length()) {
        if (charsMatchedInNodeFound == nodeFound.getIncomingEdge().length()) {
          return Classification.INCOMPLETE_MATCH_TO_END_OF_EDGE;
        } else if (charsMatchedInNodeFound < nodeFound.getIncomingEdge().length()) {
          return Classification.INCOMPLETE_MATCH_TO_MIDDLE_OF_EDGE;
        }
      }
      throw new IllegalStateException("Unexpected failure to classify SearchResult: " + this);
    }

    @Override
    public String toString() {
      return "SearchResult{" +
          "key=" + key +
          ", node=" + node +
          ", matched=" + matched +
          ", found=" + found +
          ", parent=" + parent +
          ", grandParent=" + grandParent +
          ", classification=" + classification +
          '}';
    }
  }
}
