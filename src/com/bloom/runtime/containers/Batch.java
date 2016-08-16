package com.bloom.runtime.containers;

import com.bloom.runtime.KeyFactory;
import com.bloom.runtime.RecordKey;
import com.bloom.runtime.StreamEvent;
import com.bloom.runtime.containers.IBatch;
import com.bloom.runtime.containers.WAEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import scala.Tuple2;
import scala.collection.immutable.HashMap;
import scala.collection.immutable.Queue;

public abstract class Batch<T>
  implements IBatch
{
  private static final long serialVersionUID = 3418639571053079192L;
  
  public abstract java.util.Iterator<T> iterator();
  
  public abstract int size();
  
  public boolean isEmpty()
  {
    return size() == 0;
  }
  
  public Iterable first()
  {
    Iterable it = this;
    java.util.Iterator i$ = it.iterator();
    if (i$.hasNext())
    {
    	Iterable e = (Iterable)i$.next();
      return e;
    }
    return null;
  }
  
  public T last()
  {
    Iterable<T> it = this;
    T ret = null;
    for (T e : it) {
      ret = e;
    }
    return ret;
  }
  
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    String sep = "";
    sb.append("[");
    Iterable<T> it = this;
    for (T e : it)
    {
      sb.append(sep).append(e);
      sep = ",";
    }
    sb.append("]");
    return sb.toString();
  }
  
  private static final Batch emptyBatch = new Batch()
  {
    private static final long serialVersionUID = -7983831581625863170L;
    
    public java.util.Iterator<WAEvent> iterator()
    {
      return Collections.emptyIterator();
    }
    
    public int size()
    {
      return 0;
    }
  };
  
  public static Batch emptyBatch()
  {
    return emptyBatch;
  }
  
  public static Batch asBatch(final HashMap<RecordKey, WAEvent> b)
  {
    return new Batch()
    {
      private static final long serialVersionUID = 4242550963548968535L;
      
      public java.util.Iterator<WAEvent> iterator()
      {
        return new java.util.Iterator()
        {
          scala.collection.Iterator<Tuple2<RecordKey, WAEvent>> it = b.iterator();
          
          public boolean hasNext()
          {
            return this.it.hasNext();
          }
          
          public WAEvent next()
          {
            return (WAEvent)((Tuple2)this.it.next())._2;
          }
          
          public void remove()
          {
            throw new UnsupportedOperationException();
          }
        };
      }
      
      public int size()
      {
        return b.size();
      }
    };
  }
  
  private static class ColBatch<WAEvent>
    extends Batch
  {
    private static final long serialVersionUID = -6646847362508352538L;
    final Collection<WAEvent> col;
    
    ColBatch(Collection<WAEvent> col)
    {
      this.col = col;
    }
    
    public java.util.Iterator<WAEvent> iterator()
    {
      return this.col.iterator();
    }
    
    public int size()
    {
      return this.col.size();
    }
  }
  
  private static class StreamColBatch<StreamEvent>
    extends Batch
  {
    private static final long serialVersionUID = -6646847362508352538L;
    final Collection<StreamEvent> col;
    
    StreamColBatch(Collection<StreamEvent> col)
    {
      this.col = col;
    }
    
    public java.util.Iterator<StreamEvent> iterator()
    {
      return this.col.iterator();
    }
    
    public int size()
    {
      return this.col.size();
    }
  }
  
  public static Batch asBatch(Collection<WAEvent> col)
  {
    return new ColBatch(col);
  }
  
  public static Batch asStreamBatch(Collection<StreamEvent> col)
  {
    return new StreamColBatch(col);
  }
  
  public static Batch asBatch(final WAEvent e)
  {
    return new Batch()
    {
      private static final long serialVersionUID = 7274470955311509658L;
      
      public java.util.Iterator<WAEvent> iterator()
      {
        return new java.util.Iterator()
        {
          private boolean hasNext = true;
          
          public boolean hasNext()
          {
            return this.hasNext;
          }
          
          public WAEvent next()
          {
            if (this.hasNext)
            {
              this.hasNext = false;
              return e;
            }
            throw new NoSuchElementException();
          }
          
          public void remove()
          {
            throw new UnsupportedOperationException();
          }
        };
      }
      
      public int size()
      {
        return 1;
      }
    };
  }
  
  public static Batch batchesAsBatch(final Collection<IBatch> col)
  {
    return new Batch()
    {
      private static final long serialVersionUID = -1796558864997411339L;
      
      public java.util.Iterator<WAEvent> iterator()
      {
        return new java.util.Iterator()
        {
          private java.util.Iterator<IBatch> sit = col.iterator();
          private java.util.Iterator<WAEvent> it = Collections.emptyIterator();
          private WAEvent _next = null;
          
          private WAEvent getNext()
          {
            if (this._next == null)
            {
              while (!this.it.hasNext())
              {
                if (!this.sit.hasNext()) {
                  break ;
                }
                this.it = ((IBatch)this.sit.next()).iterator();
              }
              this._next = ((WAEvent)this.it.next());
            }
            
            return this._next;
          }
          
          public boolean hasNext()
          {
            return getNext() != null;
          }
          
          public WAEvent next()
          {
            WAEvent ret = getNext();
            this._next = null;
            return ret;
          }
          
          public void remove()
          {
            throw new UnsupportedOperationException();
          }
        };
      }
      
      public int size()
      {
        int size = 0;
        for (IBatch b : col) {
          size += b.size();
        }
        return size;
      }
      
      public boolean isEmpty()
      {
        if (!col.isEmpty()) {
          for (IBatch b : col) {
            if (!b.isEmpty()) {
              return false;
            }
          }
        }
        return true;
      }
    };
  }
  
  public static Batch asBatch(final Queue<WAEvent> buf)
  {
    return new Batch()
    {
      private static final long serialVersionUID = 4716758572600013367L;
      
      public java.util.Iterator<WAEvent> iterator()
      {
        return new java.util.Iterator()
        {
          private scala.collection.Iterator<WAEvent> it = buf.iterator();
          
          public boolean hasNext()
          {
            return this.it.hasNext();
          }
          
          public WAEvent next()
          {
            return (WAEvent)this.it.next();
          }
          
          public void remove()
          {
            throw new UnsupportedOperationException();
          }
        };
      }
      
      public int size()
      {
        return buf.size();
      }
    };
  }
  
  public static Map<RecordKey, IBatch<WAEvent>> partition(KeyFactory keyFactory, IBatch<WAEvent> batch)
  {
    if (batch.size() == 1)
    {
      WAEvent rec = (WAEvent)batch.first();
      RecordKey key = keyFactory.makeKey(rec.data);
      return Collections.singletonMap(key, batch);
    }
    Map<RecordKey, IBatch<WAEvent>> map = new LinkedHashMap();
    for (WAEvent rec : batch)
    {
      RecordKey key = keyFactory.makeKey(rec.data);
      IBatch values = (IBatch)map.get(key);
      if (values == null)
      {
        values = new ColBatch(new ArrayList());
        map.put(key, values);
      }
      ((ColBatch)values).col.add(rec);
    }
    return map;
  }
}
