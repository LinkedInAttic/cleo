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
import cleo.search.connection.ConnectionFilter;
import cleo.search.connection.TransitivePartitionConnectionFilter;
import cleo.search.filter.BloomFilter;
import cleo.search.filter.FnvBloomFilter;
import cleo.search.selector.PrefixSelectorFactory;
import cleo.search.selector.SelectorFactory;
import cleo.search.store.ArrayStoreConnections;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.MemoryArrayStoreElement;
import cleo.search.store.StoreFactory;
import cleo.search.typeahead.VanillaNetworkTypeahead;
import cleo.search.typeahead.NetworkTypeaheadConfig;
import cleo.search.typeahead.Typeahead;
import cleo.search.util.Range;
import krati.core.segment.WriteBufferSegmentFactory;

/**
 * VanillaNetworkTypeaheadInitializer
 * 
 * @author jwu
 * @since 02/08, 2011
 */
public class VanillaNetworkTypeaheadInitializer<E extends Element> implements TypeaheadInitializer<E>, IndexerInitializer<E> {
  private final VanillaNetworkTypeahead<E> networkTypeahead;

  public VanillaNetworkTypeaheadInitializer(Config<E> config) throws Exception {
    this.networkTypeahead = createTypeahead(config);
  }
  
  public VanillaNetworkTypeaheadInitializer(NetworkTypeaheadConfig<E> config) throws Exception {
    this.networkTypeahead = createTypeahead(config);
  }
  
  @Override
  public Typeahead<E> getTypeahead() {
    return networkTypeahead;
  }
  
  @Override
  public Indexer<E> getIndexer() {
    return networkTypeahead;
  }
  
  protected VanillaNetworkTypeahead<E> createTypeahead(NetworkTypeaheadConfig<E> config) throws Exception {
    // create elementStore
    ArrayStoreElement<E> elementStore = StoreFactory.createElementStorePartition(
        config.getElementStoreDir(),
        config.getElementStoreIndexStart(),
        config.getElementStoreCapacity(),
        new WriteBufferSegmentFactory(config.getElementStoreSegmentMB()),
        config.getElementStoreSegmentMB(),
        config.getElementSerializer());
    
    // load elementStore in memory
    if(config.isElementStoreCached()) {
      elementStore = new MemoryArrayStoreElement<E>(elementStore);
    }
    
    // create connectionsStore
    ArrayStoreConnections connectionsStore = StoreFactory.createArrayStoreConnections(
        config.getConnectionsStoreDir(),
        config.getConnectionsStoreCapacity(),
        config.getConnectionsStoreSegmentMB());
    
    // create selectorFactory
    SelectorFactory<E> selectorFactory = config.getSelectorFactory();
    if(selectorFactory == null) selectorFactory = new PrefixSelectorFactory<E>();
    
    // create bloomFilter
    BloomFilter<Integer> bloomFilter = new FnvBloomFilter(config.getFilterPrefixLength());
    
    // create connectionFilter
    ConnectionFilter connectionFilter = config.getConnectionFilter();
    if(connectionFilter == null) {
      connectionFilter = new TransitivePartitionConnectionFilter(new Range(config.getPartitionStart(), config.getPartitionCount()));
    }
    
    // create NetworkTypeahead
    return new VanillaNetworkTypeahead<E>(config.getName(), elementStore, connectionsStore, selectorFactory, bloomFilter, connectionFilter);
  }
  
  public static class Config<E extends Element> extends NetworkTypeaheadConfig<E> {}
}
