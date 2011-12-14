package cleo.search.store;

import krati.array.Array;

/**
 * IntArrayPartition
 * 
 * @author jwu
 * @since 02/03, 2011
 */
public interface IntArrayPartition extends Array {

  public int capacity();
  
  public int getIndexStart();
  
  public int getIndexEnd();
  
  public int get(int index);
  
  public void set(int index, int value);
  
  public int[] getInternalArray();
}
