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

import java.util.Iterator;

/**
 * KratiDataStoreConnections
 * 
 * @author jwu
 * @since 02/04, 2011
 */
public final class KratiDataStoreConnections extends KratiDataStoreInts implements ConnectionsStore<String> {
  
  public KratiDataStoreConnections(KratiDataStore store) {
    super(store);
  }
  
  @Override
  public int[] getConnections(String source) {
    return get(source);
  }
  
  @Override
  public void putConnections(String source, int[] connections, long scn) throws Exception {
    put(source, connections, scn);
  }
  
  @Override
  public void deleteConnections(String source, long scn) throws Exception {
    delete(source, scn);
  }
  
  @Override
  public void addConnection(String source, int connection, long scn) throws Exception {
    add(source, connection, scn);
  }
  
  @Override
  public void removeConnection(String source, int connection, long scn) throws Exception {
    remove(source, connection, scn);
  }

  @Override
  public Iterator<String> sourceIterator() {
    return keyIterator();
  }
}
