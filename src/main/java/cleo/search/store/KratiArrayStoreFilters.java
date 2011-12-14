package cleo.search.store;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import krati.array.Array;

/**
 * KratiArrayStoreFilters
 * 
 * @author jwu
 * @since 01/11, 2011
 * 
 * <p>
 * 09/18, 2011 - Added readBytes() to support partial reads <br/>
 */
public class KratiArrayStoreFilters implements ArrayStoreFilters {
  protected final static int NUM_BYTES_IN_INT = 4;
  protected final KratiArrayStore store;
  private volatile long counter = 0L;
  
  public KratiArrayStoreFilters(KratiArrayStore store) {
    this.store = store;
  }
  
  @Override
  public String getStatus() {
    return store.getStatus();
  }
  
  @Override
  public final File getStoreHome() {
    return store.getStoreHome();
  }
  
  @Override
  public final int length() {
    return store.length();
  }
  
  @Override
  public final boolean hasIndex(int index) {
    return store.hasIndex(index);
  }
  
  @Override
  public int getCount(int index) {
    return Math.max(0, store.getLength(index) / NUM_BYTES_IN_INT / 2);
  }
  
  @Override
  public int getLength(int index) {
    return store.getLength(index);
  }
  
  @Override
  public int[][] get(int index) {
    byte[] dat = store.get(index);
    if(dat == null) return new int[2][0];
    
    ByteBuffer bb = ByteBuffer.wrap(dat);
    int cnt = dat.length / NUM_BYTES_IN_INT / 2;
    int[][] results = new int[2][cnt];
    for (int i = 0; i < cnt; i++) {
      results[ArrayStoreFilters.ELEMID_SUBARRAY_INDEX][i] = bb.getInt();
      results[ArrayStoreFilters.FILTER_SUBARRAY_INDEX][i] = bb.getInt();
    }
    
    return results;
  }
  
  /**
   * Sets new element filter data at an array index.
   * 
   * @param index
   * @param filterData
   * @param scn
   * @throws Exception
   */
  @Override
  public synchronized void set(int index, int[][] filterData, long scn) throws Exception {
    store.expandCapacity(index);
    
    if(filterData == null || filterData.length == 0) {
      store.set(index, null, scn);
      internalPersist(scn);
      return;
    }
    
    // Check input data
    if(filterData.length != 2 ||
       (filterData[ArrayStoreFilters.ELEMID_SUBARRAY_INDEX].length !=
        filterData[ArrayStoreFilters.ELEMID_SUBARRAY_INDEX].length) ) {
      throw new IllegalArgumentException("Invalid filterData");
    }
    
    byte[] upd = new byte[NUM_BYTES_IN_INT * filterData[0].length * 2];
    ByteBuffer bb = ByteBuffer.wrap(upd);
    for (int i = 0, cnt = filterData[0].length; i < cnt; i++) {
      bb.putInt(filterData[ArrayStoreFilters.ELEMID_SUBARRAY_INDEX][i]);
      bb.putInt(filterData[ArrayStoreFilters.FILTER_SUBARRAY_INDEX][i]);
    }
    
    // Update store
    store.set(index, upd, scn);
    internalPersist(scn);
  }
  
  @Override
  public synchronized void delete(int index, long scn) throws Exception {
    if(store.hasIndex(index)) {
      store.set(index, null, scn);
    }
    internalPersist(scn);
  }
  
  @Override
  public synchronized void add(int index, int elemId, int elemFilter, long scn) throws Exception {
    store.expandCapacity(index);

    byte[] upd = null;
    byte[] dat = store.get(index);

    if (dat == null) {
      upd = new byte[NUM_BYTES_IN_INT << 1];
      ByteBuffer bb = ByteBuffer.wrap(upd);
      bb.putInt(elemId);
      bb.putInt(elemFilter);
    } else {
      ByteBuffer bb = ByteBuffer.wrap(dat);
      
      int cnt = dat.length / NUM_BYTES_IN_INT / 2;
      for(int i = 0; i < cnt; i++) {
        int id = bb.getInt();
        int filter = bb.getInt();
        if(id == elemId) {
          if(filter == elemFilter) {
            internalPersist(scn);
            return;
          } else {
            bb.position(bb.position() - NUM_BYTES_IN_INT);
            bb.putInt(filter);
            upd = dat;
          }
        }
      }
      
      if(upd == null) {
        int addBytesCnt = NUM_BYTES_IN_INT << 1;
        int safeLen = dat.length - (dat.length % addBytesCnt);
        upd = new byte[safeLen + addBytesCnt];
        bb = ByteBuffer.wrap(upd);
        bb.put(dat, 0, safeLen);
        bb.putInt(elemId);
        bb.putInt(elemFilter);
      }
    }
    
    // Update store
    store.set(index, upd, scn);
    internalPersist(scn);
  }
  
