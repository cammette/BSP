package com.bloom.exception;

public class CompilationException
  extends FatalException
{
  private static final long serialVersionUID = 3843494709186515537L;
  
  public CompilationException() {}
  
  public CompilationException(String message)
  {
    super(message);
  }
  
  public CompilationException(Throwable cause)
  {
    super(cause);
  }
  
  public CompilationException(String message, Throwable cause)
  {
    super(message, cause);
  }
  
  public CompilationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
