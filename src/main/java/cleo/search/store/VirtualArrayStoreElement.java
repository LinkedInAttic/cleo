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

import java.io.IOException;

import krati.array.Array;
import krati.util.SourceWaterMarks;

import cleo.search.Element;
import cleo.search.ElementSerializer;
import cleo.search.util.ScnFactory;

/**
 * VirtualArrayStoreElement
 * 
 * @author jwu
 * @since 05/10, 2011
 */
public class VirtualArrayStoreElement<E extends Element> implements ArrayStoreElement<E> {
  protected final String source;
  protected final ScnFactory scnFactory;
  protected final ArrayStoreElement<E> elementStore;
  protected final SourceWaterMarks sourceWaterMarks;
  protected volatile long hwMark;
  protected volatile long lwMark;
  protected boolean isClosed = false;
  
  /**
   * Creates a new instance of VirtualArrayStoreElement for a user specified source.
   * 
   * @param source       - Source where new updates originate
   * @param scnFactory   - System change number (SCN) factory
   * @param elementStore - Underlying element store
   * @param waterMarks   - Source water marks for maintaining progress from a source.
   */
  public VirtualArrayStoreElement(String source, ScnFactory scnFactory, ArrayStoreElement<E> elementStore, SourceWaterMarks waterMarks) {
    this.source = source;
    this.scnFactory = scnFactory;
    this.elementStore = elementStore;
    this.sourceWaterMarks = waterMarks;
    
    // Initialize source water marks
    this.lwMark = waterMarks.getLWMScn(source);
    this.hwMark = waterMarks.getHWMScn(source);
  }

  protected void ensureOpen() throws UnsupportedOperationException {
    if(isClosed)
      throw new UnsupportedOperationException("Cannot modify store, already closed!");
  }

  /**
   * @return the source where new updates originate.
   */
  public final String getSource() {
    return source;
  }
  
  /**
   * @return the system change number (SCN) factory.
   */
  public final ScnFactory getScnFactory() {
    return scnFactory;
  }
  
  /**
   * @return the underlying element store.
   */
  public final ArrayStoreElement<E> getElementStore() {
    return elementStore;
  }
  
  /**
   * @return the source water marks where progress from the source is maintained.
   */
  public final SourceWaterMarks getSourceWaterMarks() {
    return sourceWaterMarks;
  }
  
  @Override
  public int length() {
    return elementStore.length();
  }
  
  @Override
  public int capacity() {
    return elementStore.capacity();
  }
  
  @Override
  public int getIndexStart() {
    return elementStore.getIndexStart();
  }
  
  @Override
  public boolean hasIndex(int index) {
    return elementStore.hasIndex(index);
  }
  
  @Override
  public E getElement(int index) {
    return elementStore.getElement(index);
  }
  
  @Override
  public synchronized void deleteElement(int index, long sourceScn) throws Exception {
    ensureOpen();
    long internalScn = scnFactory.next();
    elementStore.deleteElement(index, internalScn);
    
    hwMark = Math.max(sourceScn, hwMark);
    if(elementStore.getHWMark() == elementStore.getLWMark()) {
      syncWaterMarksInternal();
    }
  }
  
  @Override
  public synchronized void setElement(int index, E element, long sourceScn) throws Exception {
    ensureOpen();
    long internalScn = scnFactory.next();
    elementStore.setElement(index, element, internalScn);
    
    hwMark = Math.max(sourceScn, hwMark);
    if(elementStore.getHWMark() == elementStore.getLWMark()) {
      syncWaterMarksInternal();
    }
  }
  
  @Override
  public long getHWMark() {
    return hwMark;
  }
  
  @Override
  public long getLWMark() {
    return lwMark;
  }
  
  @Override
  public synchronized void saveHWMark(long endOfPeriod) throws Exception {
    hwMark = endOfPeriod;
  }
  
  @Override
  public synchronized void persist() throws IOException {
    ensureOpen();
    elementStore.persist();
    syncWaterMarksInternal();
  }
  
  @Override
  public synchronized void sync() throws IOException {
    ensureOpen();
    elementStore.sync();
    syncWaterMarksInternal();
  }
  
  @Override
  public synchronized void clear() {
    ensureOpen();
    elementStore.clear();
    lwMark = hwMark = 0;
    synchronized(sourceWaterMarks) {
      sourceWaterMarks.clear();
      syncWaterMarksInternal();
    }
  }
  
  protected void syncWaterMarksInternal() {
    lwMark = hwMark;
    synchronized(sourceWaterMarks) {
      sourceWaterMarks.syncWaterMarks(source, lwMark, hwMark);
    }
  }
  
  @Override
  public byte[] getElementBytes(int index) {
    return elementStore.getElementBytes(index);
  }
  
  @Override
  public synchronized void setElementBytes(int index, byte[] elementBytes, long scn) throws Exception {
    ensureOpen();
    elementStore.setElementBytes(index, elementBytes, scn);
  }
  
  @Override
  public ElementSerializer<E> getElementSerializer() {
    return elementStore.getElementSerializer();
  }
  
  @Override
  public Array.Type getType() {
    return elementStore.getType();
  }

  @Override
  public void close() throws IOException {
    try {
      elementStore.close();
    }
    finally {
      isClosed=true;
    }
  }
}
