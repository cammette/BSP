package com.bloom.exception;

public class CacheException
  extends Warning
{
  private static final long serialVersionUID = 545426163340497235L;
  
  public CacheException(String s)
  {
    super(s);
  }
  
  public CacheException(String s, Throwable t)
  {
    super(s, t);
  }
}
