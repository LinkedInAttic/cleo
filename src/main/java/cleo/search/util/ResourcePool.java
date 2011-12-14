package cleo.search.util;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * ResourcePool
 * 
 * @author jwu
 * @since 04/30, 2011
 */
public class ResourcePool<T> {
  private int capacity;
  private final ConcurrentLinkedQueue<T> internalQueue;
  
  public ResourcePool(int capacity) {
    this.internalQueue = new ConcurrentLinkedQueue<T>();
    this.setCapacity(capacity);
  }
  
  public final int getCapacity() {
    return capacity;
  }
  
  public final void setCapacity(int capacity) {
    this.capacity =  Math.max(10, capacity);
  }
  
  public boolean put(T resource) {
    if(resource == null) {
      return false;
    }
    
    if(internalQueue.size() < capacity) {
      return internalQueue.offer(resource);
    }
    
    return false;
  }
  
  public T get() {
    return internalQueue.poll();
  }
}
