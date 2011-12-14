package cleo.search.test.store;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import cleo.search.store.DataStoreInts;
import cleo.search.store.KratiDataStore;
import cleo.search.store.KratiDataStoreInts;
import cleo.search.store.StoreFactory;
import cleo.search.test.util.FileUtils;

import junit.framework.TestCase;
import krati.core.segment.MemorySegmentFactory;
import krati.core.segment.SegmentFactory;

/**
 * TestDataStoreInts
 * 
 * @author jwu
 * @since 02/02, 2011
 */
public class TestDataStoreInts extends TestCase {
  protected Random rand = new Random();
  protected DataStoreInts store;
  
  public TestDataStoreInts(String name) {
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
      KratiDataStore underlyingStore =
        StoreFactory.createKratiDataStore(storeHome,
                                          initialCapacity,
                                          indexSegmentFileSizeMB,
                                          indexSegmentFactory,
                                          storeSegmentFileSizeMB,
                                          storeSegmentFactory);
      store = new KratiDataStoreInts(underlyingStore);
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
    String key = "key." + rand.nextInt(10000);
    
    int[] elemIds = new int[] { 0, 109, 201, 67983, 4391 };
    
    store.delete(key, System.currentTimeMillis());
    
    assertEquals(null, store.get(key));
    
    assertTrue(store.put(key, elemIds, System.currentTimeMillis()));
    
    int[] results = store.get(key);
    assertEquals(5, results.length);
    assertTrue(Arrays.equals(elemIds, results));
    
    store.remove(key, 0, System.currentTimeMillis());
    results = store.get(key);
    assertEquals(4, results.length);
    
    store.remove(key, 4391, System.currentTimeMillis());
    results = store.get(key);
    assertEquals(3, results.length);
    
    store.remove(key, 201, System.currentTimeMillis());
    results = store.get(key);
    assertEquals(2, results.length);
    
    store.remove(key, 109, System.currentTimeMillis());
    results = store.get(key);
    assertEquals(1, results.length);
    
    store.remove(key, 67983, System.currentTimeMillis());
    results = store.get(key);
    assertEquals(0, results.length);
    
    store.sync();
    
    store.put(key, elemIds, System.currentTimeMillis());
    store.add(key, -19, System.currentTimeMillis());
    store.add(key, -55, System.currentTimeMillis());
    results = store.get(key);
    assertEquals(elemIds.length + 2, results.length);
    
    Arrays.sort(results);
    
    assertEquals(-55, results[0]);
    assertEquals(-19, results[1]);
    assertEquals(0, results[2]);
    assertEquals(109, results[3]);
    assertEquals(201, results[4]);
    assertEquals(4391, results[5]);
    assertEquals(67983, results[6]);
    
    store.sync();
    assertEquals(store.getLWMark(), store.getHWMark());
  }
  
  public void testStress() throws Exception {
    int counter = 0;
    int opCount = 0;
    int base = Math.max(100, rand.nextInt(1000));
    
    long timeout = Math.max(10, rand.nextInt(30)) * 1000;
    long startTime = System.currentTimeMillis();
    long elapsedTime;
    
    while(true) {
      counter++;
      
      int[] elemIds = new int[rand.nextInt(1000)];
      for(int i = 0, cnt = elemIds.length; i < cnt; i++) {
        elemIds[i] = i * base + rand.nextInt(base);
      }
      
      String key = "key." + counter;
      store.put(key, elemIds, System.currentTimeMillis());
      opCount++;
      
      int[] results = store.get(key);
      assertTrue(Arrays.equals(elemIds, results));
      opCount++;
      
      for(int i = 0, cnt = elemIds.length; i < cnt; i++) {
        store.remove(key, elemIds[i], System.currentTimeMillis());
      }
      opCount += elemIds.length;
      
      results = store.get(key);
      assertEquals(0, results.length);
      opCount++;
      
      for(int i = 0, cnt = elemIds.length; i < cnt; i++) {
        store.add(key, elemIds[i], System.currentTimeMillis());
      }
      opCount += elemIds.length;
      
      results = store.get(key);
      assertEquals(elemIds.length, results.length);
      opCount++;
      
      elapsedTime = System.currentTimeMillis() - startTime;
      if(elapsedTime > timeout) {
        break;
      }
    }
    
    store.sync();
    assertEquals(store.getLWMark(), store.getHWMark());
    System.out.printf("\t%d operations in %d ms%n", opCount, elapsedTime);
  }
}
