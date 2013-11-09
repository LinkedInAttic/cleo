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

package cleo.search.bootstrap;

import java.io.File;

import krati.util.Chronos;
import cleo.search.store.ArrayStoreConnections;
import cleo.search.store.StoreFactory;
import cleo.search.util.ConnectionsScanner;

/**
 * NetworkTypeaheadConnectionsStoreBootstrap - Network Typeahead Connections Store Bootstrap.
 * 
 * @author jwu
 * @since 02/22, 2011
 */
public class NetworkTypeaheadConnectionsStoreBootstrap extends ArrayStoreConnectionsBootstrap {

  public NetworkTypeaheadConnectionsStoreBootstrap(ArrayStoreConnections connectionsStore,
                                                   int connSourceIdStart, int connSourceIdCount,
                                                   int connTargetIdStart, int connTargetIdCount) {
    super(connectionsStore,
          connSourceIdStart, connSourceIdCount,
          connTargetIdStart, connTargetIdCount);
  }
  
  @Override
  public boolean acceptConnection(int source, int target) {
    if((targetIdStart <= target && target < targetIdEnd) ||
       (targetIdStart <= source && source < targetIdEnd) ){
      return true;
    }
    return false;
  }
  
  /**
   *  <pre>
   * java NetworkTypeaheadConnectionsStoreBootstrap -server -Xms4G -Xmx16G \
   *      connectionsStorePath connectionsStoreSegmentFileSizeMB \
   *      connectionsDir
   *      connectionSourceIdStart connectionSourceIdCount
   *      connectionTargetIdStart connectionTargetIdCount
   *      
   * java NetworkTypeaheadConnectionsStoreBootstrap -server -Xms4G -Xmx16G \
   *      bootstrap/i001/member/typeahead/connections-store 32 \
   *      bootstrap/i001/member/connections 0 200000000 0 5000000
   * </pre>
   * 
   * @param args
   * @throws Exception
   */
  public static void main(String args[]) throws Exception {
    File connectionsStoreDir = new File(args[0]);
    int connectionsStoreSegMB = Integer.parseInt(args[1]);    
    
    File connectionsDir = new File(args[2]);
    int connSourceIdStart = Integer.parseInt(args[3]);
    int connSourceIdCount = Integer.parseInt(args[4]);
    int connTargetIdStart = Integer.parseInt(args[5]);
    int connTargetIdCount = Integer.parseInt(args[6]);
    
    Chronos c = new Chronos();
    
    // Create connectionsStore
    int capacity = connSourceIdCount;
    ArrayStoreConnections connectionsStore =
      StoreFactory.createArrayStoreConnections(connectionsStoreDir, capacity, connectionsStoreSegMB);
    
    // Bootstrap connectionsStore
    ArrayStoreConnectionsBootstrap bootstrap =
      new ArrayStoreConnectionsBootstrap(connectionsStore,
                                         connSourceIdStart,
                                         connSourceIdCount,
                                         connTargetIdStart,
                                         connTargetIdCount);
    
    ConnectionsScanner scanner = new ConnectionsScanner(connectionsDir);
    scanner.scan(bootstrap);
    connectionsStore.sync();
    
    System.out.printf("Bootstrap done in %d ms%n", c.tick());
  }
}