  @Override
  public synchronized void remove(int index, int elemId, long scn) throws Exception {
    store.expandCapacity(index);
    
    byte[] dat = store.get(index);
    if (dat == null) {
      internalPersist(scn);
      return;
    }
    
    ByteBuffer bb = ByteBuffer.wrap(dat);
    int cnt = dat.length / NUM_BYTES_IN_INT / 2;
    for(int i = 0; i < cnt; i++) {
      if(bb.getInt() == elemId) {
        bb.position(bb.position() - NUM_BYTES_IN_INT);
        break;
      }
      
      bb.position(bb.position() + NUM_BYTES_IN_INT); // Pass filter
    }
    
    int ind = bb.position() / NUM_BYTES_IN_INT / 2;
    
    // Update store regardless to take care of the head/tail corner cases
    if (ind < cnt) {
      int delBytesCnt = NUM_BYTES_IN_INT << 1;
      
      // Shift data to the left if found in the middle
      if (ind < (cnt - 1)) {
        for (int pos = bb.position(), len = dat.length - delBytesCnt; pos < len; pos++) {
          dat[pos] = dat[pos + delBytesCnt];
        }
      }
      
      store.set(index, dat, 0, Math.max(0, dat.length - delBytesCnt) /* length */, scn);
    }
    
    internalPersist(scn);
  }
  
  @Override
  public void persist() throws IOException {
    store.persist();
  }
  
  @Override
  public void sync() throws IOException {
    store.sync();
  }
  
  protected final void internalPersist(long scn) throws Exception {
    counter++;
    if(counter % store.getUpdateBatchSize() == 0) {
      store.saveHWMark(scn);
      store.persist();
    }
  }
  
  @Override
  public long getLWMark() {
    return store.getLWMark();
  }
  
  @Override
  public long getHWMark() {
    return store.getHWMark();
  }
  
  @Override
  public void saveHWMark(long endOfPeriod) throws Exception {
    store.saveHWMark(endOfPeriod);
  }
  
  @Override
  public void clear() {
    store.clear();
  }

  @Override
  public int getFilter(int index, int elemId) {
    byte[] dat = store.get(index);
    if(dat == null) return 0;
    
    ByteBuffer bb = ByteBuffer.wrap(dat);
    int cnt = dat.length / NUM_BYTES_IN_INT / 2;
    for (int i = 0; i < cnt; i++) {
      if(bb.getInt() == elemId) {
        return bb.getInt();
      }
      
      // Bypass filter if elemIds do not match
      bb.position(bb.position() + NUM_BYTES_IN_INT);
    }
    
    return 0;
  }

  @Override
  public void setFilter(int index, int elemId, int elemFilter, long scn) throws Exception {
    add(index, elemId, elemFilter, scn);
  }

  @Override
  public int[][] getFilterData(int index) {
    return get(index);
  }

  @Override
  public void setFilterData(int index, int[][] filterData, long scn) throws Exception {
    set(index, filterData, scn);
  }

  @Override
  public byte[] getBytes(int index) {
    return store.get(index);
  }

  @Override
  public int getBytes(int index, byte[] dst) {
    return store.get(index, dst);
  }

  @Override
  public int getBytes(int index, byte[] dst, int offset) {
    return store.get(index, dst, offset);
  }
  
  @Override
  public int readBytes(int index, byte[] dst) {
    return store.read(index, dst);
  }

  @Override
  public int readBytes(int index, int offset, byte[] dst) {
    return store.read(index, offset, dst);
  }
  
  @Override
  public Array.Type getType() {
    return store.getType();
  }
}
