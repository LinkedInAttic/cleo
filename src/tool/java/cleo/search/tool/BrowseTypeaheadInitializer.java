package cleo.search.tool;

import cleo.search.Element;
import cleo.search.filter.BloomFilter;
import cleo.search.filter.FnvBloomFilterLong;
import cleo.search.selector.PrefixSelectorFactory;
import cleo.search.selector.SelectorFactory;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.MemoryArrayStoreElement;
import cleo.search.store.StoreFactory;
import cleo.search.typeahead.BrowseTypeahead;
import cleo.search.typeahead.BrowseTypeaheadConfig;
import cleo.search.typeahead.Typeahead;

/**
 * BrowseTypeaheadInitializer
 * 
 * @author jwu
 * @since 02/13, 2011
 */
public class BrowseTypeaheadInitializer<E extends Element> implements TypeaheadInitializer<E> {
  private final BrowseTypeahead<E> browseTypeahead;

  public BrowseTypeaheadInitializer(Config<E> config) throws Exception {
    this.browseTypeahead = createTypeahead(config);
  }
  
  public BrowseTypeaheadInitializer(BrowseTypeaheadConfig<E> config) throws Exception {
    this.browseTypeahead = createTypeahead(config);
  }
  
  protected BrowseTypeahead<E> createTypeahead(BrowseTypeaheadConfig<E> config) throws Exception {
    // create elementStore
    ArrayStoreElement<E> elementStore =
      StoreFactory.createElementStorePartition(
          config.getElementStoreDir(),
          config.getElementStoreIndexStart(),
          config.getElementStoreCapacity(),
          config.getElementStoreSegmentMB(),
          config.getElementSerializer());
    
    // load elementStore in memory
    elementStore = new MemoryArrayStoreElement<E>(elementStore);
    
    // create selectorFactory
    SelectorFactory<E> selectorFactory = config.getSelectorFactory();
    if(selectorFactory == null) selectorFactory = new PrefixSelectorFactory<E>();
    
    // Create BrowseTypeahead
    BloomFilter<Long> bloomFilter = new FnvBloomFilterLong(config.getFilterPrefixLength());
    
    return new BrowseTypeahead<E>(
        config.getName(),
        elementStore,
        selectorFactory,
        bloomFilter);
  }
  
  @Override
  public Typeahead<E> getTypeahead() {
    return browseTypeahead;
  }
  
  public static class Config<E extends Element> extends BrowseTypeaheadConfig<E> {}
}
