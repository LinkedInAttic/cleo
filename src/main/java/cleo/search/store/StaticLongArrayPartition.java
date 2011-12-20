/*
 * Copyright (c) 2011 LinkedIn, Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package cleo.search.store;

import java.util.Arrays;

import krati.array.Array;

/**
 * StaticLongArrayPartition
 * 
 * @author jwu
 * @since 02/03, 2011
 */
public final class StaticLongArrayPartition implements LongArrayPartition {
  private final int indexStart;
  private final int indexEnd;
  private final int capacity;
  private final long[] array;
  
  public StaticLongArrayPartition(int indexStart, int capacity) {
    this.indexStart = indexStart;
    this.indexEnd = indexStart + capacity;
    this.capacity = capacity;
    this.array = new long[capacity];
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
  public long[] getInternalArray() {
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
  public long get(int index) {
    return array[index - indexStart];
  }
  
  @Override
  public void set(int index, long value) {
    array[index - indexStart] = value;
  }
  
  @Override
  public Array.Type getType() {
    return Array.Type.STATIC;
  }
}
