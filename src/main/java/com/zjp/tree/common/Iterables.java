package com.zjp.tree.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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

  public static <T> List<T> toList(Iterable<T> iterable) {
    if (iterable instanceof Collection) {
      return new ArrayList<>((Collection<T>) iterable);
    } else {
      List<T> list = new LinkedList<>();
      for (T e : iterable) {
        list.add(e);
      }
      return list;
    }
  }


}
