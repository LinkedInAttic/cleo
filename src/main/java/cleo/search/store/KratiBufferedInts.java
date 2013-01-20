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
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import krati.PersistableListener;
import krati.core.segment.MemorySegmentFactory;
import krati.core.segment.SegmentFactory;
import krati.io.Serializer;

/**
 * KratiBufferedInts
 * 
 * @author jwu
 * @since 09/12, 2012
 */
public class KratiBufferedInts implements DataStoreInts {
  /**
   * The logger.
   */
  private static final Logger logger = Logger.getLogger(KratiBufferedInts.class);
  
  /**
   * The number of integers in buffer.
   */
  protected volatile int bufferSize;
  
  /**
   * The UTF-8 java string serializer.
   */
  protected final Serializer<String> serializer;
  
  /**
   * The data store for buffering list of integers.
   */
  protected final KratiDataStore buffer;
  
  /**
   * The buffering store for list of integers.
   */
  protected final KratiDataStoreInts bufInts;
  
  /**
   * The data store extension for storing list of integers.
   */
  protected final KratiArrayStore extension;
  
  /**
   * The extension store for list of integers.
   */
  protected final KratiArrayStoreInts extInts;
  
  /**
   * Fine-granularity reentrant locks.
   */
  protected final ReentrantLock[] lockArray;
  
  /**
   * The default buffer size is 512.
   */
  public static final int DEFAULT_BUFFER_SIZE = 512;
  
  /**
   * The minimum buffer size is 128.
   */
  public static final int MINIMUM_BUFFER_SIZE = 128;
  
  /**
   * Creates a new KratiBufferedInts.
   * 
   * @param store - the underlying store
   * @throws Exception
   */
  public KratiBufferedInts(KratiDataStore store) throws Exception {
    this(DEFAULT_BUFFER_SIZE, store);
  }
  
  /**
   * Creates a new KratiBufferedInts.
   * 
   * @param bufferSize - the size of elementId buffer
   * @param store - the underlying store
   * @throws Exception 
   */
  public KratiBufferedInts(int bufferSize, KratiDataStore store) throws Exception {
    this.bufferSize = Math.max(bufferSize, MINIMUM_BUFFER_SIZE);
    
    // Initialize buffering
    this.buffer = store;
    this.bufInts = new KratiDataStoreInts(store);
    
    // Initialize extension
    this.extension = initExtension(new MemorySegmentFactory());
    this.extInts = new KratiArrayStoreInts(extension);
    
    // Initialize buffer persist listener
    initPersistableListener();
    
    // Initialize reentrant locks
    lockArray = new ReentrantLock[32];
    for(int i = 0; i < lockArray.length; i++) {
        lockArray[i] = new ReentrantLock();
    }
    
    serializer = bufInts.getKeySerializer();
  }
  
  /**
   * Initializes the buffer persist listener.
   */
  protected void initPersistableListener() {
    final PersistableListener originalListener = buffer.getUnderlyingStore().getPersistableListener();
    buffer.getUnderlyingStore().setPersistableListener(new PersistableListener() {
      final PersistableListener origin = originalListener; 
      @Override
      public void beforePersist() {
        try {
          extension.persist();
          if(origin != null) {
            origin.beforePersist();
          }
        } catch(Exception e) {
          logger.error("failed on calling beforePersist", e);
        }
      }
      
      @Override
      public void afterPersist() {
        if(origin != null) {
          origin.afterPersist();
        }
      }
    });
  }
  
  /**
   * Initializes the extension store.
   * 
   * @throws Exception
   */
  protected KratiArrayStore initExtension(SegmentFactory segmentFactory) throws Exception {
    final File extHomeDir = new File(buffer.getStoreHome(), "store-ext");
    final int initialCapacity = buffer.getUnderlyingStore().capacity();
    final int batchSize = 1000;
    final int numSyncBatches = 100;
    int segmentFileSizeMB = 128;
    double segmentCompactFactor = 0.5;
    
    KratiArrayStore extStore = new KratiArrayStore(
        initialCapacity,
        batchSize,
        numSyncBatches,
        extHomeDir,
        segmentFactory,
        segmentFileSizeMB,
        segmentCompactFactor);
    
    return extStore;
  }
  
  protected int getExtensionIndex(String key) {
    byte[] rawKey = serializer.serialize(key);
    return buffer.getUnderlyingStore().getDBIndex(rawKey);
  }
  
