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
    
    // create weightedConnectionsStore
    ArrayStoreWeights weightedConnectionsStore = StoreFactory.createArrayStoreWeights(
        config.getConnectionsStoreDir(),
        config.getConnectionsStoreCapacity(),
        config.getConnectionsStoreSegmentFactory(),
        config.getConnectionsStoreSegmentMB());
    
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
