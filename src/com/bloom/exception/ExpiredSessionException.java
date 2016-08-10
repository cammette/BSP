package com.bloom.exception;

public class ExpiredSessionException
  extends SecurityException
{
  private static final long serialVersionUID = -7001586791541788133L;
  
  public ExpiredSessionException() {}
  
  public ExpiredSessionException(String message)
  {
    super(message);
  }
  
  public ExpiredSessionException(Throwable cause)
  {
    super(cause);
  }
  
  public ExpiredSessionException(String message, Throwable cause)
  {
    super(message, cause);
  }
  
  public ExpiredSessionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
