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

import java.io.IOException;
import java.util.Iterator;

/**
 * KratiDataStoreConnections
 * 
 * @author jwu
 * @since 02/04, 2011
 */
public final class KratiDataStoreConnections implements ConnectionsStore<String> {
  /**
   * The underlying store for list of integers.
   */
  private final DataStoreInts storeInts;
  
  /**
   * Creates a new instance KratiDataStoreConnections.
   * 
   * @param storeInts - the underlying store for list of integers
   */
  public KratiDataStoreConnections(DataStoreInts storeInts) {
    this.storeInts = storeInts;
  }
  
  @Override
  public int[] getConnections(String source) {
    return storeInts.get(source);
  }
  
  @Override
  public void putConnections(String source, int[] connections, long scn) throws Exception {
    storeInts.put(source, connections, scn);
  }
  
  @Override
  public void deleteConnections(String source, long scn) throws Exception {
    storeInts.delete(source, scn);
  }
  
  @Override
  public void addConnection(String source, int connection, long scn) throws Exception {
    storeInts.add(source, connection, scn);
  }
  
  @Override
  public void removeConnection(String source, int connection, long scn) throws Exception {
    storeInts.remove(source, connection, scn);
  }
  
  @Override
  public Iterator<String> sourceIterator() {
    return storeInts.keyIterator();
  }
  
  @Override
  public void sync() throws IOException {
    storeInts.sync();
  }
  
  @Override
  public void persist() throws IOException {
    storeInts.persist();
  }
  
  @Override
  public long getLWMark() {
    return storeInts.getLWMark();
  }

  @Override
  public long getHWMark() {
    return storeInts.getHWMark();
  }
  
  @Override
  public void saveHWMark(long endOfPeriod) throws Exception {
    storeInts.saveHWMark(endOfPeriod);
  }

  public void close() throws IOException {
    storeInts.close();
  }
}
