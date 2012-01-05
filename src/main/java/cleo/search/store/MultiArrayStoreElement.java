package cleo.search.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import cleo.search.Element;
import cleo.search.ElementSerializer;

/**
 * MultiArrayStoreElement is a simple container of more than one subsidiary {@link ArrayStoreElement}.
 * 
 * @author jwu
 * @since 01/05, 2012
 */
public final class MultiArrayStoreElement<E extends Element> implements ArrayStoreElement<E> {
  private final static Logger logger = Logger.getLogger(MultiArrayStoreElement.class);
  private final List<ArrayStoreElement<E>> storeList;
  
  /**
   * Creates a new instance of MultiArrayStoreElement.
   * 
   * @param subStores - the list of subsidiary stores 
   */
  public MultiArrayStoreElement(List<ArrayStoreElement<E>> subStores) {
    storeList = new ArrayList<ArrayStoreElement<E>>();
    if(subStores != null) {
      storeList.addAll(subStores);
    }
    
    Collections.sort(storeList, new Comparator<ArrayStoreElement<E>>() {
      @Override
      public int compare(ArrayStoreElement<E> s1, ArrayStoreElement<E> s2) {
        return s1.getIndexStart() - s2.getIndexStart();
      }
    });
  }
  
  @Override
  public Type getType() {
    return (storeList.size() > 0) ? storeList.get(0).getType() : null;
  }
  
  @Override
  public int length() {
    int length = 0;
    
    if(storeList.size() > 0) {
      ArrayStoreElement<E> store;
      
      store = storeList.get(0); 
      int indexStart = store.getIndexStart();
      
      store = storeList.get(storeList.size() - 1);
      int indexEnd = store.getIndexStart() + store.capacity();
      
      length = indexEnd - indexStart;
    }
    
    return length;
  }
  
  @Override
  public int capacity() {
    int capacity = 0;
    
    for(ArrayStoreElement<E> store : storeList) {
      capacity += store.capacity();
    }
    
    return capacity;
  }
  
  @Override
  public boolean hasIndex(int index) {
    for(ArrayStoreElement<E> store : storeList) {
      if(store.hasIndex(index)) {
        return true;
      }
    }
    
    return false;
  }
  
  @Override
  public int getIndexStart() {
    return (storeList.size() > 0) ? storeList.get(0).getIndexStart() : 0;
  }
  
  @Override
  public ElementSerializer<E> getElementSerializer() {
    return (storeList.size() > 0) ? storeList.get(0).getElementSerializer() : null;
  }
    
  @Override
  public E getElement(int index) {
    for(ArrayStoreElement<E> store : storeList) {
      if(store.hasIndex(index)) {
        return store.getElement(index);
      }
    }
    
    return null;
  }
  
  @Override
  public byte[] getElementBytes(int index) {
    for(ArrayStoreElement<E> store : storeList) {
      if(store.hasIndex(index)) {
        return store.getElementBytes(index);
      }
    }
    
    return null;
  }
  
  @Override
  public void setElement(int index, E element, long scn) throws Exception {
    for(ArrayStoreElement<E> store : storeList) {
      if(store.hasIndex(index)) {
        store.setElement(index, element, scn);
        return;
      }
    }
  }
  
  @Override
  public void setElementBytes(int index, byte[] elementBytes, long scn) throws Exception {
    for(ArrayStoreElement<E> store : storeList) {
      if(store.hasIndex(index)) {
        store.setElementBytes(index, elementBytes, scn);
        return;
      }
    }
  }
  
  @Override
  public void deleteElement(int index, long scn) throws Exception {
    for(ArrayStoreElement<E> store : storeList) {
      if(store.hasIndex(index)) {
        store.deleteElement(index, scn);
        return;
      }
    }
  }
  
  /**
   * @return the lowest water mark of all the subsidiary stores.
   */
  @Override
  public long getLWMark() {
    long lwm = 0;
    for(ArrayStoreElement<E> store : storeList) {
      lwm = (lwm == 0) ? store.getLWMark() : Math.min(lwm, store.getLWMark());
    }
    
    return lwm;
  }
  
  /**
   * @return the highest water mark of all the subsidiary stores.
   */
  @Override
  public long getHWMark() {
    long hwm = 0;
    for(ArrayStoreElement<E> store : storeList) {
      hwm = Math.max(hwm, store.getHWMark());
    }
    
    return hwm;
  }
  
  /**
   * Saves the high water mark of each subsidiary store.
   * 
   * @param endOfPeriod
   */
  @Override
  public synchronized void saveHWMark(long endOfPeriod) {
    for(ArrayStoreElement<E> store : storeList) {
      try {
        store.saveHWMark(endOfPeriod);
      } catch (Exception e) {
        int indexStart = store.getIndexStart();
        int indexEnd = indexStart + store.capacity();
        logger.warn("failed to saveHWMark " + getHWMark() + " for [" + indexStart + "," + indexEnd + ")");
      }
    }
  }
  
  @Override
  public synchronized void persist() throws IOException {
    saveHWMark(getHWMark());
    
    // Persist each store sequentially
    for(ArrayStoreElement<E> store : storeList) {
      store.persist();
    }
  }
  
  @Override
  public synchronized void sync() throws IOException {
    saveHWMark(getHWMark());
    
    // Sync each store sequentially
    for(ArrayStoreElement<E> store : storeList) {
      store.sync();
    }
  }
  
  @Override
  public synchronized void clear() {
    saveHWMark(getHWMark());
    
    // Clear each store sequentially
    for(ArrayStoreElement<E> store : storeList) {
      store.clear();
    }
  }
}
