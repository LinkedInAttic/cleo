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

import java.util.List;

import krati.Persistable;
import krati.array.Array;

import cleo.search.util.Weight;

/**
 * ArrayStoreWeights
 * 
 * @author jwu
 * @since 04/18, 2011
 * 
 * <p>
 * 09/18, 2011 - Added readBytes() to support partial reads <br/>
 */
public interface ArrayStoreWeights extends Persistable, Array {
  
  public static final int ELEMID_SUBARRAY_INDEX = ArrayStoreFilters.ELEMID_SUBARRAY_INDEX;
  public static final int WEIGHT_SUBARRAY_INDEX = ArrayStoreFilters.FILTER_SUBARRAY_INDEX;
  
  public int getWeight(int index, int elemId);
  
  public void setWeight(int index, int elemId, int elemWeight, long scn) throws Exception;
  
  public int[][] getWeightData(int index);
  
  public void setWeightData(int index, int[][] weightData, long scn) throws Exception;
  
  public void setWeightData(int index, List<Weight> weightData, long scn) throws Exception;
  
  public void remove(int index, int elemId, long scn) throws Exception;
  
  public void delete(int index, long scn) throws Exception;
  
  public int getLength(int index);
  
  /**
   * Gets data bytes at an index.
   * 
   * @param index - the array index
   * @return an array of bytes if data is available at the given index.
   *         Otherwise, <code>null</code>.
   */
  public byte[] getBytes(int index);
  
  /**
   * Gets data bytes at a given index.
   * 
   * @param index - the array index
   * @param dst   - the byte array to fill in
   * @return the length of data at the given index.
   * @throws ArrayIndexOutOfBoundsException if the index is out of range
   * or if the byte array does not have enough space to hold the read data.
   */
  public int getBytes(int index, byte[] dst);
  
  /**
   * Gets data bytes at a given index.
   * 
   * @param index  - the array index
   * @param dst    - the byte array to fill in
   * @param offset - the offset of the byte array where data is filled in 
   * @return the length of data at the given index.
   * @throws ArrayIndexOutOfBoundsException if the index is out of range
   * or if the byte array does not have enough space to hold the read data.
   */
  public int getBytes(int index, byte[] dst, int offset);
  
  /**
   * Reads data bytes at an index into a byte array.
   * 
   * This method does a full read of data bytes only if the destination byte
   * array has enough capacity to store all the bytes from the specified index.
   * Otherwise, a partial read is done to fill in the destination byte array.
   *   
   * @param index - the array index
   * @param dst   - the byte array to fill in
   * @return the total number of bytes read if data is available at the given index.
   *         Otherwise, <code>-1</code>.
   */
  public int readBytes(int index, byte[] dst);
  
  /**
   * Reads data bytes from an offset of data at an index to fill in a byte array.
   *   
   * @param index  - the array index
   * @param offset - the offset of data bytes
   * @param dst    - the byte array to fill in
   * @return the total number of bytes read if data is available at the given index.
   *         Otherwise, <code>-1</code>.
   */
  public int readBytes(int index, int offset, byte[] dst);
  
}
