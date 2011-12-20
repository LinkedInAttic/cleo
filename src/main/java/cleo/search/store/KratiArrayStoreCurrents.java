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

import org.apache.log4j.Logger;

import krati.array.Array;
import krati.core.StoreConfig;
import krati.core.array.basic.DynamicIntArray;
import krati.core.segment.MemorySegmentFactory;
import krati.core.segment.SegmentFactory;
import krati.store.BytesDB;

/**
 * KratiArrayStoreCurrents (e.g. CurrentCompaniesStore)
 * 
 * @author jwu
 * @since 01/04, 2011
 */
public class KratiArrayStoreCurrents implements ArrayStoreInts {
  private final File homeDir;
  private final String homePath;
  private final BytesDB internalDB;
  private final DynamicIntArray mainArray;
  
  private final int batchSize;
  private final int numSyncBatches;
  private volatile long counter = 0L;
  
  final static int NUM_BYTES_IN_INT = 4;
  final static int CURRENT_COMPANY_MASK = Integer.MAX_VALUE; // All 1's for the least significant 31 bits
  final static int CURRENT_COMPANY_IN_ADDR = (1 << 31);      // Same as Integer.MIN_VALUE
  final static Logger logger = Logger.getLogger(KratiArrayStoreCurrents.class);
  
  public KratiArrayStoreCurrents(int length,
                                 int batchSize,
                                 int numSyncBatches,
                                 String homeDirPath) throws Exception {
    this(length, batchSize, numSyncBatches, homeDirPath, new MemorySegmentFactory());
  }
  
  public KratiArrayStoreCurrents(int length,
                                 int batchSize,
                                 int numSyncBatches,
                                 String homeDirPath,
                                 SegmentFactory segmentFactory) throws Exception {
    this.batchSize = batchSize;
    this.numSyncBatches = numSyncBatches;
    this.homeDir = new File(homeDirPath);
    this.homePath = homeDir.getCanonicalPath();
    this.mainArray = new DynamicIntArray(batchSize, numSyncBatches, homeDir);
    this.mainArray.expandCapacity(length - 1);
    
    final int segmentFileSizeMB = 64;
    final File bytesDBHomeDir = new File(homeDir, "BytesDB");
    
    StoreConfig config = new StoreConfig(bytesDBHomeDir, length);
    config.setBatchSize(batchSize);
    config.setNumSyncBatches(numSyncBatches);
    config.setSegmentFileSizeMB(segmentFileSizeMB);
    config.setSegmentFactory(segmentFactory);
    this.internalDB = new BytesDB(config);
  }
  
  @Override
  public final File getStoreHome() {
    return homeDir;
  }
  
  public final String getStoreHomePath() {
    return homePath;
  }
    
  public final int getNumSyncBatches() {
    return numSyncBatches;
  }
  
  public final int getUpdateBatchSize() {
    return batchSize;
  }
  
  @Override
  public String getStatus() {
    StringBuilder buffer = new StringBuilder();

    buffer.append("path");
    buffer.append("=");
    buffer.append(homePath);
    buffer.append(" ");

    buffer.append("length");
    buffer.append("=");
    buffer.append(length());
    buffer.append(" ");

    buffer.append("lwMark");
    buffer.append("=");
    buffer.append(getLWMark());
    buffer.append(" ");

    buffer.append("hwMark");
    buffer.append("=");
    buffer.append(getHWMark());

    return buffer.toString();
  }
  
  private final void assertPositive(int elemId) {
    if(elemId <= 0) {
      throw new IllegalArgumentException("non-positive number not allowed: " + elemId);
    }
  }
  
  private final void assertPositive(int[] elemIds) {
    if(elemIds != null) {
      for(int i : elemIds) {
        if(i <= 0) {
          throw new IllegalArgumentException("non-positive number not allowed: " + i);
        }
      }
    }
  }
  
  private final int dbIndexEncode(int dbIndex) {
    return CURRENT_COMPANY_IN_ADDR | dbIndex;
  }
  
  private final int dbIndexDecode(int dbIndexCode) {
    return CURRENT_COMPANY_MASK & dbIndexCode;
  }
  
  @Override
  public int capacity() {
    return mainArray.length();
  }
  
