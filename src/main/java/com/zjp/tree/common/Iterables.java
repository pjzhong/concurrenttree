package com.zjp.tree.common;

import java.util.Iterator;

public class Iterables {

  public static String toString(Iterable<?> iterable) {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (Iterator<?> i = iterable.iterator(); i.hasNext(); ) {
      sb.append(i.next());
      if (i.hasNext()) {
        sb.append(", ");
      }
    }
    sb.append("]");
    return sb.toString();
  }
}
