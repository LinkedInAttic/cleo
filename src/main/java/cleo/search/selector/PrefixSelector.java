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

package cleo.search.selector;

import cleo.search.Element;

/**
 * PrefixSelector
 * 
 * @author jwu
 * @since 01/12, 2011
 * 
 * @param <E> Typeahead Element
 */
public class PrefixSelector<E extends Element> implements Selector<E> {
  private static final long serialVersionUID = 1L;
  
  protected final String[] queryTerms;
  
  public PrefixSelector(String... queryTerms) {
    this.queryTerms = queryTerms;
  }
  
  @Override
  public boolean select(E element, SelectorContext ctx) {
    final String[] elemTerms = element.getTerms();
    if (elemTerms == null) return false;
    final int length = elemTerms.length;
    int i = 0;
    
    for(String prefix : queryTerms) {
      for(i = 0; i < length; i++) {
        if(elemTerms[i].startsWith(prefix)) {
          break;
        }
      }
      
      // Query term cannot be matched
      if(i == length) return false;
    }
    
    return true;
  }
}
