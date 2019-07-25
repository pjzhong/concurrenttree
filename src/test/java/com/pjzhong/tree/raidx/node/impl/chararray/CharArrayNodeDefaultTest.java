package com.pjzhong.tree.raidx.node.impl.chararray;

import com.zjp.tree.node.Node;
import com.zjp.tree.node.impl.CharArrayNodeDefault;
import java.util.Collections;
import org.junit.Test;

public class CharArrayNodeDefaultTest {

  @Test(expected = IllegalStateException.class)
  public void testUpdateOutgoingEdge_NoExistentEdge() {
    Node node = new CharArrayNodeDefault("FOO", null, Collections.emptyList());
    node.updateOutgoingEdge(new CharArrayNodeDefault("BAR", null, Collections.emptyList()));
  }

}
