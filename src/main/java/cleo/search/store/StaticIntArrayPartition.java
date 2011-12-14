package cleo.search.store;

import java.util.Arrays;

import krati.array.Array;

/**
 * StaticIntArrayPartition
 * 
 * @author jwu
 * @since 02/03, 2011
 */
public final class StaticIntArrayPartition implements IntArrayPartition {
  private final int indexStart;
  private final int indexEnd;
  private final int capacity;
  private final int[] array;
  
  public StaticIntArrayPartition(int indexStart, int capacity) {
    this.indexStart = indexStart;
    this.indexEnd = indexStart + capacity;
    this.capacity = capacity;
    this.array = new int[capacity];
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
  public int[] getInternalArray() {
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
  public int get(int index) {
    return array[index - indexStart];
  }
  
  @Override
  public void set(int index, int value) {
    array[index - indexStart] = value;
  }
  
  @Override
  public Array.Type getType() {
    return Array.Type.STATIC;
  }
}
