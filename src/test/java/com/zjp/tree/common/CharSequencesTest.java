package com.zjp.tree.common;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
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
  public void testGetPrefix() {
    String str = "BANANA";
    assertEquals("", CharSequences.getPrefix(str, 0));
    assertEquals("B", CharSequences.getPrefix(str, 1));
    assertEquals("BAN", CharSequences.getPrefix(str, 3));
    assertEquals("BANANA", CharSequences.getPrefix(str, 6));
    assertEquals("BANANA", CharSequences.getPrefix(str, 7));
  }

  @Test
  public void testSubtractPrefix() {
    assertEquals("JJ", CharSequences.subtractPrefix("ZZZZJJ", "ZZZZ"));
    assertEquals("", CharSequences.subtractPrefix("ZZZZJJ", "ZZZZJJ"));
    assertEquals("", CharSequences.subtractPrefix("ZZZZJJ", "ZZZZJJPPPPP"));
    assertEquals("", CharSequences.subtractPrefix("", "ZZZZJJ"));
    assertEquals("ZZZZJJ", CharSequences.subtractPrefix("ZZZZJJ", ""));

  }

  @Test
  public void testGenerateSuffixes() {
    CharSequence input = "BANANAS";
    List<CharSequence> expected = Arrays.asList("BANANAS", "ANANAS", "NANAS", "ANAS", "NAS", "AS", "S");
    int index = 0;
    for (CharSequence c : CharSequences.generateSuffixes(input)) {
      assertEquals(expected.get(index++), c);
    }

  }

}
