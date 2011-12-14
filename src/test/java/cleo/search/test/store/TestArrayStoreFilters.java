package cleo.search.test.store;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import cleo.search.store.ArrayStoreFilters;
import cleo.search.store.KratiArrayStore;
import cleo.search.store.KratiArrayStoreFilters;
import cleo.search.store.StoreFactory;
import cleo.search.test.util.FileUtils;
import cleo.search.util.Weight;

import junit.framework.TestCase;

import krati.core.segment.MemorySegmentFactory;

/**
 * TestArrayStoreFilters
 * 
 * @author jwu
 * @since 01/15, 2011
 * 
 * <p>
 * 09/18, 2011 - Added testPartialRead <br/>
 */
public class TestArrayStoreFilters extends TestCase {
  protected Random rand = new Random();
  protected ArrayStoreFilters store;
  
  public TestArrayStoreFilters(String name) {
    super(name);
  }
  
  @Override
  protected void setUp() {
    int capacity = 1000;
    String storeName = getClass().getSimpleName();
    try {
      File storeHome = FileUtils.getTestDir(storeName);
      KratiArrayStore kas = StoreFactory.createKratiArrayStore(storeHome, capacity, new MemorySegmentFactory(), 32);
      store = new KratiArrayStoreFilters(kas);
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
    int uid = rand.nextInt(store.length());
    int elemId, elemFilter;
    int[][] filterData;
    
    // delete
    store.delete(uid, System.currentTimeMillis());
    filterData = store.getFilterData(uid);
    assertEquals(0, filterData[ArrayStoreFilters.ELEMID_SUBARRAY_INDEX].length);
    assertEquals(0, filterData[ArrayStoreFilters.FILTER_SUBARRAY_INDEX].length);
    
    // sync
    store.sync();
    assertEquals(store.getHWMark(), store.getHWMark());
    
    /**
     * Test add & remove
     */
    
    // add
    elemId = rand.nextInt(store.length());
    elemFilter = rand.nextInt(Integer.MAX_VALUE);
    int cnt = rand.nextInt(100);
    for(int i = 0; i < cnt; i++) {
      store.add(uid, elemId, elemFilter, System.currentTimeMillis());
      assertEquals(elemFilter, store.getFilter(uid, elemId));
      elemId++;
      elemFilter--;
    }
    assertEquals(cnt, store.getCount(uid));
    
    // get
    filterData = store.getFilterData(uid);
    assertEquals(2, filterData.length);
    assertEquals(cnt, filterData[ArrayStoreFilters.ELEMID_SUBARRAY_INDEX].length);
    assertEquals(cnt, filterData[ArrayStoreFilters.FILTER_SUBARRAY_INDEX].length);
    
    // remove
    for(int i = 0; i < cnt; i++) {
      elemId--;
      store.remove(uid, elemId, System.currentTimeMillis());
      assertEquals(0, store.getFilter(uid, elemId));
    }
    assertEquals(0, store.getCount(uid));
    
    // persist
    store.persist();
    assertEquals(store.getHWMark(), store.getHWMark());
    
    /**
     * Test setElementFilter/getElementFilter & remove
     */
    
    // setElementFilter/getElementFilter
    elemId = rand.nextInt(store.length());
    elemFilter = rand.nextInt(Integer.MAX_VALUE);
    cnt = rand.nextInt(100);
    for(int i = 0; i < cnt; i++) {
      store.setFilter(uid, elemId, elemFilter, System.currentTimeMillis());
      assertEquals(elemFilter, store.getFilter(uid, elemId));
      elemId++;
      elemFilter--;
    }
    assertEquals(cnt, store.getCount(uid));
    
    // get
    filterData = store.getFilterData(uid);
    assertEquals(2, filterData.length);
    assertEquals(cnt, filterData[ArrayStoreFilters.ELEMID_SUBARRAY_INDEX].length);
    assertEquals(cnt, filterData[ArrayStoreFilters.FILTER_SUBARRAY_INDEX].length);
    
    // remove
    for(int i = 0; i < cnt; i++) {
      elemId--;
      store.remove(uid, elemId, System.currentTimeMillis());
      assertEquals(0, store.getFilter(uid, elemId));
    }
    assertEquals(0, store.getCount(uid));
    
    // persist
    store.persist();
    assertEquals(store.getHWMark(), store.getHWMark());
    
    /**
     * Test set/get/delete
     */
    
    // set
    store.set(uid, filterData, System.currentTimeMillis());
    assertEquals(cnt, store.getCount(uid));
    
    // get
    int[][] filterTemp = store.getFilterData(uid);
    assertEquals(2, filterTemp.length);
    Arrays.equals(filterData[ArrayStoreFilters.ELEMID_SUBARRAY_INDEX], filterTemp[ArrayStoreFilters.ELEMID_SUBARRAY_INDEX]);
    Arrays.equals(filterData[ArrayStoreFilters.FILTER_SUBARRAY_INDEX], filterTemp[ArrayStoreFilters.FILTER_SUBARRAY_INDEX]);
    
    // delete
    store.delete(uid, System.currentTimeMillis());
    filterData = store.getFilterData(uid);
    assertEquals(2, filterData.length);
    assertEquals(0, filterData[ArrayStoreFilters.ELEMID_SUBARRAY_INDEX].length);
    assertEquals(0, filterData[ArrayStoreFilters.FILTER_SUBARRAY_INDEX].length);
    
    // sync
    store.sync();
    assertEquals(store.getHWMark(), store.getHWMark());
    
    /**
     * Test getBytes
     */
    // empty filterData
    filterData  = new int[][] { {}, {} };
    uid = rand.nextInt(store.length());
    store.setFilterData(uid, filterData, System.currentTimeMillis());
    
    byte[] expectBytes = new byte[filterData.length * filterData[0].length * 4];
    ByteBuffer bb = ByteBuffer.wrap(expectBytes);
    for(int i = 0; i < filterData[0].length; i++) {
      bb.putInt(filterData[ArrayStoreFilters.ELEMID_SUBARRAY_INDEX][i]);
      bb.putInt(filterData[ArrayStoreFilters.FILTER_SUBARRAY_INDEX][i]);
    }
    byte[] resultBytes = store.getBytes(uid);
    assertEquals(expectBytes.length, resultBytes.length);
    assertTrue(Arrays.equals(expectBytes, resultBytes));
    assertTrue(Arrays.equals(bb.array(), resultBytes));
    assertEquals(expectBytes.length, store.getBytes(uid, resultBytes));
    assertEquals(expectBytes.length, store.getBytes(uid, resultBytes, 0));
    
    // normal filterData
    filterData  = new int[][] { {1, 2, 19}, {10, 101, 23} };
    uid = rand.nextInt(store.length());
    store.setFilterData(uid, filterData, System.currentTimeMillis());
    
    expectBytes = new byte[filterData.length * filterData[0].length * 4];
    bb = ByteBuffer.wrap(expectBytes);
    for(int i = 0; i < filterData[0].length; i++) {
      bb.putInt(filterData[ArrayStoreFilters.ELEMID_SUBARRAY_INDEX][i]);
      bb.putInt(filterData[ArrayStoreFilters.FILTER_SUBARRAY_INDEX][i]);
    }
    
    resultBytes = store.getBytes(uid);
    assertEquals(expectBytes.length, resultBytes.length);
    assertTrue(Arrays.equals(expectBytes, resultBytes));
    assertTrue(Arrays.equals(bb.array(), resultBytes));
    assertEquals(expectBytes.length, store.getBytes(uid, resultBytes));
    assertEquals(expectBytes.length, store.getBytes(uid, resultBytes, 0));
  }
  
  public void testPartialRead() throws Exception {
    int numRuns = 1 + rand.nextInt(100);
    while(numRuns-- > 0) {
      int base = rand.nextInt(1000);
      int num = base + rand.nextInt(1000);
      int index = rand.nextInt(store.length());
      int length = num * (Weight.ELEMENT_ID_NUM_BYTES + Weight.ELEMENT_WEIGHT_NUM_BYTES);
      
      byte[] bytes = new byte[length];
      int[] elemIds = new int[num];
      int[] elemFilters = new int[num];
      ByteBuffer bb = ByteBuffer.wrap(bytes);
      ArrayList<Weight> list = new ArrayList<Weight>(num);
      
      for(int i = 0; i < num; i++) {
        Weight weight = new Weight(i, rand.nextInt(10000));
        list.add(weight);
        bb.putInt(weight.elementId);
        bb.putInt(weight.elementWeight);
        elemIds[i] = weight.elementId;
        elemFilters[i] = weight.elementWeight;
      }
      
      store.set(index, new int[][] {elemIds, elemFilters}, System.currentTimeMillis());
      
      int length1 = base * (Weight.ELEMENT_ID_NUM_BYTES + Weight.ELEMENT_WEIGHT_NUM_BYTES);
      byte[] src1 = new byte[length1];
      byte[] dst1 = new byte[length1];
      System.arraycopy(bytes, 0, src1, 0, src1.length);
      store.readBytes(index, dst1);
      assertTrue(Arrays.equals(src1, dst1));
      
      store.readBytes(index, 0, dst1);
      assertTrue(Arrays.equals(src1, dst1));
      
      int offset = length1;
      int length2 = length - length1;
      byte[] src2 = new byte[length2];
      byte[] dst2 = new byte[length2];
      System.arraycopy(bytes, offset, src2, 0, src2.length);
      store.readBytes(index, offset, dst2);
      assertTrue(Arrays.equals(src2, dst2));
    }
  }
}
