/*
 * Copyright (c) 2012 LinkedIn, Inc
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

package cleo.search.selector;

import cleo.search.Element;

/**
 * StrictPrefixSelector does prefix match by preserving the same ordering shared by query terms and element terms.
 * 
 * <pre>
 *    Element Terms: 'open', 'source', 'software', 'distributed', 'systems' 
 *    
 *    Query 1 Terms: 'soft', 'system'
 *    
 *    Query 2 Terms: 'open', 'system'
 *    
 *    Query 3 Terms: 'system', 'soft'
 * </pre>
 * 
 * <p>
 * Query 1 can match the specified element because the query terms are in order with element terms: <code>'software'</code>, <code>'systems'</code>.
 * </p>
 * 
 * <p>
 * Query 1 can match the specified element because the query terms are in order with element terms: <code>'open'</code>, <code>'systems'</code>.
 * </p>
 * 
 * <p>
 * Query 3 cannot match the specified element as the query terms are in reverse order with element terms: <code>'software'</code>, <code>'systems'</code>.
 * </p>
 * 
 * @author jwu
 * @since 02/16, 2012
 * 
 */
public class StrictPrefixSelector<E extends Element> implements Selector<E> {
  private static final long serialVersionUID = 1L;
  
  /**
   * The query terms.
   */
  protected final String[] queryTerms;
  
  /**
   * Creates a new instance of StrictPrefixSelector.
   * 
   * @param queryTerms - the query terms (should not contain <code>null</code>).
   */
  public StrictPrefixSelector(String... queryTerms) {
    this.queryTerms = queryTerms;
  }
  
  @Override
  public boolean select(E element, SelectorContext ctx) {
    final String[] elemTerms = element.getTerms();
    if (elemTerms == null) return false;
    
    final int length = elemTerms.length;
    
    /**
     * The index i increase while terms in the query are looped.
     */
    int i = 0;
    
    for(String prefix : queryTerms) {
      for(; i < length; i++) {
        if(elemTerms[i].startsWith(prefix)) {
          // Do not increase i to handle cases where the same query term is repeated in adjacency such as: 'open', 'system', 'system'
          break;
        }
      }
      
      // Query terms cannot be matched strictly in order with query terms.
      if(i == length) return false;
    }
    
    // Set the matching score in the context
    ctx.setScore((double)queryTerms.length / (length + i));
    
    return true;
  }
}
