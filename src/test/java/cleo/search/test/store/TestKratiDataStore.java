package cleo.search.test.store;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import cleo.search.store.KratiDataStore;
import cleo.search.store.StoreFactory;
import cleo.search.test.util.FileUtils;

import junit.framework.TestCase;
import krati.core.segment.MemorySegmentFactory;
import krati.core.segment.SegmentFactory;

/**
 * TestKratiDataStore
 * 
 * @author jwu
 * @since 02/02, 2011
 */
public class TestKratiDataStore extends TestCase {
  protected Random rand = new Random();
  protected KratiDataStore store;
  
  public TestKratiDataStore(String name) {
    super(name);
  }
  
  @Override
  protected void setUp() {
    int initialCapacity = 500000;
    String storeName = getClass().getSimpleName();
    File storeHome = FileUtils.getTestDir(storeName);
    
    int indexSegmentFileSizeMB = 8;
    SegmentFactory indexSegmentFactory = new MemorySegmentFactory();
    int storeSegmentFileSizeMB = 32;
    SegmentFactory storeSegmentFactory = new MemorySegmentFactory();
    
    try {
      store = StoreFactory.createKratiDataStore(storeHome,
                                                initialCapacity,
                                                indexSegmentFileSizeMB,
                                                indexSegmentFactory,
                                                storeSegmentFileSizeMB,
                                                storeSegmentFactory);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @Override
  protected void tearDown() {
    if(store != null) {
      try {
        FileUtils.deleteDirectory(store.getStoreHome());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  public void testApiBasics() throws Exception {
    int cnt = store.getUpdateBatchSize() + rand.nextInt(store.getUpdateBatchSize());
    
    // clear
    store.clear();
    
    // put & get
    for(int i = 0; i < cnt; i++) {
      String key = "key." + i;
      String value = "value." + i;
      store.put(key.getBytes(), value.getBytes(), System.currentTimeMillis());
      
      byte[] bytes = store.get(key.getBytes());
      assertTrue(Arrays.equals(value.getBytes(), bytes));
    }
    
    // get
    for(int i = 0; i < cnt; i++) {
      String key = "key." + i;
      String value = "value." + i;
      byte[] bytes = store.get(key.getBytes());
      assertTrue(Arrays.equals(value.getBytes(), bytes));
    }
    
    // sync
    assertTrue(store.getLWMark() <= store.getHWMark());
    store.sync();
    assertEquals(store.getLWMark(), store.getHWMark());
    
    // delete
    for(int i = 0; i < cnt; i++) {
      String key = "key." + i;
      assertTrue(store.delete(key.getBytes(), System.currentTimeMillis()));
    }
    
    // persist
    assertTrue(store.getLWMark() <= store.getHWMark());
    store.persist();
    assertEquals(store.getLWMark(), store.getHWMark());
    
    // get (returns null)
    for(int i = 0; i < cnt; i++) {
      String key = "key." + i;
      assertTrue(store.get(key.getBytes()) == null);
    }
    
    // put & get
    for(int i = 0; i < cnt; i++) {
      String key = "key." + i;
      String value = "value." + i;
      store.put(key.getBytes(), value.getBytes(), System.currentTimeMillis());
      
      byte[] bytes = store.get(key.getBytes());
      assertTrue(Arrays.equals(value.getBytes(), bytes));
    }
    
    // clear
    store.clear();
    
    // get (returns null)
    for(int i = 0; i < cnt; i++) {
      String key = "key." + i;
      assertTrue(store.get(key.getBytes()) == null);
    }
    
    // saveHWMark
    long endOfPeriod = System.currentTimeMillis() + 1000;
    store.saveHWMark(endOfPeriod);
    assertEquals(endOfPeriod, store.getHWMark());
    
    store.sync();
    assertEquals(endOfPeriod, store.getLWMark());
  }
  
  public void testKeyIterator() throws Exception {
    int cnt = store.getUpdateBatchSize() + rand.nextInt(store.getUpdateBatchSize());
    HashSet<String> keySet = new HashSet<String>();
    Iterator<byte[]> keyIter;
    
    // clear
    store.clear();
    
    keyIter = store.keyIterator();
    assertFalse(keyIter.hasNext());
    
    // put & get
    for(int i = 0; i < cnt; i++) {
      String key = "key." + i;
      String value = "value." + i;
      store.put(key.getBytes(), value.getBytes(), System.currentTimeMillis());
      
      byte[] bytes = store.get(key.getBytes());
      assertTrue(Arrays.equals(value.getBytes(), bytes));
      
      keySet.add(key);
    }
    
    assertEquals(cnt, keySet.size());
    
    keyIter = store.keyIterator();
    while(keyIter.hasNext()) {
      keySet.remove(new String(keyIter.next()));
    }
    assertEquals(0, keySet.size());
    
    keyIter = store.keyIterator();
    while(keyIter.hasNext()) {
      store.delete(keyIter.next(), System.currentTimeMillis());
    }
    
    keyIter = store.keyIterator();
    assertFalse(keyIter.hasNext());
  }
}
