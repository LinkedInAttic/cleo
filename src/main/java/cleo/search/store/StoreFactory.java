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

import java.io.File;

import cleo.search.Element;
import cleo.search.ElementSerializer;

import krati.core.segment.MemorySegmentFactory;
import krati.core.segment.SegmentFactory;
import krati.store.ArrayStorePartition;
import krati.store.StaticArrayStorePartition;

/**
 * StoreFactory
 * 
 * @author jwu
 * @since 01/20, 2011
 */
public class StoreFactory {
  
  public static KratiArrayStore createKratiArrayStore(File storeHomeDir,
                                                      int initialCapacity,
                                                      SegmentFactory segmentFactory,
                                                      int segmentFileSizeMB) throws Exception {
    int batchSize = 5000;
    int numSyncBatches = 20;
    double segmentCompactFactor = 0.67;
    
    return new KratiArrayStore(initialCapacity,
                               batchSize,
                               numSyncBatches,
                               storeHomeDir,
                               segmentFactory,
                               segmentFileSizeMB,
                               segmentCompactFactor);
  }
  
  public static KratiDataStore createKratiDataStore(File storeHomeDir,
                                                    int initialCapacity,
                                                    int indexSegmentFileSizeMB,
                                                    SegmentFactory indexSegmentFactory,
                                                    int storeSegmentFileSizeMB,
                                                    SegmentFactory storeSegmentFactory) throws Exception {
    int batchSize = 5000;
    int numSyncBatches = 20;
    return new KratiDataStore(storeHomeDir,
                              initialCapacity, batchSize, numSyncBatches,
                              indexSegmentFileSizeMB, indexSegmentFactory,
                              storeSegmentFileSizeMB, storeSegmentFactory);
  }
  
  public static KratiDataStore createKratiDataStore(File storeHomeDir,
                                                    int initialCapacity,
                                                    int batchSize,
                                                    int numSyncBatches,
                                                    int indexSegmentFileSizeMB,
                                                    SegmentFactory indexSegmentFactory,
                                                    int storeSegmentFileSizeMB,
                                                    SegmentFactory storeSegmentFactory) throws Exception {
    return new KratiDataStore(storeHomeDir,
                              initialCapacity, batchSize, numSyncBatches,
                              indexSegmentFileSizeMB, indexSegmentFactory,
                              storeSegmentFileSizeMB, storeSegmentFactory);
  }
  
  /**
   * Creates a <code>ArrayStoreConnections</code> using <code>MemorySegmentFactory</code>.
   * 
   * @param storeHomeDir      - Store home directory
   * @param capacity          - Initial capacity
   * @param segmentFileSizeMB - Store segment size in MB
   * @return the created <code>ArrayStoreConnections</code>.
   * @throws Exception if the <code>ArrayStoreConnections</code> cannot be created.
   */
  public static ArrayStoreConnections createArrayStoreConnections(File storeHomeDir, int capacity, int segmentFileSizeMB) throws Exception {
    KratiArrayStore kas = createKratiArrayStore(storeHomeDir, capacity, new MemorySegmentFactory(), segmentFileSizeMB);
    return new KratiArrayStoreConnections(new KratiArrayStoreInts(kas));
  }
  
  /**
   * Creates a <code>ArrayStoreConnections</code> using a specified segment factory.
   * 
   * @param storeHomeDir      - Store home directory
   * @param capacity          - Initial capacity
   * @param segmentFactory    - Store Segment factory
   * @param segmentFileSizeMB - Store segment size in MB
   * @return the created <code>ArrayStoreConnections</code>.
   * @throws Exception if the <code>ArrayStoreConnections</code> cannot be created.
   */
  public static ArrayStoreConnections createArrayStoreConnections(File storeHomeDir, int capacity, SegmentFactory segmentFactory, int segmentFileSizeMB) throws Exception {
    KratiArrayStore kas = createKratiArrayStore(storeHomeDir, capacity, segmentFactory, segmentFileSizeMB);
    return new KratiArrayStoreConnections(new KratiArrayStoreInts(kas));
  }
  
  public static ArrayStoreFilters createArrayStoreFilters(File storeHomeDir, int capacity, SegmentFactory segmentFactory, int segmentFileSizeMB) throws Exception {
    return new KratiArrayStoreFilters(createKratiArrayStore(storeHomeDir, capacity, segmentFactory, segmentFileSizeMB));
  }
  
  public static ArrayStoreWeights createArrayStoreWeights(File storeHomeDir, int capacity, SegmentFactory segmentFactory, int segmentFileSizeMB) throws Exception {
    return new KratiArrayStoreWeights(createKratiArrayStore(storeHomeDir, capacity, segmentFactory, segmentFileSizeMB));
  }
  
  public static <E extends Element> ArrayStoreElement<E> createElementStorePartition(File storeHomeDir, int idStart, int idCount, SegmentFactory segmentFactory, int segmentFileSizeMB, ElementSerializer<E> serializer)
  throws Exception {
    int batchSize = 5000;
    int numSyncBatches = 20;
    ArrayStorePartition p = new StaticArrayStorePartition(idStart, idCount, batchSize, numSyncBatches, storeHomeDir, segmentFactory, segmentFileSizeMB, false);
    return new KratiArrayStoreElement<E>(p, serializer);
  }
  
  public static ConnectionsStore<String> createConnectionsStore(
      File storeHomeDir,
      int initialCapacity,
      int indexSegmentFileSizeMB, SegmentFactory indexSegmentFactory,
      int storeSegmentFileSizeMB, SegmentFactory storeSegmentFactory) throws Exception {
    int batchSize = 5000;
    int numSyncBatches = 20;
    KratiDataStore kds = new KratiDataStore(storeHomeDir,
                                            initialCapacity, batchSize, numSyncBatches,
                                            indexSegmentFileSizeMB, indexSegmentFactory,
                                            storeSegmentFileSizeMB, storeSegmentFactory);
    return new KratiDataStoreConnections(new KratiBufferedInts(kds));
  }
  
  public static ConnectionsStore<String> createConnectionsStore(
      File storeHomeDir,
      int initialCapacity,
      int batchSize, int numSyncBatches,
      int indexSegmentFileSizeMB, SegmentFactory indexSegmentFactory,
      int storeSegmentFileSizeMB, SegmentFactory storeSegmentFactory) throws Exception {
    KratiDataStore kds = new KratiDataStore(storeHomeDir,
                                            initialCapacity, batchSize, numSyncBatches,
                                            indexSegmentFileSizeMB, indexSegmentFactory,
                                            storeSegmentFileSizeMB, storeSegmentFactory);
    return new KratiDataStoreConnections(new KratiBufferedInts(kds));
  }
}
