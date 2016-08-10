package com.bloom.exception;

public class SecurityException
  extends Warning
{
  private static final long serialVersionUID = -7001586791561708133L;
  
  public SecurityException() {}
  
  public SecurityException(String message)
  {
    super(message);
  }
  
  public SecurityException(Throwable cause)
  {
    super(cause);
  }
  
  public SecurityException(String message, Throwable cause)
  {
    super(message, cause);
  }
  
  public SecurityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
