package cleo.search.store;

import java.io.File;

import krati.Persistable;
import krati.array.Array;

/**
 * ArrayStoreFilters
 * 
 * @author jwu
 * @since 01/11, 2011
 * 
 * <p>
 * 09/18, 2011 - Added readBytes() to support partial reads <br/>
 */
public interface ArrayStoreFilters extends Persistable, Array {
  public static final int ELEMID_SUBARRAY_INDEX = 0;
  public static final int FILTER_SUBARRAY_INDEX = 1;
  
  public String getStatus();
  
  public File getStoreHome();
  
  public int getCount(int index);
  
  public int getLength(int index);
  
  public int[][] get(int index);
  
  public void set(int index, int[][] elemFilterData, long scn) throws Exception;
  
  public void add(int index, int elemId, int elemFilter, long scn) throws Exception;
  
  public void remove(int index, int elemId, long scn) throws Exception;
  
  public void delete(int index, long scn) throws Exception;
  
  public int[][] getFilterData(int index);
  
  public void setFilterData(int index, int[][] filterData, long scn) throws Exception;
  
  public int getFilter(int index, int elemId);
  
  public void setFilter(int index, int elemId, int elemFilter, long scn) throws Exception;
  
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
