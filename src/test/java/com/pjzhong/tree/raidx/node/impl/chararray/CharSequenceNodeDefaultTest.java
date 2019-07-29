package com.pjzhong.tree.raidx.node.impl.chararray;

import com.zjp.tree.node.Node;
import com.zjp.tree.node.impl.CharSequenceNodeDefault;
import java.util.Collections;
import org.junit.Test;

public class CharSequenceNodeDefaultTest {

  @Test(expected = IllegalStateException.class)
  public void testUpdateOutgoingEdge_NoExistentEdge() {
    Node node = new CharSequenceNodeDefault("FOO", null, Collections.emptyList());
    node.updateOutgoingEdge(new CharSequenceNodeDefault("BAR", null, Collections.emptyList()));
  }
}
