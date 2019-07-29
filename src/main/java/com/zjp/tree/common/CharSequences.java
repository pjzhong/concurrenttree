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
    return new StringBuilder().append(charSequence).toString();
  }

  public static CharSequence getCommonPrefix(CharSequence a, CharSequence b) {
    int minLength = Math.min(a.length(), b.length());
    for (int i = 0; i < minLength; i++) {
      if (a.charAt(i) != b.charAt(i)) {
        return a.subSequence(0, i);
      }
    }
    return a.subSequence(0, minLength);
  }

  public static CharSequence subtractPrefix(CharSequence main, CharSequence prefix) {
    int startIdx = prefix.length();
    int mainLength = main.length();
    if (startIdx > mainLength) {
      return "";
    }
    return main.subSequence(startIdx, mainLength);
  }
}
