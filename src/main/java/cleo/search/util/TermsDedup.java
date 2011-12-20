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

import java.util.HashSet;
import java.util.Set;


/**
 * TermsDedup
 * 
 * @author jwu
 * @since 02/17, 2011
 */
public class TermsDedup implements TermsHandler {
  private final Set<String> termSet = new HashSet<String>();
  
  @Override
  public String[] handle(int source, String[] terms) throws Exception {
    termSet.clear();
    
    for(String term : terms) {
      if(term != null) {
        termSet.add(term);
      }
    }
    
    int size = termSet.size();
    if(size < terms.length) {
      int i = 0;
      String[] results = new String[size];
      
      // preserve the original order of terms
      for(String term : terms) {
        if(termSet.remove(term)) {
          if(i < results.length) {
            results[i++] = term;
          }
        }
      }
      
      return results;
    } else {
      return terms;
    }
  }
}
