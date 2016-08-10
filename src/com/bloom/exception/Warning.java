package com.bloom.exception;

public class Warning
  extends RuntimeException
{
  private static final long serialVersionUID = -3680640083544369451L;
  
  public Warning() {}
  
  public Warning(String s)
  {
    super(s);
  }
  
  public Warning(String s, Throwable t)
  {
    super(s, t);
  }
  
  public Warning(Throwable cause)
  {
    super(cause);
  }
  
  public Warning(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
