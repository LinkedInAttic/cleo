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

import java.io.Serializable;
import java.util.List;

import cleo.search.Element;
import cleo.search.Hit;
import cleo.search.network.Proximity;

/**
 * Collector
 * 
 * @author jwu
 * @since 02/06, 2011
 * 
 * @param <E> Element
 */
public interface Collector<E extends Element> extends Serializable {
  
  /**
   * The collector can stop collecting elements.
   * 
   * @return <code>true</code> if the collector can stop collecting elements.
   * Otherwise, <code>false</code>.
   */
  public boolean canStop();
  
  /**
   * @return <code>true</code> if the collector does not have any elements.
   * Otherwise, <code>false</code>.
   */
  public boolean isEmpty();
  
  /**
   * Adds an element to this Collector.
   * 
   * @param element - element
   * @param score   - element score
   * @param source  - element source
   * 
   * @return <code>true</code> if the collector changed as a result of the operation.
   * Otherwise, <code>false</code>.
   */
  public boolean add(E element, double score, String source);
  
  /**
   * Adds an element to this Collector.
   * 
   * @param element   - element
   * @param score     - element score
   * @param source    - element source
   * @param proximity - network proximity
   * 
   * @return <code>true</code> if the collector changed as a result of the operation.
   * Otherwise, <code>false</code>.
   */
  public boolean add(E element, double score, String source, Proximity proximity);
  
  /**
   * Adds an element to this Collector.
   * 
   * @param hit
   * 
   * @return <code>true</code> if the collector changed as a result of the operation.
   * Otherwise, <code>false</code>.
   */
  public boolean add(Hit<E> hit);
  
  /**
   * Adds the elements from another collector.
   * 
   * @param collector - collector to add
   * 
   * @return <code>true</code> if the collector changed as a result of the operation.
   * Otherwise, <code>false</code>.
   */
  public boolean add(Collector<E> collector);
  
  /**
   * @return a list view of element hits currently contained by this collector.
   */
  public List<Hit<E>> hits();
  
  /**
   * @return a list view of elements currently contained by this Collector.
   */
  public List<E> elements();
  
  /**
   * @return the number of elements currently contained by this Collector.
   */
  public int size();
  
  /**
   * @return the capacity of this Collector.
   */
  public int capacity();
  
  /**
   * @return the stop size of this Collector.
   */
  public int stopSize();
  
  /**
   * Clear all elements contained by this Collector.
   */
  public void clear();
  
  /**
   * @return a new collector of the same type.
   */
  public Collector<E> newInstance();
}
