package cleo.search.test.typeahead;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import cleo.search.Element;
import cleo.search.ElementSerializer;
import cleo.search.connection.ConnectionFilter;
import cleo.search.connection.TransitivePartitionConnectionFilter;
import cleo.search.filter.BloomFilter;
import cleo.search.filter.FnvBloomFilter;
import cleo.search.selector.ScoredPrefixSelectorFactory;
import cleo.search.selector.SelectorFactory;
import cleo.search.store.ArrayStoreConnections;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.StoreFactory;
import cleo.search.test.util.FileUtils;
import cleo.search.typeahead.VanillaNetworkTypeahead;
import cleo.search.util.Range;

import junit.framework.TestCase;
import krati.core.segment.MemorySegmentFactory;
import krati.core.segment.SegmentFactory;

/**
 * AbstractTestVanillaNetworkTypeahead
 * 
 * @author jwu
 * @since 02/13, 2011
 */
public abstract class AbstractTestVanillaNetworkTypeahead<E extends Element> extends TestCase {
  protected Random rand = new Random();
  protected VanillaNetworkTypeahead<E> typeahead;
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
    return new ScoredPrefixSelectorFactory<E>();
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
  
  protected ArrayStoreConnections createConnectionsStore() throws Exception {
    File connectionsStoreDir = new File(getHomeDir(), "connections-store");
    int connectionsStoreSegMB = 32;
    SegmentFactory connectionsStoreSegFactory = new MemorySegmentFactory();
    
    ArrayStoreConnections connectionsStore =
      StoreFactory.createArrayStoreConnections(
          connectionsStoreDir,
          getConnectionsStoreCapacity(),
          connectionsStoreSegFactory,
          connectionsStoreSegMB);
    return connectionsStore;
  }
  
  protected VanillaNetworkTypeahead<E> createTypeahead() throws Exception {
    return new VanillaNetworkTypeahead<E>(
        "Network",
        createElementStore(),
        createConnectionsStore(),
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
