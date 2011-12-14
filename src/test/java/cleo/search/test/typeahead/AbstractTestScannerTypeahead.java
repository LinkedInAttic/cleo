package cleo.search.test.typeahead;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import junit.framework.TestCase;
import cleo.search.Element;
import cleo.search.ElementSerializer;
import cleo.search.filter.BloomFilter;
import cleo.search.filter.FnvBloomFilterLong;
import cleo.search.selector.ScoredPrefixSelectorFactory;
import cleo.search.selector.SelectorFactory;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.StoreFactory;
import cleo.search.test.util.FileUtils;
import cleo.search.typeahead.ScannerTypeahead;

/**
 * AbstractTestScannerTypeahead
 * 
 * @author jwu
 * @since 03/24, 2011
 */
public abstract class AbstractTestScannerTypeahead<E extends Element> extends TestCase {
  protected Random rand = new Random();
  protected ScannerTypeahead<E> typeahead;
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
  
  protected BloomFilter<Long> createBloomFilter() {
    return new FnvBloomFilterLong(getFilterPrefixLength());
  }
  
  protected SelectorFactory<E> createSelectorFactory() {
    return new ScoredPrefixSelectorFactory<E>();
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
  
  protected ScannerTypeahead<E> createTypeahead() throws Exception {
    return new ScannerTypeahead<E>(
        "Scanner",
        createElementStore(),
        createSelectorFactory(),
        createBloomFilter());
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
