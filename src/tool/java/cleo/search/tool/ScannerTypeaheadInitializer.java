package cleo.search.tool;

import cleo.search.Element;
import cleo.search.filter.BloomFilter;
import cleo.search.filter.FnvBloomFilterLong;
import cleo.search.selector.PrefixSelectorFactory;
import cleo.search.selector.SelectorFactory;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.MemoryArrayStoreElement;
import cleo.search.store.StoreFactory;
import cleo.search.typeahead.ScannerTypeahead;
import cleo.search.typeahead.ScannerTypeaheadConfig;
import cleo.search.typeahead.Typeahead;

/**
 * ScannerTypeaheadInitializer
 * 
 * @author jwu
 * @since 03/24, 2011
 */
public class ScannerTypeaheadInitializer<E extends Element> implements TypeaheadInitializer<E> {
  private final ScannerTypeahead<E> scannerTypeahead;

  public ScannerTypeaheadInitializer(Config<E> config) throws Exception {
    this.scannerTypeahead = createTypeahead(config);
  }
  
  public ScannerTypeaheadInitializer(ScannerTypeaheadConfig<E> config) throws Exception {
    this.scannerTypeahead = createTypeahead(config);
  }
  
  protected ScannerTypeahead<E> createTypeahead(ScannerTypeaheadConfig<E> config) throws Exception {
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
    
    return new ScannerTypeahead<E>(
        config.getName(),
        elementStore,
        selectorFactory,
        bloomFilter);
  }
  
  @Override
  public Typeahead<E> getTypeahead() {
    return scannerTypeahead;
  }
  
  public static class Config<E extends Element> extends ScannerTypeaheadConfig<E> {}
}

