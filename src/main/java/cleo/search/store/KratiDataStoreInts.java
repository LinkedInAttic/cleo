package cleo.search.store;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

import cleo.search.util.StringIterator;

/**
 * KratiDataStoreInts
 * 
 * @author jwu
 * @since 02/01, 2011
 */
public class KratiDataStoreInts implements DataStoreInts {
  private final static int NUM_BYTES_IN_INT = 4;
  private final KratiDataStore store;
  private volatile long counter = 0L;
  
  public KratiDataStoreInts(KratiDataStore store) {
    this.store = store;
  }
  
  @Override
  public int[] get(String key) {
    if(key == null) return null;
    
    byte[] dat = store.get(key.getBytes());
    if(dat == null) return null;
    
    ByteBuffer bb = ByteBuffer.wrap(dat);
    int cnt = dat.length / NUM_BYTES_IN_INT;
    int[] result = new int[cnt];
    for (int i = 0; i < cnt; i++) {
      result[i] = bb.getInt();
    }
    
    return result;
  }
  
  @Override
  public boolean put(String key, int[] elemIds, long scn) throws Exception {
    if(key == null) return false;
    
    if(elemIds == null) {
      store.put(key.getBytes(), null, scn);
      internalPersist(scn);
      return true;
    }
    
    if(elemIds.length == 0) {
      store.put(key.getBytes(), new byte[0], scn);
      internalPersist(scn);
      return true;
    }
    
    byte[] upd = new byte[NUM_BYTES_IN_INT * elemIds.length];
    ByteBuffer bb = ByteBuffer.wrap(upd);
    for (int i = 0; i < elemIds.length; i++) {
      bb.putInt(elemIds[i]);
    }
    
    // Update store
    store.put(key.getBytes(), upd, scn);
    internalPersist(scn);
    return true;
  }
  
  @Override
  public boolean delete(String key, long scn) throws Exception {
    if(key == null) return false;
    
    store.put(key.getBytes(), null, scn);
    internalPersist(scn);
    return true;
  }
  
  @Override
  public void add(String key, int elemId, long scn) throws Exception {
    if(key == null) throw new NullPointerException("key cannot be null");
    
    byte[] rawKey = key.getBytes();
    byte[] rawDat = store.get(key.getBytes());
    byte[] updDat = null;
    
    if (rawDat == null) {
      updDat = new byte[NUM_BYTES_IN_INT];
      ByteBuffer bb = ByteBuffer.wrap(updDat);
      bb.putInt(elemId);
    } else {
      ByteBuffer bb = ByteBuffer.wrap(rawDat);
      
      while ((bb.position() + NUM_BYTES_IN_INT) <= rawDat.length) {
        int id = bb.getInt();
        if (id == elemId) {
          internalPersist(scn);
          return;
        }
      }
      
      int safeLen = rawDat.length - (rawDat.length % NUM_BYTES_IN_INT);
      updDat = new byte[safeLen + NUM_BYTES_IN_INT];
      bb = ByteBuffer.wrap(updDat);
      bb.put(rawDat, 0, safeLen);
      bb.putInt(elemId);
    }
    
    // Update store
    store.put(rawKey, updDat, scn);
    internalPersist(scn);
  }
  
  @Override
  public void remove(String key, int elemId, long scn) throws Exception {
    if(key == null) throw new NullPointerException("key cannot be null");
    
    byte[] rawKey = key.getBytes();
    byte[] rawDat = store.get(rawKey);
    
    if (rawDat == null) {
      internalPersist(scn);
      return;
    }
    
    int rawCnt = rawDat.length / NUM_BYTES_IN_INT;
    ByteBuffer bb = ByteBuffer.wrap(rawDat);
    
    for(int i = 0; i < rawCnt; i++) {
      if (bb.getInt() == elemId) {
        bb.position(i * NUM_BYTES_IN_INT);
        break;
      }
    }
    
    int length1 = bb.position();
    if(length1 <= rawDat.length - NUM_BYTES_IN_INT) {
      byte[] updDat = null;
      updDat = new byte[(rawCnt - 1) * NUM_BYTES_IN_INT];
      System.arraycopy(rawDat, 0, updDat, 0, length1);
      System.arraycopy(rawDat, length1 + NUM_BYTES_IN_INT, updDat, length1, updDat.length - length1);
      store.put(rawKey, updDat, scn);
    }
    
    internalPersist(scn);
  }
  
  protected void internalPersist(long scn) throws Exception {
    counter++;
    if(counter % store.getUpdateBatchSize() == 0)
    {
      store.saveHWMark(scn);
      store.persist();
    }
  }
  
  @Override
  public void sync() throws IOException {
    store.sync();
  }
  
  @Override
  public void persist() throws IOException {
    store.persist();
  }
  
  @Override
  public void saveHWMark(long endOfPeriod) throws Exception {
    store.saveHWMark(endOfPeriod);
  }
  
  @Override
  public long getHWMark() {
    return store.getHWMark();
  }
  
  @Override
  public long getLWMark() {
    return store.getLWMark();
  }

  @Override
  public File getStoreHome() {
    return store.getStoreHome();
  }
  
  @Override
  public String getStatus() {
    return store.getStatus();
  }

  @Override
  public Iterator<String> keyIterator() {
    return new StringIterator(store.keyIterator());
  }
}
