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

package cleo.search.test.typeahead;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import junit.framework.TestCase;
import krati.core.segment.MemorySegmentFactory;
import krati.core.segment.SegmentFactory;
import cleo.search.Element;
import cleo.search.ElementSerializer;
import cleo.search.connection.ConnectionFilter;
import cleo.search.connection.TransitivePartitionConnectionFilter;
import cleo.search.filter.BloomFilter;
import cleo.search.filter.FnvBloomFilter;
import cleo.search.selector.ScoredElementSelectorFactory;
import cleo.search.selector.SelectorFactory;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.ArrayStoreWeights;
import cleo.search.store.StoreFactory;
import cleo.search.test.util.FileUtils;
import cleo.search.typeahead.WeightedNetworkTypeahead;
import cleo.search.util.Range;

/**
 * AbstractTestWeightedNetworkTypeahead
 * 
 * @author jwu
 * @since 04/20, 2011
 */
public abstract class AbstractTestWeightedNetworkTypeahead<E extends Element> extends TestCase {
  protected Random rand = new Random();
  protected WeightedNetworkTypeahead<E> typeahead;
  protected File homeDir;
  
  protected File getHomeDir() {
    if(homeDir == null) {
      homeDir = FileUtils.getTestDir(getClass().getSimpleName());
    }
    return homeDir;
  }
  
  protected int getPartitionStart() {
    return 0;
  }
  
  protected int getPartitionCount() {
    return 5000;
  }
  
  protected int getElementStoreIndexStart() {
    return getPartitionStart();
  }
  
  protected int getElementStoreCapacity() {
    return getPartitionCount();
  }
  
  protected int getFilterPrefixLength() {
    return 3;
  }
  
  protected BloomFilter<Integer> createBloomFilter() {
    return new FnvBloomFilter(getFilterPrefixLength());
  }
  
  protected SelectorFactory<E> createSelectorFactory() {
    return new ScoredElementSelectorFactory<E>();
  }
  
  protected ConnectionFilter createConnectionFilter() {
    return new TransitivePartitionConnectionFilter(new Range(getPartitionStart(), getPartitionCount()));
  }
  
  protected ArrayStoreElement<E> createElementStore() throws Exception {
    File elementStoreDir = new File(getHomeDir(), "element-store");
    int elementStoreSegMB = 32;
    
    ArrayStoreElement<E> elementStore =
      StoreFactory.createElementStorePartition(
          elementStoreDir,
          getElementStoreIndexStart(),
          getElementStoreCapacity(),
          elementStoreSegMB,
          createElementSerializer());
    return elementStore;
  }
  
  protected int getConnectionsStoreIndexStart() {
    return getElementStoreIndexStart();
  }
  
  protected int getConnectionsStoreCapacity() {
    return getElementStoreCapacity();
  }
  
  protected ArrayStoreWeights createWeightedConnectionsStore() throws Exception {
    File connectionsStoreDir = new File(getHomeDir(), "weighted-connections-store");
    int connectionsStoreSegMB = 32;
    SegmentFactory connectionsStoreSegFactory = new MemorySegmentFactory();
    
    ArrayStoreWeights connectionsStore =
      StoreFactory.createArrayStoreWeights(
          connectionsStoreDir,
          getConnectionsStoreCapacity(),
          connectionsStoreSegFactory,
          connectionsStoreSegMB);
    
    return connectionsStore;
  }
  
  protected WeightedNetworkTypeahead<E> createTypeahead() throws Exception {
    return new WeightedNetworkTypeahead<E>(
        "Network",
        createElementStore(),
        createWeightedConnectionsStore(),
        createSelectorFactory(),
        createBloomFilter(),
        createConnectionFilter());
  }
  
  protected abstract ElementSerializer<E> createElementSerializer();
  
  @Override
  protected void setUp() {
    try {
      getHomeDir();
      typeahead = createTypeahead();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @Override
  protected void tearDown() {
    try {
      if(homeDir != null) {
        FileUtils.deleteDirectory(homeDir);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
