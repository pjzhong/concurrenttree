package com.zjp.tree.common;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class LazyIterator<T> implements Iterator<T> {

  T next = null;

  enum State {READY, NOT_READY, DONE, FAILED}

  State state = State.NOT_READY;

  @Override
  public boolean hasNext() {
    if(state == State.FAILED) {
      throw new IllegalStateException(
          "This iterator is in an inconsistent state, and can no longer be used, " +
              "due to an exception previously thrown by the computeNext() method");
    }
    switch (state) {
      case DONE:return false;
      case READY:return true;
    }
    return tryToComputeNext();
  }

  boolean tryToComputeNext() {
    state = State.FAILED;
    next = computeNext();
    if (state != State.DONE) {
      state = State.READY;
      return true;
    }
    return false;
  }

  @Override
  public T next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    state = State.NOT_READY;
    return next;
  }

  protected final T endOfData() {
    state = State.DONE;
    return null;
  }

  protected abstract T computeNext();
}
