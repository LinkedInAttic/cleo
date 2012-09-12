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

package cleo.search.tool;

import cleo.search.Element;
import cleo.search.Indexer;
import cleo.search.filter.BloomFilter;
import cleo.search.filter.FnvBloomFilterLong;
import cleo.search.selector.PrefixSelectorFactory;
import cleo.search.selector.SelectorFactory;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.ConnectionsStore;
import cleo.search.store.MemoryArrayStoreElement;
import cleo.search.store.MemoryConnectionsStore;
import cleo.search.store.StoreFactory;
import cleo.search.typeahead.GenericTypeahead;
import cleo.search.typeahead.GenericTypeaheadConfig;
import cleo.search.typeahead.Typeahead;
import cleo.search.util.ElementScoreScanner;
import cleo.search.util.ScoreScanner;

/**
 * GenericTypeaheadInitializer
 * 
 * @author jwu
 * @since 02/09, 2011
 */
public class GenericTypeaheadInitializer<E extends Element> implements TypeaheadInitializer<E>, IndexerInitializer<E> {
  private final GenericTypeahead<E> genericTypeahead;

  public GenericTypeaheadInitializer(Config<E> config) throws Exception {
    this.genericTypeahead = createTypeahead(config);
  }
  
  public GenericTypeaheadInitializer(GenericTypeaheadConfig<E> config) throws Exception {
    this.genericTypeahead = createTypeahead(config);
  }
  
  protected GenericTypeahead<E> createTypeahead(GenericTypeaheadConfig<E> config) throws Exception {
    // create elementStore
    ArrayStoreElement<E> elementStore =
      StoreFactory.createElementStorePartition(
          config.getElementStoreDir(),
          config.getElementStoreIndexStart(),
          config.getElementStoreCapacity(),
          config.getElementStoreSegmentMB(),
          config.getElementSerializer());
    
    // load elementStore in memory
    if(config.isElementStoreCached()) {
      elementStore = new MemoryArrayStoreElement<E>(elementStore);
    }
    
    // create connectionsStore
    ConnectionsStore<String> connectionsStore =
      StoreFactory.createConnectionsStore(
          config.getConnectionsStoreDir(),
          config.getConnectionsStoreCapacity(),
          config.getConnectionsStoreIndexSegmentMB(),
          config.getConnectionsStoreIndexSegmentFactory(),
          config.getConnectionsStoreSegmentMB(),
          config.getConnectionsStoreSegmentFactory());
    
    // load connectionsStore in memory
    if(config.isConnectionsStoreCached()) {
      connectionsStore = new MemoryConnectionsStore<String>(
          config.getConnectionsStoreCapacity(),
          connectionsStore);
    }
    
    // create selectorFactory
    SelectorFactory<E> selectorFactory = config.getSelectorFactory();
    if(selectorFactory == null) selectorFactory = new PrefixSelectorFactory<E>();
    
    // create bloomFilter
    BloomFilter<Long> bloomFilter = new FnvBloomFilterLong(config.getFilterPrefixLength());
    
    // create scoreScanner
    ScoreScanner scoreScanner = new ElementScoreScanner(config.getElementScoreFile());
    
    // Create GenericTypeahead
    return new GenericTypeahead<E>(
          config.getName(),
          elementStore,
          connectionsStore,
          selectorFactory,
          bloomFilter,
          scoreScanner,
          config.getMaxKeyLength());
  }
  
  @Override
  public Typeahead<E> getTypeahead() {
    return genericTypeahead;
  }

  @Override
  public Indexer<E> getIndexer() {
    return genericTypeahead;
  }
  
  public static class Config<E extends Element> extends GenericTypeaheadConfig<E> {}
}
