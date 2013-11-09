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

package cleo.search;

import java.util.List;

import cleo.search.collector.Collector;

/**
 * Searcher
 * 
 * @author jwu
 * @since 01/12, 2011
 * 
 * @param <E>
 */
public interface Searcher<E extends Element> {
  
  /**
   * Search for elements matching given search terms without timeout.
   * 
   * @param uid           - Searcher Id
   * @param terms         - Search terms
   * @return a list of elements matching given search terms.
   */
  public List<E> search(int uid, String[] terms);
  
  /**
   * Search for elements matching given search terms within a user-specified timeout.
   * 
   * @param uid           - Searcher Id
   * @param terms         - Search terms
   * @param timeoutMillis - Timeout in milliseconds
   * @return a list of elements matching given search terms.
   */
  public List<E> search(int uid, String[] terms, long timeoutMillis);
  
  /**
   * Search for elements matching given search terms within a user-specified timeout.
   * 
   * @param uid           - Searcher Id
   * @param terms         - Search terms
   * @param maxNumResults - Max number of results
   * @param timeoutMillis - Timeout in milliseconds
   * @return a list of elements matching given search terms up to a user-specified number of results.
   */
  public List<E> search(int uid, String[] terms, int maxNumResults, long timeoutMillis);
  
  /**
   * Search for elements matching given search terms using a collector.
   * 
   * @param uid           - Searcher Id
   * @param terms         - Search terms
   * @param collector     - Element collector
   * @return the element collector which may be the original collector or a newly created collector. 
   */
  public Collector<E> search(int uid, String[] terms, Collector<E> collector);
  
  /**
   * Search for elements matching given search terms using a collector within a user-specified timeout.
   * 
   * @param uid           - Searcher Id
   * @param terms         - Search terms
   * @param collector     - Element collector
   * @param timeoutMillis - Timeout in milliseconds
   * @return the element collector which may be the original collector or a newly created collector. 
   */
  public Collector<E> search(int uid, String[] terms, Collector<E> collector, long timeoutMillis);
  
}
