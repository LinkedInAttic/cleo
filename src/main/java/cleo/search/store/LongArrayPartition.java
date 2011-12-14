package cleo.search.store;

import krati.array.Array;

/**
 * LongArrayPartition
 * 
 * @author jwu
 * @since 02/03, 2011
 */
public interface LongArrayPartition extends Array {

  public int capacity();
  
  public int getIndexStart();
  
  public int getIndexEnd();
  
  public long get(int index);
  
  public void set(int index, long value);
  
  public long[] getInternalArray();
}
