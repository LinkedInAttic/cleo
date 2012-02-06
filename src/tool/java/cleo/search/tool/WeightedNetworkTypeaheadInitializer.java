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
import cleo.search.connection.ConnectionIndexer;
import cleo.search.connection.TransitivePartitionConnectionFilter;
import cleo.search.filter.BloomFilter;
import cleo.search.filter.FnvBloomFilter;
import cleo.search.selector.PrefixSelectorFactory;
import cleo.search.selector.SelectorFactory;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.ArrayStoreWeights;
import cleo.search.store.MemoryArrayStoreElement;
import cleo.search.store.StoreFactory;
import cleo.search.typeahead.NetworkTypeaheadConfig;
import cleo.search.typeahead.Typeahead;
import cleo.search.typeahead.WeightedNetworkTypeahead;
import cleo.search.util.Range;

/**
 * WeightedNetworkTypeaheadInitializer
 * 
 * @author jwu
 * @since 04/20, 2011
 * 
 * <p>
 * 02/06, 2012 - Optimize by loading weighted connection store before element store. <br/>
 */
public class WeightedNetworkTypeaheadInitializer<E extends Element> implements TypeaheadInitializer<E>, IndexerInitializer<E>, ConnectionIndexerInitializer {
  private final WeightedNetworkTypeahead<E> networkTypeahead;

  public WeightedNetworkTypeaheadInitializer(Config<E> config) throws Exception {
    this.networkTypeahead = createTypeahead(config);
  }
  
  public WeightedNetworkTypeaheadInitializer(NetworkTypeaheadConfig<E> config) throws Exception {
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
  
  @Override
  public ConnectionIndexer getConnectionIndexer() {
    return networkTypeahead;
  }
  
  protected WeightedNetworkTypeahead<E> createTypeahead(NetworkTypeaheadConfig<E> config) throws Exception {
    // create weightedConnectionsStore
    ArrayStoreWeights weightedConnectionsStore = StoreFactory.createArrayStoreWeights(
        config.getConnectionsStoreDir(),
        config.getConnectionsStoreCapacity(),
        config.getConnectionsStoreSegmentFactory(),
        config.getConnectionsStoreSegmentMB());
    
    // create elementStore
    ArrayStoreElement<E> elementStore = StoreFactory.createElementStorePartition(
        config.getElementStoreDir(),
        config.getElementStoreIndexStart(),
        config.getElementStoreCapacity(),
        config.getElementStoreSegmentFactory(),
        config.getElementStoreSegmentMB(),
        config.getElementSerializer());
    
    // load elementStore in memory
    if(config.isElementStoreCached()) {
      elementStore = new MemoryArrayStoreElement<E>(elementStore);
    }
    
    // create bloomFilter
    BloomFilter<Integer> bloomFilter = new FnvBloomFilter(config.getFilterPrefixLength());
    
    // create selectorFactory
    SelectorFactory<E> selectorFactory = config.getSelectorFactory();
    if(selectorFactory == null) selectorFactory = new PrefixSelectorFactory<E>();
    
    // create connectionFilter
    ConnectionFilter connectionFilter = config.getConnectionFilter();
    if(connectionFilter == null) {
      connectionFilter = new TransitivePartitionConnectionFilter(new Range(config.getPartitionStart(), config.getPartitionCount()));
    }
    
    // create WeightedNetworkTypeahead
    return new WeightedNetworkTypeahead<E>(config.getName(), elementStore, weightedConnectionsStore, selectorFactory, bloomFilter, connectionFilter);
  }
  
  public static class Config<E extends Element> extends NetworkTypeaheadConfig<E> {}
}
