package cleo.search.store;

import java.util.Arrays;

import krati.array.Array;

/**
 * StaticFloatArrayPartition
 * 
 * @author jwu
 * @since 03/25, 2011
 */
public class StaticFloatArrayPartition implements FloatArrayPartition {
  private final int indexStart;
  private final int indexEnd;
  private final int capacity;
  private final float[] array;
  
  public StaticFloatArrayPartition(int indexStart, int capacity) {
    this.indexStart = indexStart;
    this.indexEnd = indexStart + capacity;
    this.capacity = capacity;
    this.array = new float[capacity];
  }
  
  @Override
  public int getIndexStart() {
    return indexStart;
  }
  
  @Override
  public int getIndexEnd() {
    return indexEnd;
  }
  
  @Override
  public int capacity() {
    return capacity;
  }
  
  @Override
  public int length() {
    return capacity;
  }
  
  @Override
  public float[] getInternalArray() {
    return array;
  }
  
  @Override
  public void clear() {
    Arrays.fill(array, 0);
  }
  
  @Override
  public boolean hasIndex(int index) {
    return (indexStart <= index && index < indexEnd);
  }
  
  @Override
  public float get(int index) {
    return array[index - indexStart];
  }
  
  @Override
  public void set(int index, float value) {
    array[index - indexStart] = value;
  }
  
  @Override
  public Array.Type getType() {
    return Array.Type.STATIC;
  }
}
