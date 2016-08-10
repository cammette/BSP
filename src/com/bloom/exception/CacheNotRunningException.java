package com.bloom.exception;

public class CacheNotRunningException
  extends Warning
{
  private static final long serialVersionUID = 545426163340497235L;
  
  public CacheNotRunningException(String s)
  {
    super(s);
  }
  
  public CacheNotRunningException(String s, Throwable t)
  {
    super(s, t);
  }
}
