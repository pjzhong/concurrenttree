package com.zjp.tree;

import com.zjp.tree.ConcurrentRadixTree.SearchResult.Classification;
import com.zjp.tree.common.CharSequences;
import com.zjp.tree.common.LazyIterator;
import com.zjp.tree.node.Node;
import com.zjp.tree.node.NodeFactory;
import com.zjp.tree.node.util.PrettyPrintable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentRadixTree<O> implements PrettyPrintable {

  private final NodeFactory nodeFactory;

  protected volatile Node root;

  private final Lock writeLock = new ReentrantLock();

  public Node getRoot() {
    return root;
  }

  public ConcurrentRadixTree(NodeFactory nodeFactory) {
    this.nodeFactory = nodeFactory;
    this.root = nodeFactory.createNode("", null, Collections.emptyList(), true);
  }

  protected void acquireWriteLock() {
    writeLock.lock();
  }

  protected void releaseWriteLock() {
    writeLock.unlock();
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

  public Iterable<CharSequence> getKeysStartingWith(CharSequence prefix) {
    SearchResult result = searchTree(prefix);
    Classification type = result.classification;
    switch (type) {
      case EXACT_MATCH: {
        return getDescendantsKeys(prefix, result.node);
      }
      case KEY_ENDS_MID_EDGE: {
        // Append the remaining characters of the edge to the key.
        // For example if we search for CO, but first matching node was COFFEE,
        // the key associated with the first node should be COFFEE...
        CharSequence edgeSuffix = CharSequences
            .getSuffix(result.node.getIncomingEdge(), result.found);
        prefix = CharSequences.concatenate(prefix, edgeSuffix);
        return getDescendantsKeys(prefix, result.node);
      }
      default: {
        return Collections.emptySet();
      }
    }
  }

  public Iterable<O> getValuesForKeyStartingWith(CharSequence prefix) {
    SearchResult result = searchTree(prefix);
    Classification type = result.classification;
    switch (type) {
      case EXACT_MATCH: {
        return getDescendantsValues(prefix, result.node);
      }
      case KEY_ENDS_MID_EDGE: {
        // Append the remaining characters of the edge to the key.
        // For example if we search for CO, but first matching node was COFFEE,
        // the key associated with the first node should be COFFEE...
        CharSequence edgeSuffix = CharSequences
            .getSuffix(result.node.getIncomingEdge(), result.found);
        prefix = CharSequences.concatenate(prefix, edgeSuffix);
        return getDescendantsValues(prefix, result.node);
      }
      default: {
        return Collections.emptySet();
      }
    }
  }

  public Iterable<CharSequence> getClosestKeys(CharSequence candidate) {
    SearchResult search = searchTree(candidate);
    Classification type = search.classification;
    switch (type) {
      case EXACT_MATCH: {
        return getDescendantsKeys(candidate, search.node);
      }
      case KEY_ENDS_MID_EDGE: {
        // Append the remaining characters of the edge to the key.
        // For example if we searched fo CO, but first matching node was
        // COFFEE, the key associated with the first node should be COFFEE...
        CharSequence edgeSuffix = CharSequences
            .getSuffix(search.node.getIncomingEdge(), search.found);
        candidate = CharSequences.concatenate(candidate, edgeSuffix);
        return getDescendantsKeys(candidate, search.node);
      }
      case INCOMPLETE_MATCH_TO_MIDDLE_OF_EDGE: {
        // Example: if we searched for CX, but deepest matching node was CO,
        // the result should include node CO and its descendants...
        CharSequence keyOfParent = CharSequences
            .getPrefix(candidate, search.matched - search.found);
        CharSequence keyOfNode = CharSequences
            .concatenate(keyOfParent, search.node.getIncomingEdge());
        return getDescendantsKeys(keyOfNode, search.node);
      }
      case INCOMPLETE_MATCH_TO_END_OF_EDGE: {
        if (search.matched == 0) {
          //Closet match is the root node, we don't consider this a match for anything...
          break;
        }

        // Example: if we searched for COFFEE, but deepest matching node was CO,
        // the results should include node CO and its descendants...
        CharSequence keyOfNode = CharSequences.getPrefix(candidate, search.matched);
        return getDescendantsKeys(keyOfNode, search.node);
      }
    }
    return Collections.emptySet();
  }

  public Iterable<O> getClosestValues(CharSequence candidate) {
    SearchResult search = searchTree(candidate);
    Classification type = search.classification;
    switch (type) {
      case EXACT_MATCH: {
        return getDescendantsValues(candidate, search.node);
      }
      case KEY_ENDS_MID_EDGE: {
        // Append the remaining characters of the edge to the key.
        // For example if we searched fo CO, but first matching node was
        // COFFEE, the key associated with the first node should be COFFEE...
        CharSequence edgeSuffix = CharSequences
            .getSuffix(search.node.getIncomingEdge(), search.found);
        candidate = CharSequences.concatenate(candidate, edgeSuffix);
        return getDescendantsValues(candidate, search.node);
      }
      case INCOMPLETE_MATCH_TO_MIDDLE_OF_EDGE: {
        // Example: if we searched for CX, but deepest matching node was CO,
        // the result should include node CO and its descendants...
        CharSequence keyOfParnet = CharSequences
            .getPrefix(candidate, search.matched - search.found);
        CharSequence keyOfNode = CharSequences
            .concatenate(keyOfParnet, search.node.getIncomingEdge());
        return getDescendantsValues(keyOfNode, search.node);
      }
      case INCOMPLETE_MATCH_TO_END_OF_EDGE: {
        if (search.matched == 0) {
          //Closet match is the root node, we don't consider this a match for anything...
          break;
        }

        // Example: if we searched for COFFEE, but deepest matching node was CO,
        // the results should include node CO and its descendants...
        CharSequence keyOfNode = CharSequences.getPrefix(candidate, search.matched);
        return getDescendantsValues(keyOfNode, search.node);
      }
    }
    return Collections.emptySet();
  }

  public boolean remove(CharSequence key) {
    Objects.requireNonNull(key);

    acquireWriteLock();
    try {
      SearchResult result = searchTree(key);

      if (result.classification == Classification.EXACT_MATCH) {
        Node node = result.node;
        if (node.getValue() == null) {
          //This node was created automatically as a split between two branches
          //(implicit node） No need to remove it
          return false;
        }
        List<Node> childEdges = node.getOutgoingEdges();
        if (childEdges.size() > 1) {
          //This node has more than one child, so if we delete the value from this node, we still
          //node to leave a similar node in place to act as the split between the child edges.
          //Just delete the value associated with this node.
          Node cloned = nodeFactory
              .createNode(node.getIncomingEdge(), null, node.getOutgoingEdges(), false);
          //Re-add the replacement node to the parent...
          result.parent.updateOutgoingEdge(cloned);
        } else if (childEdges.size() == 1) {
          //Node has one child edge.
          //Create a new node which is the concatenation of the edges from this node and
          // its child, and which ahs the outgoing edges of the child and the value from the child.
          Node child = childEdges.get(0);
          CharSequence concatenateEdges = CharSequences
              .concatenate(node.getIncomingEdge(), child.getIncomingEdge());
          Node mergeNode = nodeFactory
              .createNode(concatenateEdges, child.getValue(), child.getOutgoingEdges(), false);
          // Re-add the merged node to the parent...
          result.parent.updateOutgoingEdge(mergeNode);
        } else {
          // Node has no children. Delete this node from its parent.
          // which involves re-creating the parent rather than simply updating it child edge
          // (this is why we need grandParent)
          // However if this would leave the parent with only one remaining child edge,
          // and the parent itself has no value(is a split node), and the parent is not the root node
          // (a special case which we never merge), then we also need to merge the parent with its
          // remaining child.

          Node parent = result.parent;
          List<Node> currentEdgesFromParent = parent.getOutgoingEdges();

          // Create a list of the outgoing edges of the parent which will remain
          // If we remove this child...
          // User a non-resizeable list, as sanity check to force ArraysIndexOutOfBounds...
          List<Node> newEdgesOfParent = Arrays
              .asList(new Node[currentEdgesFromParent.size() - 1]);
          for (int i = 0, added = 0, size = currentEdgesFromParent.size(); i < size; i++) {
            Node n = currentEdgesFromParent.get(i);
            if (n != node) {
              newEdgesOfParent.set(added++, n);
            }
          }

          //Node the parent might actually be the root node(which we should never merge)...
          boolean parentIsRoot = (parent == root);
          Node newParent;
          if (newEdgesOfParent.size() == 1 && parent.getValue() == null && !parentIsRoot) {
            // Parent is a non-root split node with only one remaining child, which can be merged.
            Node onlyChild = newEdgesOfParent.get(0);

            // Merge the parent with its only remaining child...
            CharSequence concatenatedEdges = CharSequences
                .concatenate(parent.getIncomingEdge(), onlyChild.getIncomingEdge());
            newParent = nodeFactory
                .createNode(concatenatedEdges, onlyChild.getValue(), onlyChild.getOutgoingEdges(),
                    parentIsRoot);
          } else {
            // Parent is a node which either has a value of its own, has more than one remaining
            // child, or is actually the root node(we never merge thr root node).
            // Create new parent node which is the same as is currently just without
            // the edge to the node being deleted.
            newParent = nodeFactory
                .createNode(parent.getIncomingEdge(), parent.getValue(), newEdgesOfParent,
                    parentIsRoot);
          }

          if (parentIsRoot) {
            // Replace the root node
            this.root = newParent;
          } else {
            // Re-add the parent node to its parent..
            result.grandParent.updateOutgoingEdge(newParent);
          }
        }
        return true;
      }
      return false;
    } finally {
      releaseWriteLock();
    }
  }

  public O put(CharSequence key, O value) {
    @SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
    O existing = (O) putInternal(key, value, true);
    return existing;
  }

  public O putIfAbsent(CharSequence key, O value) {
    @SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
    O existing = (O) putInternal(key, value, false);
    return existing;
  }

  public int size() {
    Deque<Node> stack = new LinkedList<>();
    stack.push(this.root);
    int count = 0;
    while (!stack.isEmpty()) {// code optimize from origin
      Node current = stack.pop();
      stack.addAll(current.getOutgoingEdges());
      if (current.getValue() != null) {
        count++;
      }
    }
    return count;
  }

  // ------------- Helper method for put() -------------

  Object putInternal(CharSequence key, Object value, boolean overwrite) {
    Objects.requireNonNull(key);
    Objects.requireNonNull(value);
    if (key.length() == 0) {
      throw new IllegalArgumentException("key value is Empty");
    }

    SearchResult searchResult = searchTree(key);
    Classification classification = searchResult.classification;

    acquireWriteLock();
    try {
      switch (classification) {
        case EXACT_MATCH: {
          Object existing = searchResult.node.getValue();
          if (overwrite || existing == null) {
            Node node = searchResult.node;
            Node replacement = nodeFactory
                .createNode(node.getIncomingEdge(), value, node.getOutgoingEdges(), false);
            searchResult.parent.updateOutgoingEdge(replacement);
          }
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
    } finally {
      releaseWriteLock();
    }
  }

  Iterable<CharSequence> getDescendantsKeys(CharSequence key, Node node) {
    return () -> new LazyIterator<CharSequence>() {
      Iterator<NodeKeyPair> descendants = lazyTraverseDescendants(key, node).iterator();

      @Override
      protected CharSequence computeNext() {
        while (descendants.hasNext()) {
          NodeKeyPair pair = descendants.next();
          Object value = pair.node.getValue();
          if (value != null) {
            return CharSequences.toString(pair.key);
          }
        }
        return endOfData();
      }
    };
  }

  Iterable<O> getDescendantsValues(CharSequence key, Node node) {
    return () -> new LazyIterator<O>() {
      Iterator<NodeKeyPair> descendants = lazyTraverseDescendants(key, node).iterator();

      @Override
      protected O computeNext() {
        while (descendants.hasNext()) {
          NodeKeyPair pair = descendants.next();
          Object value = pair.node.getValue();
          if (value != null) {
            return (O) value;
          }
        }
        return endOfData();
      }
    };
  }

  protected Iterable<NodeKeyPair> lazyTraverseDescendants(final CharSequence key, final Node node) {
    return () -> new LazyIterator<NodeKeyPair>() {

      Deque<NodeKeyPair> stack = new LinkedList<>();

      {
        stack.push(new NodeKeyPair(node, key));
      }

      @Override
      protected NodeKeyPair computeNext() {
        if (stack.isEmpty()) {
          return endOfData();
        }
        NodeKeyPair current = stack.pop();
        List<Node> childNodes = current.node.getOutgoingEdges();

        for (int i = childNodes.size(); i > 0; i--) {
          Node child = childNodes.get(i - 1);
          stack.push(new NodeKeyPair(child,
              CharSequences.concatenate(current.key, child.getIncomingEdge())));
        }
        return current;
      }
    };
  }

  protected static class NodeKeyPair {

    public final Node node;
    public final CharSequence key;

    public NodeKeyPair(Node node, CharSequence key) {
      this.node = node;
      this.key = key;
    }

    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder("NodeKeyPair{");
      sb.append("key=").append(key);
      sb.append('}');
      return sb.toString();
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
        int matched, int foundInNode) {
      if (matched == key.length()) {
        if (foundInNode == nodeFound.getIncomingEdge()
            .length()) {
          return Classification.EXACT_MATCH;
        } else if (foundInNode < nodeFound.getIncomingEdge()
            .length()) {
          return Classification.KEY_ENDS_MID_EDGE;
        }
      } else if (matched < key.length()) {
        if (foundInNode == nodeFound.getIncomingEdge()
            .length()) {
          return Classification.INCOMPLETE_MATCH_TO_END_OF_EDGE;
        } else if (foundInNode < nodeFound.getIncomingEdge()
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
