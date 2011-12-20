/*
 * Copyright (c) 2011 LinkedIn, Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package cleo.search.store;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import krati.array.Array;

/**
 * KratiArrayStoreInts
 *
 * @author jwu
 * @since 09/30, 2010
 */
public class KratiArrayStoreInts implements ArrayStoreInts {
  protected final static int NUM_BYTES_IN_INT = 4;
  protected final KratiArrayStore store;
  private volatile long counter = 0L;
  
  public KratiArrayStoreInts(KratiArrayStore store) {
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
  public int capacity() {
    return store.capacity();
  }
  
  @Override
  public int getIndexStart() {
    return store.getIndexStart();
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
    return (store.getLength(index) / NUM_BYTES_IN_INT);
  }
  
  @Override
  public int[] get(int index) {
    byte[] dat = store.get(index);
    if(dat == null) return null;
    
    ByteBuffer bb = ByteBuffer.wrap(dat);
    int cnt = dat.length / 4;
    int[] result = new int[cnt];
    for (int i = 0; i < cnt; i++) {
      result[i] = bb.getInt();
    }
    
    return result;
  }
  
  /**
   * Replace the original element with an array of integers.
   * 
   * @param index
   * @param elemIds
   * @param scn
   * @throws Exception
   */
  @Override
  public synchronized void set(int index, int elemIds[], long scn) throws Exception {
    store.expandCapacity(index);
    
    if(elemIds == null || elemIds.length == 0) {
      store.set(index, null, scn);
      internalPersist(scn);
      return;
    }
    
    byte[] upd = new byte[NUM_BYTES_IN_INT * elemIds.length];
    ByteBuffer bb = ByteBuffer.wrap(upd);
    for (int i = 0; i < elemIds.length; i++) {
      bb.putInt(elemIds[i]);
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
  public synchronized void add(int index, int elemId, long scn) throws Exception {
    store.expandCapacity(index);
    
    byte[] upd = null;
    byte[] dat = store.get(index);
    
    if (dat == null) {
      upd = new byte[NUM_BYTES_IN_INT];
      ByteBuffer bb = ByteBuffer.wrap(upd);
      bb.putInt(elemId);
    } else {
      ByteBuffer bb = ByteBuffer.wrap(dat);
      
      while ((bb.position() + NUM_BYTES_IN_INT) <= dat.length) {
        int id = bb.getInt();
        if (id == elemId) {
          internalPersist(scn);
          return;
        }
      }
      
      int safeLen = dat.length - (dat.length % NUM_BYTES_IN_INT);
      upd = new byte[safeLen + NUM_BYTES_IN_INT];
      bb = ByteBuffer.wrap(upd);
      bb.put(dat, 0, safeLen);
      bb.putInt(elemId);
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
    
    int pos = 0;
    boolean foundElem = false;
    while ((pos + NUM_BYTES_IN_INT) <= dat.length) {
      if (bb.getInt() == elemId) {
        foundElem = true;
        break;
      }
      pos = bb.position();
    }
    
    // Shift data to the left if found in the middle
    if (pos < (dat.length - NUM_BYTES_IN_INT)) {
      for (int len = dat.length - NUM_BYTES_IN_INT; pos < len; pos++) {
        dat[pos] = dat[pos + NUM_BYTES_IN_INT];
      }
    }
    
    // Update store regardless to take care of the head/tail corner cases
    if (foundElem) {
      store.set(index, dat, 0, Math.max(0, dat.length - NUM_BYTES_IN_INT) /* length */, scn);
    }
  }
  
  @Override
  public void persist() throws IOException {
    store.persist();
  }
  
  @Override
  public void sync() throws IOException {
    store.sync();
  }
  
  protected void internalPersist(long scn) throws Exception {
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
  public Array.Type getType() {
    return store.getType();
  }
}
