package com.bloom.exceptionhandling;

import com.bloom.runtime.ExceptionEvent;
import com.bloom.runtime.components.Subscriber;
import com.bloom.uuid.UUID;
import com.bloom.runtime.containers.IBatch;
import com.bloom.runtime.containers.ITaskEvent;
import com.bloom.runtime.containers.WAEvent;

import java.util.Queue;
import org.apache.log4j.Logger;

public class WAExceptionMgr
  implements Subscriber
{
  private static Logger logger = Logger.getLogger(WAExceptionMgr.class);
  public static final UUID ExceptionStreamUUID = new UUID("5619C7DF-2292-4535-BBE5-E376C5F5BC42");
  private static volatile WAExceptionMgr instance = null;
  private static final int LIMIT = 1000;
  private Queue<ExceptionEvent> exceptions = new FixedSizeQueue(1000);
  
  public static WAExceptionMgr get()
  {
    if (instance == null) {
      synchronized (WAExceptionMgr.class)
      {
        if (instance == null) {
          instance = new WAExceptionMgr();
        }
      }
    }
    return instance;
  }
  
  public String getName()
  {
    return "Global.ExceptionManager";
  }
  
  public void receive(Object linkID, ITaskEvent event)
    throws Exception
  {
    if (logger.isInfoEnabled()) {
      logger.info("received an exception object.");
    }
    synchronized (this.exceptions)
    {
      IBatch<WAEvent> ees = event.batch();
      if ((ees != null) && (ees.size() > 0)) {
        for (WAEvent we : ees)
        {
          Object ee = we.data;
          if ((ee instanceof ExceptionEvent)) {
            this.exceptions.add((ExceptionEvent)ee);
          }
        }
      }
    }
  }
}