  @Override
  public int getIndexStart() {
    return 0;
  }
  
  @Override
  public int length() {
    return mainArray.length();
  }
  
  @Override
  public boolean hasIndex(int index) {
    return mainArray.hasIndex(index);
  }
  
  @Override
  public int getCount(int index) {
    if(mainArray.hasIndex(index)) {
      int val = mainArray.get(index);
      
      if (val > 0) {
        return 1;
      }
      
      if (val < 0) {
        int dbIndex = dbIndexDecode(val);
        return internalDB.hasIndex(dbIndex) ? (Math.max(0, internalDB.getLength(dbIndex)) / NUM_BYTES_IN_INT) : 0;
      }
    }
    
    return 0;
  }
  
  @Override
  public int[] get(int index) {
    int val = mainArray.get(index);
    if(val < 0) {
      int dbIndex = dbIndexDecode(val);
      if(internalDB.hasIndex(dbIndex)) {
        byte[] dat = internalDB.get(dbIndex);
        
        if(dat == null || dat.length == 0) {
          return new int[0];
        }
        
        int[] result = new int[dat.length / NUM_BYTES_IN_INT];
        ByteBuffer bb = ByteBuffer.wrap(dat);
        for(int i = 0; i < result.length; i++) {
          result[i] = bb.getInt();
        }
        
        return result;
      }
    }
    
    if(val > 0) {
      return new int[] { val };
    }
    
    return new int[0];
  }

  @Override
  public synchronized void set(int index, int[] elemIds, long scn) throws Exception {
    assertPositive(elemIds);
    mainArray.expandCapacity(index);
    
    int val = mainArray.get(index);
    if(val < 0) {
      int dbIndex = dbIndexDecode(val);
      if(internalDB.hasIndex(dbIndex)) {
        if(elemIds == null || elemIds.length == 0) {
          internalDB.set(dbIndex, null, scn);
          mainArray.set(index, 0, scn);
        } else if(elemIds.length == 1) {
          internalDB.set(dbIndex, null, scn);
          mainArray.set(index, elemIds[0], scn);
        } else {
          internalDB.set(dbIndex, bytes(elemIds), scn);
        }
        
        internalPersist(scn);
        return;
      }
    }
    
    if(elemIds == null || elemIds.length == 0) {
      mainArray.set(index, 0, scn);
    } else if(elemIds.length == 1) {
      mainArray.set(index, elemIds[0], scn);
    } else {
      int dbIndex = internalDB.add(bytes(elemIds), scn);
      mainArray.set(index, dbIndexEncode(dbIndex), scn);
    }
    
    internalPersist(scn);
  }
  
  @Override
  public synchronized void add(int index, int elemId, long scn) throws Exception {
    assertPositive(elemId);
    mainArray.expandCapacity(index);
    
    int val = mainArray.get(index);
    if(val < 0) {
      int dbIndex = dbIndexDecode(val);
      if(internalDB.hasIndex(dbIndex)) {
        addInternal(dbIndex, elemId, scn);
        internalPersist(scn);
        return;
      }
    }
    
    if(val <= 0) {
      mainArray.set(index, elemId, scn);
    } else if(val != elemId) {
      int[] elems = { val, elemId };
      int dbIndex = internalDB.add(bytes(elems), scn);
      mainArray.set(index, dbIndexEncode(dbIndex), scn);
    }
    
    internalPersist(scn);
  }
  
  @Override
  public synchronized void remove(int index, int elemId, long scn) throws Exception {
    if(!mainArray.hasIndex(index)) {
      return;
    }
    
    int val = mainArray.get(index);
    if(val < 0) {
      int dbIndex = dbIndexDecode(val);
      if(internalDB.hasIndex(dbIndex)) {
        removeInternal(index, dbIndex, elemId, scn);
        internalPersist(scn);
        return;
      }
    }
    
    if(val < 0 || val == elemId) {
      mainArray.set(index, 0, scn);
    }
    
    internalPersist(scn);
  }

  @Override
  public void delete(int index, long scn) throws Exception {
    if(!mainArray.hasIndex(index)) {
      return;
    }
    
    int val = mainArray.get(index);
    if(val < 0) {
      int dbIndex = dbIndexDecode(val);
      if(internalDB.hasIndex(dbIndex)) {
        internalDB.set(dbIndex, null, scn);
      }
    }
    
    mainArray.set(index, 0, scn);
    internalPersist(scn);
  }
  
