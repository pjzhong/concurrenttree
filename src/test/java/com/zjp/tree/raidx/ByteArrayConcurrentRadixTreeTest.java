package com.zjp.tree.raidx;

import com.zjp.tree.node.NodeFactory;
import com.zjp.tree.node.impl.bytearry.DefaultByteArrayNodeFactory;

public class ByteArrayConcurrentRadixTreeTest extends ConcurrentRadixTreeTest {

  @Override
  public NodeFactory getNodeFactory() {
    return new DefaultByteArrayNodeFactory();
  }
}
