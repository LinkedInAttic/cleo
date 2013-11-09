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
