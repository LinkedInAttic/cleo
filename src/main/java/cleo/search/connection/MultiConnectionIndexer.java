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

package cleo.search.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * MultiConnectionIndexer
 * 
 * @author jwu
 * @since 12/12, 2011
 */
public class MultiConnectionIndexer implements ConnectionIndexer {
  private String name = MultiConnectionIndexer.class.getSimpleName();
  private List<ConnectionIndexer> indexerList = new ArrayList<ConnectionIndexer>();
  
  /**
   * Create a new instance of {@link MultiConnectionIndexer}.
   * 
   * @param connIndexers - the list of connection indexers.
   */
  public MultiConnectionIndexer(List<ConnectionIndexer> connIndexers) {
    if(connIndexers != null) {
      for(ConnectionIndexer indexer : connIndexers) {
        if(indexer != null) {
          indexerList.add(indexer);
        }
      }
    }
  }
  
  /**
   * Create a new instance of {@link MultiConnectionIndexer}.
   * 
   * @param name - the name of this MultiConnectionIndexer.
   * @param connIndexers - the list of connection indexers.
   */
  public MultiConnectionIndexer(String name, List<ConnectionIndexer> connIndexers) {
    this(connIndexers);
    this.setName(name);
  }
  
  /**
   * Gets the name of this {@link MultiConnectionIndexer}.
   */
  public final String getName() {
    return name;
  }
  
  /**
   * Sets the name of this {@link MultiConnectionIndexer}.
   */
  public final void setName(String name) {
    this.name = name;
  }
  
  /**
   * Gets the list of subsidiary connection indexers.
   */
  public final List<ConnectionIndexer> subIndexers() {
    return indexerList;
  }
  
  /**
   * Flushes indexes.
   * 
   * @throws IOException if this operation failed.
   */
  @Override
  public void flush() throws IOException {
    for(ConnectionIndexer indexer : indexerList) {
      indexer.flush();
    }
  }
  
  /**
   * Indexes a connection.
   * 
   * @param  conn - a connection to be indexed.
   * @return <code>true</code> if the underlying indexes changed
   *         as a result of this operation. Otherwise, <code>false</code>.
   * @throws Exception if this operation failed.
   */
  @Override
  public boolean index(Connection conn) throws Exception {
    boolean b = false;
    
    for(ConnectionIndexer indexer : indexerList) {
      if(indexer.accept(conn)) {
        b = indexer.index(conn) || b;
      }
    }
    
    return b;
  }
  
  /**
   * Checks whether a connection is acceptable or not.
   * 
   * @param conn - connection to test
   * @return <code>true</code> if the connection is acceptable. Otherwise, <code>false</code>.
   */
  @Override
  public boolean accept(Connection conn) {
    for(ConnectionIndexer indexer : indexerList) {
      if(indexer.accept(conn)) {
        return true;
      }
    }
    
    return false;
  }
  
  /**
   * Checks whether a connection is acceptable or not.
   *  
   * @param source - connection source
   * @param target - connection target
   * @param active - whether the connection is active or not.
   * @return <code>true</code> if the connection is acceptable. Otherwise, <code>false</code>.
   */
  @Override
  public boolean accept(int source, int target, boolean active) {
    for(ConnectionIndexer indexer : indexerList) {
      if(indexer.accept(source, target, active)) {
        return true;
      }
    }
    
    return false;
  }
}
