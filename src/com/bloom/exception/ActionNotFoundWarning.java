package com.bloom.exception;

public class ActionNotFoundWarning
  extends Warning
{
  public ActionNotFoundWarning() {}
  
  public ActionNotFoundWarning(String s)
  {
    super(s);
  }
  
  public ActionNotFoundWarning(String s, Throwable t)
  {
    super(s, t);
  }
  
  public ActionNotFoundWarning(Throwable cause)
  {
    super(cause);
  }
  
  public ActionNotFoundWarning(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
