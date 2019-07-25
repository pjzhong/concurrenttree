package com.zjp.tree.common;

public class CharSequences {

  public static char[] toCharArray(CharSequence charSequence) {
    final int numChars = charSequence.length();
    char[] charArray = new char[numChars];
    for (int i = 0; i < numChars; i++) {
      charArray[i] = charSequence.charAt(i);
    }
    return charArray;
  }

  public static String toString(CharSequence charSequence) {
    if (charSequence == null) {
      return null;
    }
    if (charSequence instanceof String) {
      return (String) charSequence;
    }
    return new StringBuilder(charSequence.length()).append(charSequence).toString();
  }

}
