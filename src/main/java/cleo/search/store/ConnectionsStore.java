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

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

import krati.Persistable;

/**
 * ConnectionsStore
 * 
 * @author jwu
 * @since 02/05, 2011
 */
public interface ConnectionsStore<S> extends Persistable, Closeable {

  /**
   * Get the connections
   */
  public int[] getConnections(S source);
  
  /**
   * Set the connections.
   * 
   * @param source      - source
   * @param connections - source connections
   * @param scn         - system change number
   */
  public void putConnections(S source, int[] connections, long scn) throws Exception;
  
  /**
   * Delete the connections for a source.
   * 
   * @param scn         - system change number
   */
  public void deleteConnections(S source, long scn) throws Exception;
  
  /**
   * Add a connection to a source.
   * 
   * @param source      - source
   * @param connection  - connection to add
   * @param scn         - system change number
   * @throws Exception
   */
  public void addConnection(S source, int connection, long scn) throws Exception;
  
  /**
   * Remove a connection from a source.
   * 
   * @param source      - source
   * @param connection  - connection to remove
   * @param scn         - system change number
   * @throws Exception
   */
  public void removeConnection(S source, int connection, long scn) throws Exception;
  
  /**
   * @return the connection source iterator.
   */
  public Iterator<S> sourceIterator();

  /**
   * closes the store
   * @throws IOException
   */
  public void close() throws IOException;
}
