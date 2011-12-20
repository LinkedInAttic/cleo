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

package cleo.search.collector;

import java.util.ArrayList;
import java.util.List;

import cleo.search.Element;
import cleo.search.Hit;
import cleo.search.ElementHit;
import cleo.search.network.Proximity;

/**
 * SimpleCollector
 * 
 * @author jwu
 * @since 02/06, 2011
 * 
 * @param <E> Element to collect.
 */
public final class SimpleCollector<E extends Element> implements Collector<E> {
  private static final long serialVersionUID = 1L;
  
  private final int capacity;
  private final List<Hit<E>> hits;
  
  /**
   * Creates a new collector with the capacity up to the maximum integer value.
   */
  public SimpleCollector() {
    this.capacity = Integer.MAX_VALUE;
    this.hits = new ArrayList<Hit<E>>(100);
  }
  
  /**
   * Creates a new collector with a specified capacity (e.g. 10).
   * 
   * @param capacity - the capacity of collector, no smaller than 1.
   */
  public SimpleCollector(int capacity) {
    this.capacity = Math.max(1, capacity);
    this.hits = new ArrayList<Hit<E>>(100);
  }
  
  @Override
  public boolean add(E element, double score, String source) {
    if(hits.size() < capacity) {
      return hits.add(new ElementHit<E>(element, score, source));
    } else {
      return false; 
    }
  }
  
  @Override
  public boolean add(E element, double score, String source, Proximity proximity) {
    if(hits.size() < capacity) {
      return hits.add(new ElementHit<E>(element, score, source, proximity));
    } else {
      return false; 
    }
  }
  
  @Override
  public boolean add(Hit<E> hit) {
    if(hits.size() < capacity) {
      return hits.add(hit);
    } else {
      return false; 
    }
  }
  
  @Override
  public boolean add(Collector<E> collector) {
    if(hits.size() >= capacity) {
      return false;
    }
    
    List<Hit<E>> list = collector.hits();
    int cnt = Math.min(capacity - hits.size(), list.size());
    for(int i = 0; i < cnt; i++) hits.add(list.get(i)); 
    return true;
  }
  
  @Override
  public List<Hit<E>> hits() {
    return hits;
  }
  
  @Override
  public List<E> elements() {
    List<E> elemList = new ArrayList<E>(hits.size());
    for(Hit<E> s : hits) {
      elemList.add(s.getElement());
    }
    return elemList;
  }
  
  @Override
  public boolean isEmpty() {
    return hits.isEmpty();
  }
  
  @Override
  public boolean canStop() {
    return hits.size() >= capacity;
  }
  
  @Override
  public int size() {
    return hits.size();
  }
  
  @Override
  public int capacity() {
    return capacity;
  }
  
  @Override
  public int stopSize() {
    return capacity;
  }
  
  @Override
  public void clear() {
    hits.clear();
  }
  
  @Override
  public Collector<E> newInstance() {
    return new SimpleCollector<E>(capacity);
  }
}
