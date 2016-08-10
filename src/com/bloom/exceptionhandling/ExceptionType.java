package com.bloom.exceptionhandling;

import java.util.Map;

import com.bloom.runtime.ActionType;

public enum ExceptionType
{
  ArithmeticException,  NumberFormatException,  ClassCastException,  InvalidDataException,  NullPointerException,  SystemException,  ConnectionException,  UnknownException;
  
  private ExceptionType() {}
  
  public static ExceptionType getType(String val)
  {
    for (ExceptionType et : ExceptionType.values()) {
      if (et.name().equalsIgnoreCase(val)) {
        return et;
      }
    }
    return UnknownException;
  }
  
  public static ExceptionType getExceptionType(Exception ex)
  {
    if (ex == null) {
      return UnknownException;
    }
    String name = ex.getClass().getSimpleName();
    return getType(name);
  }
  
  public static boolean contains(String val)
  {
    for (ExceptionType et : ExceptionType.values()) {
      if (et.name().equalsIgnoreCase(val)) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean validate(Map<String, Object> ehandlers)
  {
    if ((ehandlers == null) || (ehandlers.isEmpty())) {
      return true;
    }
    for (String et : ehandlers.keySet())
    {
      if (!contains(et)) {
        return false;
      }
      String val = (String)ehandlers.get(et);
      if (!ActionType.contains(val)) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean isDataException(Throwable ex)
  {
    if (ex == null) {
      return false;
    }
    String str = ex.getClass().getSimpleName();
    int ik = str.lastIndexOf(".");
    if (ik < 0) {
      str = str.substring(0, str.length());
    } else {
      str = str.substring(str.lastIndexOf(".") + 1, str.length());
    }
    return contains(str);
  }
}
