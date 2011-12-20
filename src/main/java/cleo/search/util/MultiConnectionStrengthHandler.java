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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * MultiConnectionStrengthHandler
 * 
 * @author jwu
 * @since 06/13, 2011
 */
public class MultiConnectionStrengthHandler implements ConnectionStrengthHandler {
  private final List<ConnectionStrengthHandler> handlerList = new ArrayList<ConnectionStrengthHandler>();
  
  public boolean add(ConnectionStrengthHandler h) {
    if(h != null && h != this) {
      handlerList.add(h);
      return true;
    }
    
    return false;
  }
  
  public int size() {
    return handlerList.size();
  }
  
  public void clear() {
    handlerList.clear();
  }
  
  public boolean isEmpty() {
    return handlerList.isEmpty();
  }
  
  public Iterator<ConnectionStrengthHandler> iterator() {
    return handlerList.iterator();
  }
  
  @Override
  public void handle(int source, int[] connections, int[] strengths) throws Exception {
    for(ConnectionStrengthHandler h : handlerList) {
      h.handle(source, connections, strengths);
    }
  }
}
