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
import java.util.ArrayList;

import krati.array.Array;

import org.apache.log4j.Logger;

import cleo.search.Element;
import cleo.search.ElementSerializer;

/**
 * MemoryArrayStoreElement
 * 
 * @author jwu
 * @since 01/20, 2011
 * 
 * <p>
 * 05/27, 2011 - Added methods getElmentBytes/setElementBytes <br/>
 */
public class MemoryArrayStoreElement<E extends Element> implements ArrayStoreElement<E> {
  private final int indexStart;
  private final ArrayList<E> elementList;
  private final ArrayStoreElement<E> elementStore;
  private final int loadParallelism;
  
  private final static Logger logger = Logger.getLogger(MemoryArrayStoreElement.class);
  
  public MemoryArrayStoreElement(ArrayStoreElement<E> elementStore) {
    this(elementStore, 10);
  }
  
  public MemoryArrayStoreElement(ArrayStoreElement<E> elementStore, int loadParallelism) {
    this.elementStore = elementStore;
    this.loadParallelism = Math.max(1, loadParallelism);
    this.elementList = new ArrayList<E>(elementStore.capacity());
    this.indexStart = elementStore.getIndexStart();
    
    this.init();
  }
  
  protected void init() {
    final int cnt = elementStore.capacity();
    boolean succ = true;
    
    // Initialize elements to null
    for(int i = 0; i < cnt; i++) {
      elementList.add(null);
    }
    
    // Start to load elements in parallel
    ArrayList<ParallelLoader> loaders = new ArrayList<ParallelLoader>(loadParallelism);
    for(int i = 0; i < loadParallelism; i++) {
      ParallelLoader l = new ParallelLoader(loadParallelism, i);
      loaders.add(l);
      l.start();
    }
    
    // Wait for all parallel loaders to finish
    for(ParallelLoader l : loaders) {
      try {
        l.join();
        logger.info(l + " success");
      } catch(Exception e) {
        logger.warn(l + " failure", e);
        succ = false;
      }
    }
    
    // Load elements sequentially upon failure
    if(!succ) {
      E element = null;
      
      for(int i = 0; i < cnt; i++) {
        if(elementList.get(i) != null) {
          continue;
        }
        
        int index = indexStart + i;
        try {
          element = elementStore.getElement(index);
        } catch(Exception e) {
          element = null;
          logger.warn("Failed to load element " + index, e);
        }
        
        elementList.set(i, element);
      }
    }
  }
  
  class ParallelLoader extends Thread {
    final int modBase;
    final int modRank;
    
    public ParallelLoader(int modBase, int modRank) {
      this.modBase = modBase;
      this.modRank = modRank;
    }
    
    @Override
    public void run() {
      E element = null;
      for(int i = modRank, cnt = elementStore.capacity(); i < cnt; i += modBase) {
        int index = indexStart + i;
        try {
          element = elementStore.getElement(index);
        } catch(Exception e) {
          element = null;
          logger.warn("Failed to load element " + index, e);
        }
        
        elementList.set(i, element);
      }
    }
    
    @Override
    public final String toString() {
      return getClass().getSimpleName() + "-" + modBase + "-" + modRank;
    }
  }
  
  private void setElementInternal(int index, E element) throws Exception {
    // Expand internal array list if index is over the current array list size
    for(int i = 0, cnt = index - indexStart - elementList.size(); i <= cnt; i++) {
      elementList.add(null);
    }
    elementList.set(index - indexStart, element);
  }
  
  @Override
  public E getElement(int index) {
    int i = index - indexStart;
    return (i < elementList.size()) ? elementList.get(i) : null;
  }
  
  @Override
  public synchronized void setElement(int index, E element, long scn) throws Exception {
    elementStore.setElement(index, element, scn);
    setElementInternal(index, element);
  }
  
  @Override
  public synchronized void deleteElement(int index, long scn) throws Exception {
    elementStore.deleteElement(index, scn);
    elementList.set(index - indexStart, null);
  }
  
  @Override
  public long getLWMark() {
    return elementStore.getLWMark();
  }
  
  @Override
  public long getHWMark() {
    return elementStore.getHWMark();
  }
  
  @Override
  public synchronized void saveHWMark(long endOfPeriod) throws Exception {
    elementStore.saveHWMark(endOfPeriod);
  }
  
  @Override
  public synchronized void persist() throws IOException {
    elementStore.persist();
  }
  
  @Override
  public synchronized void sync() throws IOException {
    elementStore.sync();
  }
  
  @Override
  public synchronized void clear() {
    elementStore.clear();
    elementList.clear();
  }
  
  @Override
  public boolean hasIndex(int index) {
    return elementStore.hasIndex(index);
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
    return indexStart;
  }
  
  @Override
  public byte[] getElementBytes(int index) {
    return elementStore.getElementBytes(index);
  }
  
  @Override
  public synchronized void setElementBytes(int index, byte[] elementBytes, long scn) throws Exception {
    E element = null;
    if(elementBytes != null) {
      element = getElementSerializer().deserialize(elementBytes);
    }
    
    elementStore.setElementBytes(index, elementBytes, scn);
    setElementInternal(index, element);
  }
  
  @Override
  public ElementSerializer<E> getElementSerializer() {
    return elementStore.getElementSerializer();
  }
  
  @Override
  public Array.Type getType() {
    return elementStore.getType();
  }
}
