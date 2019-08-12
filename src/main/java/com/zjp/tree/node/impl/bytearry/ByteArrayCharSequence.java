package com.zjp.tree.node.impl.bytearry;

import com.zjp.tree.common.CharSequences;

public class ByteArrayCharSequence implements CharSequence {

  final byte[] bytes;
  final int start;
  final int end;

  public ByteArrayCharSequence(byte[] bytes, int start, int end) {
    if (start < 0) {
      throw new IllegalArgumentException("start " + start + " < 0");
    }
    if (end > bytes.length) {
      throw new IllegalArgumentException("end " + end + " > length " + bytes.length);
    }
    if (end < start) {
      throw new IllegalArgumentException("end " + end + " < start " + start);
    }
    this.bytes = bytes;
    this.start = start;
    this.end = end;
  }

  @Override
  public int length() {
    return end - start;
  }

  @Override
  public char charAt(int index) {
    return (char) (bytes[index + start] & 0xFF);
  }

  @Override
  public ByteArrayCharSequence subSequence(int start, int end) {
    if (start < 0) {
      throw new IllegalArgumentException(String.format("start %s < 0%n", start));
    }
    if (end > length()) {
      throw new IllegalArgumentException(String.format("end %s > length %s%n", start, length()));
    }
    if (end < start) {
      throw new IllegalArgumentException(String.format("end %s < start %s%n", end, start));
    }
    return new ByteArrayCharSequence(bytes, this.start + start, this.start + end);
  }

  @Override
  public String toString() {
    return CharSequences.toString(this);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CharSequence)) {
      return false;
    }
    CharSequence that = (CharSequence) o;
    return that.toString().equals(toString());
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  public static byte[] toSingleByteUtf8Encoding(CharSequence charSequence) {
    final int length = charSequence.length();
    byte[] bytes = new byte[length];
    for (int i = 0; i < length; i++) {
      char inputChar = charSequence.charAt(i);
      if (inputChar > 255) {
        throw new IncompatibleCharacterException(
            "Input contains a character which cannot be represented as a single byte in UTF-8: "
                + inputChar);
      }
      bytes[i] = (byte) inputChar;
    }
    return bytes;
  }

  public static class IncompatibleCharacterException extends IllegalStateException {

    public IncompatibleCharacterException(String s) {
      super(s);
    }
  }
}