  /**
   * Gets the reentrant read-write lock the specified <code>key</code>.
   */
  protected ReentrantLock getLock(String key) {
      int hashCode = key.hashCode();
      int pos = hashCode % lockArray.length;
      if (pos < 0) {
          pos += lockArray.length;
      }
      return lockArray[pos];
  }
  
  @Override
  public void sync() throws IOException {
    extension.sync();
    buffer.sync();
  }
  
  @Override
  public void persist() throws IOException {
    extension.persist();
    buffer.persist();
  }
  
  @Override
  public long getLWMark() {
    return buffer.getHWMark();
  }
  
  @Override
  public long getHWMark() {
    return buffer.getHWMark();
  }
  
  @Override
  public void saveHWMark(long endOfPeriod) throws Exception {
    buffer.saveHWMark(endOfPeriod);
  }
  
  @Override
  public String getStatus() {
    return bufInts.getStatus();
  }
  
  @Override
  public File getStoreHome() {
    return buffer.getStoreHome();
  }
  
  /**
   * Gets the buffer size.
   */
  public final int getBufferSize() {
    return bufferSize;
  }
  
  /**
   * Sets the buffer size to the specified value.
   */
  public final void setBufferSize(int size) {
    this.bufferSize = Math.max(size, MINIMUM_BUFFER_SIZE);
  }
  
  /**
   * Gets the store key serializer.
   */
  public final Serializer<String> getKeySerializer() {
    return serializer;
  }
  
  @Override
  public int[] get(String key) {
    int w1 = 0, w2 = 0;
    int[] ext = null, buf = null;
    
    do {
      w1 = bufInts.getCount(key);
      if(w1 >= 0) {
        int index = getExtensionIndex(key);
        if (index >= 0) {
          ext = extInts.get(index);
        }
        buf = bufInts.get(key);
        w2 = (buf == null) ? 0 : buf.length;
      }
    } while(w1 > w2);
    
    if(buf == null) {
      return ext;
    } else if(ext == null) {
      return buf;
    } else {
      int[] result = new int[buf.length + ext.length];
      System.arraycopy(buf, 0, result, 0, buf.length);
      System.arraycopy(ext, 0, result, buf.length, ext.length);
      
      return result;
    }
  }

  @Override
  public boolean put(String key, int[] elemIds, long scn) throws Exception {
    if(elemIds == null) {
      return delete(key, scn);
    }
    
    ReentrantLock l = getLock(key);
    
    l.lock();
    try {
      bufInts.put(key, new int[0], scn);
      int index = getExtensionIndex(key);
      extInts.set(index, elemIds, scn);
      
      return true;
    } finally {
        l.unlock();
    }
  }

  @Override
  public boolean delete(String key, long scn) throws Exception {
    ReentrantLock l = getLock(key);
    
    l.lock();
    try {
      int index = getExtensionIndex(key);
      if (index >= 0) {
        extInts.set(index, null, scn);
      }
      return bufInts.delete(key, scn);
    } finally {
        l.unlock();
    }
  }

  @Override
  public void add(String key, int elemId, long scn) throws Exception {
    ReentrantLock l = getLock(key);
    
    l.lock();
    try {
      int[] buf = bufInts.get(key);
      
      if(buf == null) {
        bufInts.put(key, new int[] { elemId }, scn);
        return;
      }
      
      if(buf.length < bufferSize) {
        int[] array = new int[buf.length + 1];
        System.arraycopy(buf, 0, array, 0, buf.length);
        array[buf.length] = elemId;
        bufInts.put(key, array, scn);
      } else {
        int index = getExtensionIndex(key);
        extInts.add(index, buf, scn);
        bufInts.put(key, new int[] {elemId}, scn);
      }
    } finally {
      l.unlock();
    }
  }

  @Override
  public void remove(String key, int elemId, long scn) throws Exception {
    ReentrantLock l = getLock(key);
    
    l.lock();
    try {
      int index = getExtensionIndex(key);
      if (index >= 0) {
        bufInts.remove(key, elemId, scn);
        extInts.remove(index, elemId, scn);
      }
    } finally {
      l.unlock();
    }
  }

  @Override
  public Iterator<String> keyIterator() {
    return bufInts.keyIterator();
  }

  public void close() throws IOException {
    this.buffer.close();
    this.extension.close();
  }
}
