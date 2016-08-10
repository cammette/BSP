package com.bloom.exception;

public class AlterException
  extends FatalException
{
  public AlterException() {}
  
  public AlterException(String s)
  {
    super(s);
  }
  
  public AlterException(String s, Throwable t)
  {
    super(s, t);
  }
  
  public AlterException(Throwable cause)
  {
    super(cause);
  }
  
  public AlterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
