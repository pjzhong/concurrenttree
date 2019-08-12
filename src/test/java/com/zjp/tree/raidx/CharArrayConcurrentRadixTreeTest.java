package com.zjp.tree.raidx;

import com.zjp.tree.node.NodeFactory;
import com.zjp.tree.node.impl.DefaultCharSequenceNodeFactory;

public class CharArrayConcurrentRadixTreeTest extends ConcurrentRadixTreeTest {

  @Override
  public NodeFactory getNodeFactory() {
    return new DefaultCharSequenceNodeFactory();
  }
}
