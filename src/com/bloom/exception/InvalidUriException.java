package com.bloom.exception;

public class InvalidUriException
  extends Warning
{
  public InvalidUriException(String s)
  {
    super(s);
  }
  
  public InvalidUriException(String s, Throwable t)
  {
    super(s, t);
  }
}
