package com.bloom.runtime.meta;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class Graph
  extends HashMap
{
  public Set get(Object key)
  {
    synchronized (key)
    {
      if (key == null) {
        return null;
      }
      if (super.get(key) == null) {
        super.put(key, new LinkedHashSet());
      }
      return (Set)super.get(key);
    }
  }
}
