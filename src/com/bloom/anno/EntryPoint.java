package com.bloom.anno;

import java.lang.annotation.Annotation;

public @interface EntryPoint
{
  public static final int USEDBY_UI = 1;
  public static final int USEDBY_WACTIONSTORE = 2;
  
  int usedBy();
}

