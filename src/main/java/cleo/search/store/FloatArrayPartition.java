package cleo.search.store;

import krati.array.Array;

/**
 * FloatArrayPartition
 * 
 * @author jwu
 * @since 03/25, 2011
 */
public interface FloatArrayPartition extends Array {

  public int capacity();
  
  public int getIndexStart();
  
  public int getIndexEnd();
  
  public float get(int index);
  
  public void set(int index, float value);
  
  public float[] getInternalArray();
}
