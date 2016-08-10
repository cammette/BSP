package com.bloom.exception;

import com.esotericsoftware.kryonet.rmi.TimeoutException;

public class RuntimeInterruptedException
  extends RuntimeException
{
  public RuntimeInterruptedException(TimeoutException e)
  {
    super(e);
  }
  
  public RuntimeInterruptedException(Exception e)
  {
    super(e);
  }
}
