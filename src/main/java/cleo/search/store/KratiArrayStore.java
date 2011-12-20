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
import java.nio.channels.WritableByteChannel;

import org.apache.log4j.Logger;

import krati.array.Array;
import krati.array.DataArray;
import krati.array.DynamicArray;
import krati.core.array.SimpleDataArray;
import krati.core.array.basic.DynamicLongArray;
import krati.core.segment.AddressFormat;
import krati.core.segment.SegmentFactory;
import krati.core.segment.SegmentManager;
import krati.store.ArrayStore;

/**
 * KratiArrayStore - a dynamic krati array store
 *
 * @author jwu
 * @since 09/30, 2010
 * 
 * <p>
 * 06/12, 2011 - Added support for Closeable <br/>
 * 09/18, 2011 - Added support for partial reads <br/>
 */
public class KratiArrayStore implements DataArray, DynamicArray, ArrayStore {
  final static Logger logger = Logger.getLogger(KratiArrayStore.class);
  
  protected final File homeDir;
  protected final String homePath;
  protected final int updateBatchSize;
  protected final SimpleDataArray dataArray;
  protected final DynamicLongArray addrArray;
  
  /**
   * Constructs a Krati array store with the following default values.
   * 
   * <pre>
   *  segmentFileSizeMB    - 256MB
   *  segmentCompactFactor - 0.67
   * </pre>
   * 
   * @param length               - the array length
   * @param batchSize            - the number of updates per update batch
   * @param numSyncBatches       - the number of update batches required for updating the underlying indexes
   * @param homeDirectoryStr     - the home directory of data array
   * @param segmentFactory       - the segment factory
   * @throws Exception
   */
  public KratiArrayStore(int length,
                         int batchSize,
                         int numSyncBatches,
                         String homeDirectoryStr,
                         SegmentFactory segmentFactory) throws Exception {
    this(length,
         batchSize,
         numSyncBatches,
         new File(homeDirectoryStr),
         segmentFactory,
         256,
         0.67);
  }
  
  /**
   * Constructs a Krati array store.
   *
   * @param length               - the array length
   * @param batchSize            - the number of updates per update batch
   * @param numSyncBatches       - the number of update batches required for updating the underlying indexes
   * @param homeDirectory        - the home directory of data array
   * @param segmentFactory       - the segment factory
   * @param segmentFileSizeMB    - the segment size in MB
   * @param segmentCompactFactor - the segment load threshold, below which a segment is eligible for compaction
   * @throws Exception
   */
  public KratiArrayStore(int length,
                         int batchSize,
                         int numSyncBatches,
                         File homeDirectory,
                         SegmentFactory segmentFactory,
                         int segmentFileSizeMB,
                         double segmentCompactFactor) throws Exception {
    this.homeDir = homeDirectory;
    this.homePath = homeDirectory.getCanonicalPath();

    // Create address array
    addrArray = createAddressArray(length, batchSize, numSyncBatches, homeDirectory);

    // Create segment manager
    String segmentHome = this.homePath + File.separator + "segs";
    SegmentManager segmentManager = SegmentManager.getInstance(segmentHome,
                                                               segmentFactory,
                                                               segmentFileSizeMB);

    // Create data array
    this.dataArray = new SimpleDataArray(addrArray, segmentManager, segmentCompactFactor);
    this.updateBatchSize = batchSize;
    
    // Logging
    logger.info(getStatus());
  }

  protected DynamicLongArray createAddressArray(int length,
                                                int batchSize,
                                                int numSyncBatches,
                                                File homeDirectory) throws Exception {
    DynamicLongArray addrArray;
    addrArray = new DynamicLongArray(batchSize, numSyncBatches, homeDirectory);
    addrArray.expandCapacity(length - 1);
    
    if (length != addrArray.length()) {
      logger.warn("address array: length " + addrArray.length() + " is different from specified " + length);
    }
    logger.info("address array: length=" + addrArray.length() + " home=" + homeDirectory.getAbsolutePath());
    
    return addrArray;
  }

  public File getStoreHome() {
    return homeDir;
  }

  public String getStoreHomePath() {
    return homePath;
  }

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
  
  @Override
  public int length() {
    return dataArray.length();
  }
  
  @Override
  public boolean hasIndex(int index) {
    return dataArray.hasIndex(index);
  }
  
  @Override
  public byte[] get(int index) {
    return dataArray.get(index);
  }

  @Override
  public int get(int index, byte[] dst) {
    return dataArray.get(index, dst);
  }

  @Override
  public int get(int index, byte[] dst, int offset) {
    return dataArray.get(index, dst, offset);
  }
  
  public int read(int index, byte[] dst) {
    return dataArray.read(index, dst);
  }
  
  public int read(int index, int offset, byte[] dst) {
    return dataArray.read(index, offset, dst);
  }

  @Override
  public final int getLength(int index) {
    return dataArray.getLength(index);
  }

  @Override
  public final boolean hasData(int index) {
    try {
      return dataArray.hasData(index);
    } catch(ArrayIndexOutOfBoundsException e) {
      return false;
    }
  }
  
  @Override
  public int transferTo(int index, WritableByteChannel channel) {
    return dataArray.transferTo(index, channel);
  }
  
  @Override
  public synchronized void set(int index, byte[] data, long scn) throws Exception {
    expandCapacity(index);
    dataArray.set(index, data, scn);
  }
  
  @Override
  public synchronized void set(int index, byte[] data, int offset, int length, long scn) throws Exception {
    expandCapacity(index);
    dataArray.set(index, data, offset, length, scn);
  }
  
  @Override
  public synchronized void delete(int index, long scn) throws Exception {
    set(index, null, scn);
  }
  
  @Override
  public synchronized void clear() {
    dataArray.clear();
  }
  
  @Override
  public long getHWMark() {
    return dataArray.getHWMark();
  }
  
  @Override
  public long getLWMark() {
    return dataArray.getLWMark();
  }
  
  @Override
  public synchronized void saveHWMark(long endOfPeriod) throws Exception {
    dataArray.saveHWMark(endOfPeriod);
  }
  
  @Override
  public synchronized void persist() throws IOException {
    dataArray.persist();
  }
  
  @Override
  public synchronized void sync() throws IOException {
    dataArray.sync();
  }
  
  @Override
  public synchronized void expandCapacity(int index) throws Exception {
    addrArray.expandCapacity(index);
  }
  
  public final int getUpdateBatchSize() {
    return updateBatchSize;
  }
  
  private final AddressFormat _addrFormat = new AddressFormat();
  
  public final String address(int index) {
    if(hasIndex(index)) {
      long addr = addrArray.get(index);
      long segment = _addrFormat.getSegment(addr);
      long offset = _addrFormat.getOffset(addr);
      long size = _addrFormat.getDataSize(addr);
      
      return String.format("Address=%d [size=%d segment=%d offset=%d]", addr, size, segment, offset);
    } else {
      return "address=n/a";
    }
  }
  
  @Override
  public final int getIndexStart() {
    return 0;
  }
  
  @Override
  public final int capacity() {
    return dataArray.length();
  }
  
  @Override
  public boolean isOpen() {
    return dataArray.isOpen();
  }
  
  @Override
  public synchronized void open() throws IOException {
    dataArray.open();
  }
  
  @Override
  public synchronized void close() throws IOException {
    dataArray.close();
  }
  
  @Override
  public Array.Type getType() {
    return Array.Type.DYNAMIC;
  }
}
