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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;

import krati.Persistable;
import krati.core.segment.SegmentFactory;
import krati.store.IndexedDataStore;

/**
 * KratiDataStore
 * 
 * @author jwu
 * @since 02/01, 2011
 */
public class KratiDataStore implements Persistable {
  private final static Logger logger = Logger.getLogger(KratiDataStore.class);
  private final IndexedDataStore indexedStore;
  private final File waterMarksFileOriginal;
  private final File waterMarksFile;
  private final File storeHomeDir;
  private final int updateBatchSize;
  private volatile long lwMark = 0; 
  private volatile long hwMark = 0; 
  private volatile long counter= 0;
  
  public KratiDataStore(File storeDir,
                        int initialCapacity,
                        int batchSize, int numSyncBatches,
                        SegmentFactory indexSegmentFactory,
                        SegmentFactory storeSegmentFactory) throws Exception {
    this(storeDir,
         initialCapacity,
         batchSize,
         numSyncBatches,
         8, indexSegmentFactory,
         32, storeSegmentFactory);
  }
  
  public KratiDataStore(File storeDir,
                        int initialCapacity,
                        int updateBatchSize, int numSyncBatches,
                        int indexSegmentFileSizeMB, SegmentFactory indexSegmentFactory,
                        int storeSegmentFileSizeMB, SegmentFactory storeSegmentFactory) throws Exception {
    int indexInitLevel = getInitLevel(initialCapacity);
    int storeInitLevel = Math.max(1, indexInitLevel/2);
    this.updateBatchSize = updateBatchSize;
    this.storeHomeDir = storeDir;
    this.indexedStore = new IndexedDataStore(
        storeDir,
        updateBatchSize,
        numSyncBatches,
        indexInitLevel,
        indexSegmentFileSizeMB,
        indexSegmentFactory,
        storeInitLevel,
        storeSegmentFileSizeMB,
        storeSegmentFactory);
    
    this.waterMarksFile = new File(storeDir, "waterMarks");
    this.waterMarksFileOriginal = new File(storeDir, "waterMarks.original");
    this.initWaterMarks();
  }
  
  protected int getInitLevel(int initialCapacity) {
    int initLevel = 1;
    int numUnits = initialCapacity >> 16;
    if(numUnits > 0) {
      initLevel = (int)Math.ceil(Math.log(numUnits) / Math.log(2));
    }
    
    return Math.max(1, initLevel);
  }
  
  protected void initWaterMarks() throws IOException {
    if(waterMarksFile != null && waterMarksFile.exists()) {
      try {
        loadWaterMarks(waterMarksFile);
      } catch(IOException e) {
        if(waterMarksFileOriginal != null && waterMarksFileOriginal.exists()) {
          loadWaterMarks(waterMarksFileOriginal);
        }
      }
    }
    
    logger.info("init water marks: lwMark=" + lwMark + " hwMark=" + hwMark);
  }
  
  protected void loadWaterMarks(File waterMarksFile) throws IOException {
    Properties p = new Properties();
    FileInputStream fis = new FileInputStream(waterMarksFile);
    
    try {
      p.load(fis);
      Enumeration<?> enm = p.propertyNames(); 
      while(enm.hasMoreElements()) {
        String key = (String)enm.nextElement();
        String value = p.getProperty(key);
        
        if(key.equalsIgnoreCase("lwMark")) {
          lwMark = Long.parseLong(value);
        } else if(key.equalsIgnoreCase("hwMark")) {
          hwMark = Long.parseLong(value);
        }
      }
      
      logger.info("Loaded water marks from " + waterMarksFile.getCanonicalPath() + ": lwMark=" + lwMark + " hwMark=" + hwMark);
    } catch(IOException e) {
      logger.error("Failed to load water marks from " + waterMarksFile.getName(), e);
      throw e;
    } finally {
      fis.close();
      fis = null;
    }
  }
  
  protected void syncWaterMarks() {
    if(lwMark < hwMark) {
      lwMark = hwMark;
      
      // Save water marks into file
      PrintWriter out = null;
      try  {
        // Backup the original file
        if(waterMarksFile.exists()) {
          if(waterMarksFileOriginal.exists()) {
            waterMarksFileOriginal.delete();
          }
          waterMarksFile.renameTo(waterMarksFileOriginal);
        }
        
        // Overwrite the existing file
        out = new PrintWriter(new FileOutputStream(waterMarksFile)); 
        out.println("lwMark=" + lwMark); 
        out.println("hwMark=" + hwMark);
        out.flush();
      } catch(IOException ioe) {
        logger.error("Failed to sync water marks", ioe);
      } finally {
        if(out != null) {
          out.close();
          out = null;
        }
      }
    }
  }
  
  protected void internalPersist(long scn) throws Exception {
    counter++;
    hwMark = Math.max(hwMark, scn);
    if(counter % updateBatchSize == 0) {
      indexedStore.persist();
      syncWaterMarks();
    }
  }
  
  public String getStatus() {
    StringBuilder sb = new StringBuilder();
    sb.append("path=").append(storeHomeDir.getAbsolutePath());
    sb.append(" lwm=").append(lwMark);
    sb.append(" hwm=").append(hwMark);
    return sb.toString();
  }
  
  public final File getStoreHome() {
    return storeHomeDir;
  }
  
  public final int getUpdateBatchSize() {
    return updateBatchSize;
  }
  
  public final IndexedDataStore getUnderlyingStore() {
    return indexedStore;
  }
  
  public byte[] get(byte[] key) {
    return indexedStore.get(key);
  }
  
  public synchronized boolean put(byte[] key, byte[] value, long scn) throws Exception {
    boolean b = indexedStore.put(key, value);
    internalPersist(scn);
    return b;
  }
  
  public synchronized boolean delete(byte[] key, long scn) throws Exception {
    boolean b = indexedStore.delete(key);
    internalPersist(scn);
    return b;
  }
  
  public synchronized void clear() throws IOException {
    indexedStore.clear();
    syncWaterMarks();
  }
  
  public synchronized void sync() throws IOException {
    indexedStore.sync();
    syncWaterMarks();
  }
  
  public synchronized void persist() throws IOException {
    indexedStore.persist();
    syncWaterMarks();
  }
  
  @Override
  public synchronized void saveHWMark(long endOfPeriod) throws Exception {
    hwMark = endOfPeriod;
    syncWaterMarks();
  }
  
  @Override
  public long getHWMark() {
    return hwMark;
  }
  
  @Override
  public long getLWMark() {
    return lwMark;
  }
  
  public Iterator<byte[]> keyIterator() {
    return indexedStore.keyIterator();
  }
}
