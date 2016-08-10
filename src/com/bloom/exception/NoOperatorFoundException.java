package com.bloom.exception;

public class NoOperatorFoundException
  extends RuntimeException
{
  private static final long serialVersionUID = 7671189737451328327L;
  
  public NoOperatorFoundException() {}
  
  public NoOperatorFoundException(String s)
  {
    super(s);
  }
  
  public NoOperatorFoundException(String s, Throwable t)
  {
    super(s, t);
  }
  
  public NoOperatorFoundException(Throwable cause)
  {
    super(cause);
  }
  
  public NoOperatorFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
