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

import cleo.search.ElementSerializer;
import cleo.search.TypeaheadElement;
import cleo.search.TypeaheadElementSerializer;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.ConnectionsStore;
import cleo.search.store.MemoryArrayStoreElement;
import cleo.search.store.StoreFactory;
import cleo.search.util.ElementScoreSetter;
import cleo.search.util.ElementScoreScanner;

import krati.core.segment.MemorySegmentFactory;
import krati.core.segment.SegmentFactory;
import krati.util.Chronos;

/**
 * GenericTypeaheadConnectionsStoreBootstrap
 * 
 * @author jwu
 * @since 02/07, 2011
 */
public class GenericTypeaheadConnectionsStoreBootstrap {
  
  /**
   * <pre>
   * java GenericTypeaheadConnectionsStoreBootstrap -server -Xms2G -Xmx8G \
   *      elementStorePath idStart idCount elementStoreSegmentMB \
   *      connectionsStorePath connectionsStoreCapacity connectionsStoreSegmentMB \
   *      elementScoreDir
   *      
   * java GenericTypeaheadConnectionsStoreBootstrap -server -Xms2G -Xmx8G \
   *      bootstrap/i001/question/typeahead/element-store 0 5000000 32 \
   *      bootstrap/i001/question/typeahead/connections-store 1000000 64 \
   *      bootstrap/i001/question/score
   * </pre>
   * 
   * @param args
   * @throws Exception
   */
  public static void main(String args[]) throws Exception {
    File elementStoreDir = new File(args[0]);
    int idStart = Integer.parseInt(args[1]);
    int idCount = Integer.parseInt(args[2]);
    int elementStoreSegMB = Integer.parseInt(args[3]);
    File connectionsStoreDir = new File(args[4]);
    int connectionsStoreCapacity = Integer.parseInt(args[5]);
    int connectionsStoreSegmentMB = Integer.parseInt(args[6]);
    File elementScoreDir = (args.length > 7) ? new File(args[7]) : null;
    
    Chronos c = new Chronos();
    
    // Load elementStore
    ElementSerializer<TypeaheadElement> elementSerializer = new TypeaheadElementSerializer();
    ArrayStoreElement<TypeaheadElement> elementStore =
      StoreFactory.createElementStorePartition(elementStoreDir, idStart, idCount, elementStoreSegMB, elementSerializer);
    
    // Load elementStore into memory
    elementStore = new MemoryArrayStoreElement<TypeaheadElement>(elementStore);
    
    // Load element scores
    if(elementStoreDir != null && elementScoreDir.exists()) {
      ElementScoreSetter<TypeaheadElement> handler = new ElementScoreSetter<TypeaheadElement>(elementStore);
      ElementScoreScanner scan = new ElementScoreScanner(elementScoreDir);
      scan.scan(handler);
    }
    
    // Create connectionsStore
    int indexSegmentFileSizeMB = 8;
    SegmentFactory indexSegmentFactory = new MemorySegmentFactory();
    SegmentFactory storeSegmentFactory = new MemorySegmentFactory();
    ConnectionsStore<String> connectionsStore =
      StoreFactory.createConnectionsStore(
          connectionsStoreDir, connectionsStoreCapacity,
          indexSegmentFileSizeMB, indexSegmentFactory,
          connectionsStoreSegmentMB, storeSegmentFactory);
    
    ConnectionsCollector connectionsCollector = new ConnectionsCollector(connectionsStoreCapacity);
    connectionsCollector.collect(elementStore, 5 /* maxKeyLength */);
    connectionsCollector.store(connectionsStore);
    
    System.out.printf("Bootstrap done in %d ms%n", c.tick());
  }
}
