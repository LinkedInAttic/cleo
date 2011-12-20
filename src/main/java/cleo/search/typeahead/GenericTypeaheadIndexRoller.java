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

package cleo.search.typeahead;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import krati.util.Chronos;

import org.apache.log4j.Logger;

import cleo.search.Element;
import cleo.search.IndexRoller;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.ConnectionsStore;

/**
 * GenericTypeaheadRoller - GenericTypeahead rolling indexer.
 * 
 * @author jwu
 * @since 03/02, 2011
 */
public class GenericTypeaheadIndexRoller<E extends Element> implements IndexRoller<E> {
  private final static Logger logger = Logger.getLogger(GenericTypeahead.class);
  private final GenericTypeahead<E> ta;
  
  public GenericTypeaheadIndexRoller(GenericTypeahead<E> ta) {
    this.ta = ta;
  }
  
  @Override
  public synchronized boolean roll(Collection<E> elements) {
    if(elements == null || elements.size() == 0) {
      return false;
    }
    
    Chronos c = new Chronos();
    HashMap<String, List<E>> map = new HashMap<String, List<E>>();
    
    try {
      long maxScn = collect(map, elements);
      store(map, maxScn);
      logger.info(ta.getName() + " applied rolling update in " + c.tick() + " ms");
      return true;
    } catch (Exception e) {
      logger.error(ta.getName() + " failed to update indexes", e);
    } finally {
      map.clear();
      map = null;
    }
    
    return false;
  }
  
  protected long collect(HashMap<String, List<E>> map, Collection<E> collection) {
    int maxKeyLength = ta.getMaxKeyLength();
    long maxScn = 0;
    
    for(E element : collection) {
      if(element != null) {
        for(String term : element.getTerms()) {
          if(term == null) continue;
          
          int len = Math.min(term.length(), maxKeyLength);
          for(int k = 1; k <= len; k++) {
            String source = term.substring(0, k);
            List<E> list = map.get(source);
            if(list == null) {
              list = new ArrayList<E>(500);
              map.put(source, list);
            }
            list.add(element);
          }
        }
        
        maxScn = Math.max(element.getTimestamp(), maxScn);
      }
    }
    
    return maxScn;
  }
  
  protected void store(HashMap<String, List<E>> map, long maxScn) throws Exception {
    HashSet<E> uniqSet = new HashSet<E>();
    ElementScoreCmpDsc scoreCmpDsc = new ElementScoreCmpDsc();
    ArrayStoreElement<E> elementStore = ta.getElementStore();
    ConnectionsStore<String> connectionsStore = ta.getConnectionsStore();

    List<E> list = new ArrayList<E>(100000);
    
    for(String key : map.keySet()) {
      List<E> rollingList = map.get(key);
      if(rollingList == null || rollingList.size() == 0) {
        continue;
      }
      
      list.clear();
      uniqSet.clear();
      
      // Add elements from original connections
      int[] connections = connectionsStore.getConnections(key);
      if(connections != null) {
        for(int i = 0, cnt = connections.length; i < cnt; i++) {
          E element = elementStore.getElement(connections[i]);
          if(element != null) {
            list.add(element);
            uniqSet.add(element);
          }
        }
      }
      
      // Add elements from rolling update
      for(E element : rollingList) {
        if(element != null && !uniqSet.contains(element)) {
          list.add(element);
          uniqSet.add(element);
        }
      }
      
      // Sort elements using score in descending order
      Collections.sort(list, scoreCmpDsc);
      connections = new int[list.size()];
      for(int i = 0, cnt = connections.length; i < cnt; i++) {
        connections[i] = list.get(i).getElementId();
      }
      
      // Update source connections
      connectionsStore.putConnections(key, connections, maxScn);
    }
    
    // Sync all changes to disk
    connectionsStore.sync();
  }
  
  public static final class ElementScoreCmpDsc implements Comparator<Element> {
    @Override
    public int compare(Element e0, Element e1) {
      float score0 = e0.getScore();
      float score1 = e1.getScore();
      
      // Descending order
      return score0 < score1 ? 1 : (score0 == score1 ? (e0.getElementId() - e1.getElementId()) : -1);
    }
  }
}
