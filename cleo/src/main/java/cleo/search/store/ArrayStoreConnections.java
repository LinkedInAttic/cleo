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

import krati.Persistable;
import krati.array.Array;

/**
 * ArrayStoreConnections
 * 
 * @author jwu
 * @since 01/22, 2011
 * 
 * <p>
 * 09/18, 2011 - Added readBytes() to support partial reads <br/>
 */
public interface ArrayStoreConnections extends Persistable, Array {
  
  /**
   * Get the connections
   */
  public int[] getConnections(int source);
  
  /**
   * Set the connections.
   * 
   * @param source      - source
   * @param connections - source connections
   * @param scn         - system change number
   */
  public void setConnections(int source, int[] connections, long scn) throws Exception;
  
  /**
   * Delete the connections for a source.
   * 
   * @param scn         - system change number
   */
  public void deleteConnections(int source, long scn) throws Exception;
  
  /**
   * Add a connection to a source.
   * 
   * @param source      - source
   * @param connection  - connection to add
   * @param scn         - system change number
   * @throws Exception
   */
  public void addConnection(int source, int connection, long scn) throws Exception;
  
  /**
   * Remove a connection from a source.
   * 
   * @param source      - source
   * @param connection  - connection to remove
   * @param scn         - system change number
   * @throws Exception
   */
  public void removeConnection(int source, int connection, long scn) throws Exception;
  
  /**
   * Gets the length of data bytes at an index.
   * 
   * @param index - the array index
   * @return the number of bytes at an index.
   */
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
