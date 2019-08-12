package com.zjp.tree.common;

import static org.junit.Assert.assertEquals;

import com.zjp.tree.node.impl.bytearry.ByteArrayCharSequence;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class ByteArrayCharSequenceTest {

  @Test
  public void testLength() {
    byte[] bytes = "FOOBAR".getBytes(StandardCharsets.UTF_8);
    ByteArrayCharSequence bacs = new ByteArrayCharSequence(bytes, 0, bytes.length);
    assertEquals(6, bacs.length());
  }

  @Test
  public void testCharAt() {
    byte[] bytes = "FOOBAR".getBytes(StandardCharsets.UTF_8);
    {
      ByteArrayCharSequence bacs = new ByteArrayCharSequence(bytes, 0, bytes.length);
      int i = 0;
      assertEquals('F', bacs.charAt(i++));
      assertEquals('O', bacs.charAt(i++));
      assertEquals('O', bacs.charAt(i++));
      assertEquals('B', bacs.charAt(i++));
      assertEquals('A', bacs.charAt(i++));
      assertEquals('R', bacs.charAt(i));
    }

    {
      ByteArrayCharSequence bacs = new ByteArrayCharSequence(bytes, 1, 5);
      int i = 0;
      assertEquals('O', bacs.charAt(i++));
      assertEquals('O', bacs.charAt(i++));
      assertEquals('B', bacs.charAt(i++));
      assertEquals('A', bacs.charAt(i));
    }
  }

  @Test
  public void testSubSequenceLength() {
    byte[] bytes = "FOOBAR".getBytes(StandardCharsets.UTF_8);
    ByteArrayCharSequence bacs = new ByteArrayCharSequence(bytes, 0, bytes.length);
    ByteArrayCharSequence subSequence = bacs.subSequence(1, 5);// OOBA
    assertEquals(4, subSequence.length());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeStart() {
    byte[] bytes = "FOOBAR".getBytes(StandardCharsets.UTF_8);
    new ByteArrayCharSequence(bytes, -1, bytes.length);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidLength() {
    byte[] bytes = "FOOBAR".getBytes(StandardCharsets.UTF_8);
    new ByteArrayCharSequence(bytes, 1, 7);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartGreaterThanEnd() {
    byte[] bytes = "FOOBAR".getBytes(StandardCharsets.UTF_8);
    new ByteArrayCharSequence(bytes, 2, 1);
  }

  @Test
  public void testSubSequenceCharaAt() {
    byte[] bytes = "FOOBAR".getBytes(StandardCharsets.UTF_8);
    ByteArrayCharSequence basc = new ByteArrayCharSequence(bytes, 0, bytes.length);
    ByteArrayCharSequence subSequence = basc.subSequence(1, 5);//OOBA
    int i = 0;
    assertEquals(4, subSequence.length());
    assertEquals('O', subSequence.charAt(i++));
    assertEquals('O', subSequence.charAt(i++));
    assertEquals('B', subSequence.charAt(i++));
    assertEquals('A', subSequence.charAt(i));

    ByteArrayCharSequence subSequence2 = subSequence.subSequence(1, 3);
    int j = 0;
    assertEquals(2, subSequence2.length());
    assertEquals('O', subSequence2.charAt(j++));
    assertEquals('B', subSequence2.charAt(j));
  }

  @Test
  public void testGenerateSuffixes() {
    String input = "BANANAS";
    ByteArrayCharSequence bacs = new ByteArrayCharSequence(input.getBytes(StandardCharsets.UTF_8),
        0, input.length());
    List<CharSequence> expected = Arrays
        .asList("BANANAS", "ANANAS", "NANAS", "ANAS", "NAS", "AS", "S");
    assertEquals(expected.toString(), Iterables.toString(CharSequences.generateSuffixes(bacs)));
  }

  @Test
  public void testToString() {
    byte[] bytes = "FOOBAR".getBytes(StandardCharsets.UTF_8);
    ByteArrayCharSequence bacs = new ByteArrayCharSequence(bytes, 0, bytes.length);
    assertEquals("FOOBAR", bacs.toString());
  }

  @Test
  public void testEncodeUtf8() {
    byte[] bytes = "FOOBAR".getBytes(StandardCharsets.UTF_8);
    assertEquals(Arrays.toString(bytes),
        Arrays.toString(ByteArrayCharSequence.toSingleByteUtf8Encoding("FOOBAR")));
  }

  @Test(expected = IllegalStateException.class)
  public void testEncodeUtf8_UnsupportedChar() {
    ByteArrayCharSequence.toSingleByteUtf8Encoding("FOOBAR○");
  }


}
