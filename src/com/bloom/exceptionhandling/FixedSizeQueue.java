package com.bloom.exceptionhandling;

import java.util.LinkedList;

public final class FixedSizeQueue<E>
  extends LinkedList<E>
{
  private static final long serialVersionUID = -3499341921349077573L;
  private int limit;
  
  public FixedSizeQueue(int limit)
  {
    this.limit = limit;
  }
  
  public boolean add(E e)
  {
    super.add(e);
    while (size() > this.limit) {
      super.remove();
    }
    return true;
  }
}
