package com.bloom.exception;

public class FatalException
  extends RuntimeException
{
  private static final long serialVersionUID = 7671189737451328327L;
  
  public FatalException() {}
  
  public FatalException(String s)
  {
    super(s);
  }
  
  public FatalException(String s, Throwable t)
  {
    super(s, t);
  }
  
  public FatalException(Throwable cause)
  {
    super(cause);
  }
  
  public FatalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