  private void addInternal(int dbIndex, int elemId, long scn) throws Exception {
    byte[] dat = internalDB.get(dbIndex);
    ByteBuffer bb = ByteBuffer.wrap(dat);
    
    while ((bb.position() + NUM_BYTES_IN_INT) <= dat.length) {
      int id = bb.getInt();
      if (id == elemId) return;
    }
    
    int safeLen = dat.length - (dat.length % NUM_BYTES_IN_INT);
    byte[] upd = new byte[safeLen + NUM_BYTES_IN_INT];
    bb = ByteBuffer.wrap(upd);
    bb.put(dat, 0, safeLen);
    bb.putInt(elemId);
    
    internalDB.set(dbIndex, upd, scn);
  }
  
  private void removeInternal(int index, int dbIndex, int elemId, long scn) throws Exception {
    byte[] dat = internalDB.get(dbIndex);
    if(dat == null) return;
    
    int pos = 0;
    ByteBuffer bb = ByteBuffer.wrap(dat);
    
    while((pos + NUM_BYTES_IN_INT) <= dat.length) {
      if(bb.getInt() == elemId) break;
      pos = bb.position();
    }
    
    int newLen = Math.max(0, dat.length - NUM_BYTES_IN_INT);
    
    // Shift data to the left if found in the middle
    if(pos < newLen) {
      for (; pos < newLen; pos++) {
        dat[pos] = dat[pos + NUM_BYTES_IN_INT];
      }
    }
    
    if(pos <= newLen) {
      if(newLen < NUM_BYTES_IN_INT) {
        internalDB.set(dbIndex, null, scn);
        mainArray.set(index, 0, scn);
      } else if(newLen == NUM_BYTES_IN_INT) {
        bb.position(0);
        internalDB.set(dbIndex, null, scn);
        mainArray.set(index, bb.getInt(), scn);
      } else {
        internalDB.set(dbIndex, dat, 0, newLen, scn);
      }
    }
  }
  
  private byte[] bytes(int[] elemIds) {
    byte[] bytes = new byte[NUM_BYTES_IN_INT * elemIds.length];
    ByteBuffer bb = ByteBuffer.wrap(bytes);
    
    for(int i = 0; i < elemIds.length; i++) {
      bb.putInt(elemIds[i]);
    }
    
    return bytes;
  }
  
  protected void internalPersist(long scn) throws Exception {
    counter++;
    if(counter % batchSize == 0) {
      mainArray.saveHWMark(scn);
      persist();
    }
  }

  @Override
  public synchronized void sync() throws IOException {
    if(internalDB.getHWMark() != mainArray.getHWMark()) {
      try {
        saveHWMark(Math.max(internalDB.getHWMark(), mainArray.getHWMark()));
      } catch(IOException ioe) {
        throw ioe;
      } catch(Exception e) {
        logger.warn("failed to sync hwm", e);
      }
    }
    
    internalDB.sync();
    mainArray.sync();
  }
  
  @Override
  public synchronized void persist() throws IOException {
    if(internalDB.getHWMark() != mainArray.getHWMark()) {
      try {
        saveHWMark(Math.max(internalDB.getHWMark(), mainArray.getHWMark()));
      } catch(Exception e) {
        logger.warn("failed to saveHWMark", e);
      }
    }
    
    internalDB.persist();
    mainArray.persist();
  }
  
  @Override
  public synchronized void saveHWMark(long endOfPeriod) throws Exception {
    internalDB.saveHWMark(endOfPeriod);
    mainArray.saveHWMark(endOfPeriod);
  }
  
  @Override
  public long getHWMark() {
    return Math.max(internalDB.getHWMark(), mainArray.getHWMark());
  }
  
  @Override
  public long getLWMark() {
    return Math.min(internalDB.getLWMark(), mainArray.getLWMark());
  }
  
  @Override
  public void clear() {
    mainArray.clear();
    internalDB.clear();
  }
  
  @Override
  public Array.Type getType() {
    return Array.Type.DYNAMIC;
  }
}
