package com.pjzhong.tree.common;

import static org.junit.Assert.assertEquals;

import com.zjp.tree.common.CharSequences;
import org.junit.Test;

public class CharSequencesTest {

  @Test
  public void testGetCommonPrefix() {
    assertEquals("BAN", CharSequences.getCommonPrefix("BANANA", "BANDANA"));
    assertEquals("BAN", CharSequences.getCommonPrefix("BAN", "BANDANA"));
    assertEquals("BAN", CharSequences.getCommonPrefix("BANDANA", "BAN"));
    assertEquals("", CharSequences.getCommonPrefix("BANDANA", "ABANANA"));
    assertEquals("", CharSequences.getCommonPrefix("", "BANDANA"));
    assertEquals("", CharSequences.getCommonPrefix("BANDANA", ""));
    assertEquals("T", CharSequences.getCommonPrefix("TOAST", "TEAM"));
  }

  @Test
  public void testGetSuffix() {
    String str = "BANANA";
    assertEquals("BANANA", CharSequences.getSuffix(str, 0));
    assertEquals("ANA", CharSequences.getSuffix(str, 3));
    assertEquals("", CharSequences.getSuffix(str, 6));
    assertEquals("", CharSequences.getSuffix(str, 7));
  }

  @Test
  public void testSubtractPrefix() {
    assertEquals("JJ", CharSequences.subtractPrefix("ZZZZJJ", "ZZZZ"));
    assertEquals("", CharSequences.subtractPrefix("ZZZZJJ", "ZZZZJJ"));
    assertEquals("", CharSequences.subtractPrefix("ZZZZJJ", "ZZZZJJPPPPP"));
    assertEquals("", CharSequences.subtractPrefix("", "ZZZZJJ"));
    assertEquals("ZZZZJJ", CharSequences.subtractPrefix("ZZZZJJ", ""));

  }

}
