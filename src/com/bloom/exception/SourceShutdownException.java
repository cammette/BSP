package com.bloom.exception;

public class SourceShutdownException
  extends Exception
{
  private static final long serialVersionUID = -9007897871791754045L;
  
  public SourceShutdownException()
  {
    super("Source going to shutdown");
  }
  
  public SourceShutdownException(String message)
  {
    super(message);
  }
  
  public SourceShutdownException(Throwable cause)
  {
    super(cause);
  }
  
  public SourceShutdownException(String message, Throwable cause)
  {
    super(message, cause);
  }
  
  public SourceShutdownException(String message, Throwable cause, boolean enableSuppression, boolean writeableStackTrace)
  {
    super(message, cause, enableSuppression, writeableStackTrace);
  }
}
