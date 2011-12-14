package cleo.search.store;

import krati.array.Array;

/**
 * DoubleArrayPartition
 * 
 * @author jwu
 * @since 03/25, 2011
 */
public interface DoubleArrayPartition extends Array {

  public int capacity();
  
  public int getIndexStart();
  
  public int getIndexEnd();
  
  public double get(int index);
  
  public void set(int index, double value);
  
  public double[] getInternalArray();
}
