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

import cleo.search.Element;
import cleo.search.ElementSerializer;
import cleo.search.filter.BloomFilter;
import cleo.search.filter.FnvBloomFilterLong;
import cleo.search.selector.PrefixSelectorFactory;
import cleo.search.selector.SelectorFactory;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.ConnectionsStore;
import cleo.search.store.StoreFactory;
import cleo.search.test.util.FileUtils;
import cleo.search.typeahead.GenericTypeahead;
import cleo.search.util.ElementScoreScanner;
import cleo.search.util.ScoreScanner;

import junit.framework.TestCase;

import krati.core.segment.MemorySegmentFactory;
import krati.core.segment.SegmentFactory;

/**
 * AbstractTestGenericTypeahead
 * 
 * @author jwu
 * @since 02/04, 2011
 */
public abstract class AbstractTestGenericTypeahead<E extends Element> extends TestCase {
  protected Random rand = new Random();
  protected GenericTypeahead<E> typeahead;
  protected File homeDir;
  
  protected File getHomeDir() {
    if(homeDir == null) {
      homeDir = FileUtils.getTestDir(getClass().getSimpleName());
    }
    return homeDir;
  }
  
  protected int getElementStoreIndexStart() {
    return 0;
  }
  
  protected int getElementStoreCapacity() {
    return 5000;
  }
  
  protected int getFilterPrefixLength() {
    return 3;
  }
  
  protected int getMaxKeyLength() {
    return 5;
  }
  
  protected BloomFilter<Long> createBloomFilter() {
    return new FnvBloomFilterLong(getFilterPrefixLength());
  }
  
  protected ScoreScanner createScoreScanner() {
    File elementScoreFile = new File(getHomeDir(), "element-score.txt");
    return new ElementScoreScanner(elementScoreFile);
  }
  
  protected SelectorFactory<E> createSelectorFactory() {
    return new PrefixSelectorFactory<E>();
  }
  
  protected abstract ElementSerializer<E> createElementSerializer();
  
  protected ArrayStoreElement<E> createElementStore() throws Exception {
    File elementStoreDir = new File(getHomeDir(), "element-store");
    int elementStoreSegMB = 32;
    
    ArrayStoreElement<E> elementStore =
      StoreFactory.createElementStorePartition(
          elementStoreDir,
          getElementStoreIndexStart(),
          getElementStoreCapacity(),
          new MemorySegmentFactory(),
          elementStoreSegMB,
          createElementSerializer());
    return elementStore;
  }
  
  protected ConnectionsStore<String> createConnectionsStore() throws Exception {
    File connectionsStoreDir = new File(getHomeDir(), "connections-store");
        
    int initialCapacity = 500000;
    int indexSegmentFileSizeMB = 8;
    SegmentFactory indexSegmentFactory = new MemorySegmentFactory();
    int storeSegmentFileSizeMB = 32;
    SegmentFactory storeSegmentFactory = new MemorySegmentFactory();
    
    ConnectionsStore<String> connectionsStore =
      StoreFactory.createConnectionsStore(
          connectionsStoreDir,
          initialCapacity,
          indexSegmentFileSizeMB, indexSegmentFactory,
          storeSegmentFileSizeMB, storeSegmentFactory);
   
    return connectionsStore;
  }
  
  protected GenericTypeahead<E> createTypeahead() throws Exception {
    return new GenericTypeahead<E>("Typeahead",
                                   createElementStore(),
                                   createConnectionsStore(),
                                   createSelectorFactory(),
                                   createBloomFilter(),
                                   createScoreScanner(),
                                   getMaxKeyLength());
  }
  
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
