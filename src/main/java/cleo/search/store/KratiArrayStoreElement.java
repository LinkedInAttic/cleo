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

import cleo.search.Element;
import cleo.search.ElementSerializer;

import krati.array.Array;
import krati.store.ArrayStore;
import krati.store.ArrayStorePartition;
import java.lang.UnsupportedOperationException;

/**
 * KratiArrayStoreElement
 * 
 * @author jwu
 * @since 01/19, 2011
 * 
 * <p>
 * 05/27, 2011 - Added methods getElmentBytes/setElementBytes <br/>
 */
public final class KratiArrayStoreElement<E extends Element> implements ArrayStoreElement<E> {
  private final int indexStart;
  private final int indexEnd;
  private final ArrayStore baseStore;
  private final ElementSerializer<E> elementSerializer;
  private boolean isClosed=false;
  
  public KratiArrayStoreElement(ArrayStore baseStore, ElementSerializer<E> elementSerializer) {
    this.baseStore = baseStore;
    this.elementSerializer = elementSerializer;
    this.indexStart = (baseStore instanceof ArrayStorePartition) ?
                      ((ArrayStorePartition)baseStore).getIdStart() : 0;
    this.indexEnd = getIndexStart() + baseStore.capacity();
  }

  protected void ensureOpen() throws UnsupportedOperationException {
    if(isClosed)
      throw new UnsupportedOperationException("Cannot modify store, already closed!");
  }
  
  public ArrayStore getBaseStore() {
    return baseStore;
  }
  
  @Override
  public ElementSerializer<E> getElementSerializer() {
    return elementSerializer;
  }
  
  @Override
  public E getElement(int index) {
    byte[] dat = baseStore.get(index);
    return (dat == null) ? null : elementSerializer.deserialize(dat);
  }
  
  @Override
  public synchronized void setElement(int index, E element, long scn) throws Exception {
    ensureOpen();
    byte[] dat = null;
    if(element != null) {
      dat = elementSerializer.serialize(element);
    }
    baseStore.set(index, dat, scn);
  }
  
  @Override
  public synchronized void deleteElement(int index, long scn) throws Exception {
    ensureOpen();
    baseStore.delete(index, scn);
  }
  
  @Override
  public long getLWMark() {
    return baseStore.getLWMark();
  }
  
  @Override
  public long getHWMark() {
    return baseStore.getHWMark();
  }
  
  @Override
  public synchronized void saveHWMark(long endOfPeriod) throws Exception {
    ensureOpen();
    baseStore.saveHWMark(endOfPeriod);
  }
  
  @Override
  public synchronized void persist() throws IOException {
    ensureOpen();
    baseStore.persist();
  }
  
  @Override
  public synchronized void sync() throws IOException {
    ensureOpen();
    baseStore.sync();
  }
  
  @Override
  public synchronized void clear() {
    ensureOpen();
    baseStore.clear();
  }
  
  @Override
  public boolean hasIndex(int index) {
    return (indexStart <= index && index < indexEnd) ? true : false;
  }
  
  @Override
  public int length() {
    return baseStore.length();
  }

  @Override
  public int capacity() {
    return baseStore.capacity();
  }

  @Override
  public int getIndexStart() {
    return indexStart;
  }
  
  @Override
  public byte[] getElementBytes(int index) {
    return baseStore.get(index);
  }
  
  @Override
  public synchronized void setElementBytes(int index, byte[] elementBytes, long scn)  throws Exception {
    ensureOpen();
    baseStore.set(index, elementBytes, scn);
  }

  @Override
  public Array.Type getType() {
    return baseStore.getType();
  }

  @Override
  public void close() throws IOException {
    try {
      baseStore.close();
    }
    finally {
      isClosed=true;
    }
  }
}
