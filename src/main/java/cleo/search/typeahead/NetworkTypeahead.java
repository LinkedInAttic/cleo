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

import cleo.search.Element;
import cleo.search.collector.Collector;
import cleo.search.util.Range;

/**
 * NetworkTypeahead
 * 
 * @author jwu
 * @since 04/28, 2011
 */
public interface NetworkTypeahead<E extends Element> extends Typeahead<E> {
  
  /**
   * @return the range managed by this NetworkTypeahead.
   */
  public Range getRange();
  
  /**
   * Creates a network typeahead search context for a searcher.
   * 
   * @param uid       - Searcher Id
   * @return the network typeahead search context for a searcher.
   */
  public NetworkTypeaheadContext createContext(int uid);
  
  /**
   * Search for elements matching given search terms using a collector within a specified network typeahead context.
   * The elements to search are from the first and second degree connections of the source in the specified context.
   * 
   * It is not required that the searcher (i.e. <code>uid</code>) is the same as the source in the context.
   * This means that a member (searcher) can search another member's network connections.
   * 
   * @param uid       - Searcher Id
   * @param terms     - Search terms
   * @param collector - Element collector
   * @param context   - Network typeahead context
   *                    which contains a member's first degree connections (or connection strengths).
   *                    The member may or may not be the searcher.
   * @return the element collector which may be the original collector or a newly created collector. 
   */
  public Collector<E> searchNetwork(int uid, String[] terms, Collector<E> collector, NetworkTypeaheadContext context);
}
